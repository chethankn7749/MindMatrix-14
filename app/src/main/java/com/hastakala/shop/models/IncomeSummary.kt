package com.hastakala.shop.models

data class IncomeSummary(
    val totalRevenue: Double = 0.0,
    val totalSales: Int = 0,
    val productWiseEarnings: Map<String, Double> = emptyMap(),
    val transactions: List<SaleEntity> = emptyList()
)
