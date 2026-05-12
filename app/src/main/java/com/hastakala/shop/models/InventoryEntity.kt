package com.hastakala.shop.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory")
data class InventoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val productName: String,
    val color: String,
    val quantity: Int,
    val reorderLevel: Int = 3,
    val imageRes: Int
)
