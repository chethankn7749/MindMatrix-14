package com.hastakala.shop.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hastakala.shop.models.ProductEntity

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Query("SELECT * FROM products WHERE lower(name) = lower(:name) LIMIT 1")
    suspend fun getByName(name: String): ProductEntity?

    @Query("SELECT * FROM products ORDER BY name ASC")
    fun observeProducts(): LiveData<List<ProductEntity>>
}
