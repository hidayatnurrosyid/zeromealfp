package com.zeromeal.app.data.local.dao

import androidx.room.*
import com.zeromeal.app.data.local.entity.ShoppingItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingItemDao {
    @Query("SELECT * FROM shopping_items ORDER BY createdAt DESC")
    fun getAllShoppingItems(): Flow<List<ShoppingItemEntity>>

    @Query("SELECT * FROM shopping_items WHERE id = :id")
    suspend fun getShoppingItemById(id: String): ShoppingItemEntity?

    @Query("SELECT COUNT(*) FROM shopping_items")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM shopping_items WHERE isChecked = 1")
    fun getPurchasedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingItem(item: ShoppingItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingItems(items: List<ShoppingItemEntity>)

    @Update
    suspend fun updateShoppingItem(item: ShoppingItemEntity)

    @Delete
    suspend fun deleteShoppingItem(item: ShoppingItemEntity)

    @Query("DELETE FROM shopping_items WHERE id = :id")
    suspend fun deleteShoppingItemById(id: String)

    @Query("DELETE FROM shopping_items")
    suspend fun deleteAllShoppingItems()
}

