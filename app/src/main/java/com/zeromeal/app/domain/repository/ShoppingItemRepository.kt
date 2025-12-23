package com.zeromeal.app.domain.repository

import com.zeromeal.app.domain.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

interface ShoppingItemRepository {
    fun getAllShoppingItems(): Flow<List<ShoppingItem>>
    suspend fun getShoppingItemById(id: String): ShoppingItem?
    fun getTotalCount(): Flow<Int>
    fun getPurchasedCount(): Flow<Int>
    suspend fun insertShoppingItem(item: ShoppingItem)
    suspend fun updateShoppingItem(item: ShoppingItem)
    suspend fun deleteShoppingItem(item: ShoppingItem)
    suspend fun deleteShoppingItemById(id: String)
}

