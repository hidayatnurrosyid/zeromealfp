package com.zeromeal.app.domain.repository

import com.zeromeal.app.domain.model.FoodItem
import kotlinx.coroutines.flow.Flow

interface FoodItemRepository {
    fun getAllFoodItems(): Flow<List<FoodItem>>
    suspend fun getFoodItemById(id: String): FoodItem?
    fun getExpiringItems(days: Int): Flow<List<FoodItem>>
    fun searchFoodItems(query: String): Flow<List<FoodItem>>
    suspend fun insertFoodItem(foodItem: FoodItem)
    suspend fun updateFoodItem(foodItem: FoodItem)
    suspend fun deleteFoodItem(foodItem: FoodItem)
    suspend fun deleteFoodItemById(id: String)
}

