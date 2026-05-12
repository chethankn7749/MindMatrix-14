package com.hastakala.shop.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.hastakala.shop.models.AnalyticsBundle
import com.hastakala.shop.models.DashboardSummary
import com.hastakala.shop.models.IncomeFilter
import com.hastakala.shop.models.IncomeSummary
import com.hastakala.shop.models.InventoryEntity
import com.hastakala.shop.models.ProductEntity
import com.hastakala.shop.models.SaleEntity
import com.hastakala.shop.repositories.SalesRepository
import kotlinx.coroutines.launch

class AppViewModel(private val repository: SalesRepository) : ViewModel() {

    val products: LiveData<List<ProductEntity>> = repository.observeProducts()
    val sales: LiveData<List<SaleEntity>> = repository.observeSales()
    val inventory: LiveData<List<InventoryEntity>> = repository.observeInventory()
    val dashboard: LiveData<DashboardSummary> = repository.observeDashboard()
    val analytics: LiveData<AnalyticsBundle> = repository.observeAnalytics()

    private val incomeFilter = MutableLiveData(IncomeFilter.THIS_WEEK)
    val incomeSummary: LiveData<IncomeSummary> = incomeFilter.switchMap { filter ->
        sales.map { repository.buildIncomeSummary(filter, it) }
    }

    private val _events = MutableLiveData<String>()
    val events: LiveData<String> = _events

    fun setIncomeFilter(filter: IncomeFilter) {
        incomeFilter.value = filter
    }

    fun recordSale(product: ProductEntity, color: String, quantity: Int) {
        viewModelScope.launch {
            repository.saveSale(product, color, quantity)
                .onSuccess { _events.value = "Sale saved successfully" }
                .onFailure { _events.value = it.message ?: "Unable to save sale" }
        }
    }

    fun addStock(item: InventoryEntity) {
        viewModelScope.launch {
            repository.addStock(item)
            _events.value = "Stock added"
        }
    }

    fun updateStock(item: InventoryEntity) {
        viewModelScope.launch {
            repository.updateStock(item)
            _events.value = "Stock updated"
        }
    }

    fun deleteStock(id: Int) {
        viewModelScope.launch {
            repository.deleteStock(id)
            _events.value = "Stock removed"
        }
    }

    fun saveStockEntry(
        existingItemId: Int?,
        productName: String,
        category: String,
        basePrice: Double,
        color: String,
        quantity: Int,
        reorderLevel: Int
    ) {
        viewModelScope.launch {
            repository.saveStockEntry(
                existingItemId = existingItemId,
                productName = productName,
                category = category,
                basePrice = basePrice,
                color = color,
                quantity = quantity,
                reorderLevel = reorderLevel
            )
            _events.value = if (existingItemId == null) "Stock added" else "Stock updated"
        }
    }
}
