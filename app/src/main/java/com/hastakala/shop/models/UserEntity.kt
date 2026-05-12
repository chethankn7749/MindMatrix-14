package com.hastakala.shop.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
    val businessName: String = "Hasta-Kala Shop"
)
