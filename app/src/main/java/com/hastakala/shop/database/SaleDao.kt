package com.hastakala.shop.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hastakala.shop.models.SaleEntity

@Dao
interface SaleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: SaleEntity)

    @Query("SELECT * FROM sales ORDER BY createdAt DESC")
    fun observeSales(): LiveData<List<SaleEntity>>
}
