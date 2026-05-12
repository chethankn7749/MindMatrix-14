package com.hastakala.shop.models

data class AnalyticsBundle(
    val productSales: Map<String, Float> = emptyMap(),
    val colorSales: Map<String, Float> = emptyMap(),
    val weeklyRevenue: List<Float> = List(7) { 0f },
    val monthlyRevenue: List<Float> = List(4) { 0f },
    val productPerformance: Map<String, Float> = emptyMap()
)
