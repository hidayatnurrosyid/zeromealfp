package com.zeromeal.app.data.repository

import com.zeromeal.app.data.local.dao.ShoppingItemDao
import com.zeromeal.app.data.mapper.toDomain
import com.zeromeal.app.data.mapper.toEntity
import com.zeromeal.app.domain.model.ShoppingItem
import com.zeromeal.app.domain.repository.ShoppingItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ShoppingItemRepositoryImpl @Inject constructor(
    private val shoppingItemDao: ShoppingItemDao
) : ShoppingItemRepository {
    override fun getAllShoppingItems(): Flow<List<ShoppingItem>> {
        return shoppingItemDao.getAllShoppingItems().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getShoppingItemById(id: String): ShoppingItem? {
        return shoppingItemDao.getShoppingItemById(id)?.toDomain()
    }

    override fun getTotalCount(): Flow<Int> {
        return shoppingItemDao.getTotalCount()
    }

    override fun getPurchasedCount(): Flow<Int> {
        return shoppingItemDao.getPurchasedCount()
    }

    override suspend fun insertShoppingItem(item: ShoppingItem) {
        shoppingItemDao.insertShoppingItem(item.toEntity())
    }

    override suspend fun updateShoppingItem(item: ShoppingItem) {
        shoppingItemDao.updateShoppingItem(item.toEntity())
    }

    override suspend fun deleteShoppingItem(item: ShoppingItem) {
        shoppingItemDao.deleteShoppingItem(item.toEntity())
    }

    override suspend fun deleteShoppingItemById(id: String) {
        shoppingItemDao.deleteShoppingItemById(id)
    }
}

