package com.zeromeal.app.data.local.dao

import androidx.room.*
import com.zeromeal.app.data.local.entity.FoodItemEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface FoodItemDao {
    @Query("SELECT * FROM food_items WHERE isFinished = 0 ORDER BY expirationDate ASC")
    fun getAllFoodItems(): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM food_items WHERE id = :id")
    suspend fun getFoodItemById(id: String): FoodItemEntity?

    @Query("SELECT * FROM food_items WHERE isFinished = 0 AND expirationDate <= :date ORDER BY expirationDate ASC")
    fun getExpiringItems(date: LocalDate): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM food_items WHERE isFinished = 0 AND name LIKE '%' || :query || '%' ORDER BY expirationDate ASC")
    fun searchFoodItems(query: String): Flow<List<FoodItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItem(foodItem: FoodItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItems(foodItems: List<FoodItemEntity>)

    @Update
    suspend fun updateFoodItem(foodItem: FoodItemEntity)

    @Delete
    suspend fun deleteFoodItem(foodItem: FoodItemEntity)

    @Query("DELETE FROM food_items WHERE id = :id")
    suspend fun deleteFoodItemById(id: String)
}

