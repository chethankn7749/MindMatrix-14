package com.hastakala.shop.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hastakala.shop.models.InventoryEntity

@Dao
interface InventoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<InventoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: InventoryEntity)

    @Update
    suspend fun update(item: InventoryEntity)

    @Query("DELETE FROM inventory WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM inventory ORDER BY productName ASC, color ASC")
    fun observeInventory(): LiveData<List<InventoryEntity>>

    @Query("SELECT * FROM inventory WHERE productId = :productId ORDER BY color ASC")
    suspend fun getByProduct(productId: Int): List<InventoryEntity>
}
