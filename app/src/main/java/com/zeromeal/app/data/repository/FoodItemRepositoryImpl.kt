package com.zeromeal.app.data.repository

import com.zeromeal.app.data.local.dao.FoodItemDao
import com.zeromeal.app.data.mapper.toDomain
import com.zeromeal.app.data.mapper.toEntity
import com.zeromeal.app.domain.model.FoodItem
import com.zeromeal.app.domain.repository.FoodItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class FoodItemRepositoryImpl @Inject constructor(
    private val foodItemDao: FoodItemDao
) : FoodItemRepository {
    override fun getAllFoodItems(): Flow<List<FoodItem>> {
        return foodItemDao.getAllFoodItems().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getFoodItemById(id: String): FoodItem? {
        return foodItemDao.getFoodItemById(id)?.toDomain()
    }

    override fun getExpiringItems(days: Int): Flow<List<FoodItem>> {
        val targetDate = LocalDate.now().plusDays(days.toLong())
        return foodItemDao.getExpiringItems(targetDate).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchFoodItems(query: String): Flow<List<FoodItem>> {
        return foodItemDao.searchFoodItems(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertFoodItem(foodItem: FoodItem) {
        foodItemDao.insertFoodItem(foodItem.toEntity())
    }

    override suspend fun updateFoodItem(foodItem: FoodItem) {
        foodItemDao.updateFoodItem(foodItem.toEntity())
    }

    override suspend fun deleteFoodItem(foodItem: FoodItem) {
        foodItemDao.deleteFoodItem(foodItem.toEntity())
    }

    override suspend fun deleteFoodItemById(id: String) {
        foodItemDao.deleteFoodItemById(id)
    }
}

