package com.hastakala.shop.models

data class DashboardSummary(
    val weeklyIncome: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val bestSellingProduct: String = "No sales yet",
    val lowStockCount: Int = 0,
    val salesSummary: String = "No sales recorded",
    val totalProductsSold: Int = 0
)
