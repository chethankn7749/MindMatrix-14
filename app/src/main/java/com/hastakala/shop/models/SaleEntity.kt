package com.hastakala.shop.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val productName: String,
    val color: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalAmount: Double,
    val createdAt: Long = System.currentTimeMillis()
)
