package com.hastakala.shop.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.hastakala.shop.database.InventoryDao
import com.hastakala.shop.database.ProductDao
import com.hastakala.shop.database.SaleDao
import com.hastakala.shop.models.AnalyticsBundle
import com.hastakala.shop.models.DashboardSummary
import com.hastakala.shop.models.IncomeFilter
import com.hastakala.shop.models.IncomeSummary
import com.hastakala.shop.models.InventoryEntity
import com.hastakala.shop.models.ProductEntity
import com.hastakala.shop.models.SaleEntity
import com.hastakala.shop.utils.ProductVisuals
import java.util.Calendar

class SalesRepository(
    private val productDao: ProductDao,
    private val saleDao: SaleDao,
    private val inventoryDao: InventoryDao
) {
    fun observeProducts(): LiveData<List<ProductEntity>> = productDao.observeProducts()
    fun observeSales(): LiveData<List<SaleEntity>> = saleDao.observeSales()
    fun observeInventory(): LiveData<List<InventoryEntity>> = inventoryDao.observeInventory()

    fun observeDashboard(): LiveData<DashboardSummary> {
        val output = MediatorLiveData<DashboardSummary>()
        var sales: List<SaleEntity> = emptyList()
        var inventory: List<InventoryEntity> = emptyList()

        fun recalc() {
            val now = System.currentTimeMillis()
            val weekStart = now - 7L * 24L * 60L * 60L * 1000L
            val monthStart = now - 30L * 24L * 60L * 60L * 1000L
            val weeklySales = sales.filter { it.createdAt >= weekStart }
            val monthlySales = sales.filter { it.createdAt >= monthStart }
            val bestseller = sales.groupBy { it.productName }
                .maxByOrNull { (_, list) -> list.sumOf { it.quantity } }
                ?.key ?: "No sales yet"
            val lowStock = inventory.count { it.quantity <= maxOf(it.reorderLevel, 3) }
            output.value = DashboardSummary(
                weeklyIncome = weeklySales.sumOf { it.totalAmount },
                monthlyIncome = monthlySales.sumOf { it.totalAmount },
                bestSellingProduct = bestseller,
                lowStockCount = lowStock,
                salesSummary = if (sales.isEmpty()) "Start billing to view trends" else "${sales.size} total sales recorded",
                totalProductsSold = sales.sumOf { it.quantity }
            )
        }

        output.addSource(observeSales()) {
            sales = it
            recalc()
        }
        output.addSource(observeInventory()) {
            inventory = it
            recalc()
        }
        return output
    }

    fun observeAnalytics(): LiveData<AnalyticsBundle> {
        val output = MediatorLiveData<AnalyticsBundle>()
        output.addSource(observeSales()) { sales ->
            val productSales = sales.groupBy { it.productName }
                .mapValues { (_, value) -> value.sumOf { it.quantity }.toFloat() }
            val colorSales = sales.groupBy { it.color }
                .mapValues { (_, value) -> value.sumOf { it.quantity }.toFloat() }
            val weeklyRevenue = MutableList(7) { 0f }
            val monthlyRevenue = MutableList(4) { 0f }
            val calendar = Calendar.getInstance()
            sales.forEach { sale ->
                calendar.timeInMillis = sale.createdAt
                val dayIndex = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
                weeklyRevenue[dayIndex] += sale.totalAmount.toFloat()
                val weekIndex = (calendar.get(Calendar.WEEK_OF_MONTH) - 1).coerceIn(0, 3)
                monthlyRevenue[weekIndex] += sale.totalAmount.toFloat()
            }
            output.value = AnalyticsBundle(
                productSales = productSales,
                colorSales = colorSales,
                weeklyRevenue = weeklyRevenue,
                monthlyRevenue = monthlyRevenue,
                productPerformance = productSales
            )
        }
        return output
    }

    suspend fun saveSale(
        product: ProductEntity,
        color: String,
        quantity: Int
    ): Result<Unit> = runCatching {
        val stock = inventoryDao.getByProduct(product.id).firstOrNull { it.color == color }
            ?: throw IllegalStateException("Stock not found for ${product.name} in $color")
        if (stock.quantity < quantity) throw IllegalArgumentException("Only ${stock.quantity} $color items left")

        saleDao.insertSale(
            SaleEntity(
                productId = product.id,
                productName = product.name,
                color = color,
                quantity = quantity,
                unitPrice = product.basePrice,
                totalAmount = product.basePrice * quantity
            )
        )
        inventoryDao.update(stock.copy(quantity = stock.quantity - quantity))
    }

    suspend fun addStock(item: InventoryEntity) = inventoryDao.insert(item)
    suspend fun updateStock(item: InventoryEntity) = inventoryDao.update(item)
    suspend fun deleteStock(id: Int) = inventoryDao.delete(id)

    suspend fun saveStockEntry(
        existingItemId: Int?,
        productName: String,
        category: String,
        basePrice: Double,
        color: String,
        quantity: Int,
        reorderLevel: Int
    ) {
        val currentProduct = productDao.getByName(productName)
        val product = currentProduct ?: ProductEntity(
            name = productName,
            category = category,
            basePrice = basePrice,
            imageRes = ProductVisuals.iconFor(productName, category),
            featuredColor = color
        ).let { created ->
            val newId = productDao.insert(created).toInt()
            created.copy(id = newId)
        }

        val item = InventoryEntity(
            id = existingItemId ?: 0,
            productId = product.id,
            productName = product.name,
            color = color,
            quantity = quantity,
            reorderLevel = reorderLevel,
            imageRes = product.imageRes
        )
        if (existingItemId == null) {
            inventoryDao.insert(item)
        } else {
            inventoryDao.update(item)
        }
    }

    fun buildIncomeSummary(filter: IncomeFilter, sales: List<SaleEntity>): IncomeSummary {
        val startTime = when (filter) {
            IncomeFilter.TODAY -> startOfToday()
            IncomeFilter.THIS_WEEK -> System.currentTimeMillis() - 7L * 24L * 60L * 60L * 1000L
            IncomeFilter.THIS_MONTH -> System.currentTimeMillis() - 30L * 24L * 60L * 60L * 1000L
        }
        val filtered = sales.filter { it.createdAt >= startTime }
        return IncomeSummary(
            totalRevenue = filtered.sumOf { it.totalAmount },
            totalSales = filtered.size,
            productWiseEarnings = filtered.groupBy { it.productName }
                .mapValues { (_, value) -> value.sumOf { it.totalAmount } },
            transactions = filtered
        )
    }

    private fun startOfToday(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
