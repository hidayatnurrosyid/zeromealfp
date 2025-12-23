package com.zeromeal.app.data.remote

import com.zeromeal.app.data.remote.dto.*
import com.zeromeal.app.domain.model.FoodItem
import com.zeromeal.app.domain.model.Recipe
import com.zeromeal.app.domain.model.ShoppingItem
import com.zeromeal.app.domain.model.Notification
import javax.inject.Inject

/**
 * Remote Data Source untuk mengakses API
 * Handle semua komunikasi dengan backend MySQL
 */
class RemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {
    
    // ========== Food Items ==========
    suspend fun getAllFoodItems(): Result<List<FoodItem>> {
        return try {
            val response = apiService.getAllFoodItems()
            if (response.isSuccessful && response.body()?.status == true) {
                val items = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                Result.success(items)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getFoodItemById(id: String): Result<FoodItem?> {
        return try {
            val response = apiService.getFoodItemById(id)
            if (response.isSuccessful && response.body()?.status == true) {
                Result.success(response.body()?.data?.toDomain())
            } else {
                Result.failure(Exception(response.body()?.message ?: "Item not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createFoodItem(item: FoodItem): Result<FoodItem> {
        return try {
            val response = apiService.createFoodItem(FoodItemDto.fromDomain(item))
            if (response.isSuccessful && response.body()?.status == true) {
                Result.success(response.body()?.data?.toDomain() ?: item)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to create item"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateFoodItem(item: FoodItem): Result<FoodItem> {
        return try {
            val response = apiService.updateFoodItem(item.id, FoodItemDto.fromDomain(item))
            if (response.isSuccessful && response.body()?.status == true) {
                Result.success(response.body()?.data?.toDomain() ?: item)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to update item"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteFoodItem(id: String): Result<Unit> {
        return try {
            val response = apiService.deleteFoodItem(id)
            if (response.isSuccessful && response.body()?.status == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to delete item"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getExpiringItems(limit: Int): Result<List<FoodItem>> {
        return try {
            val response = apiService.getExpiringItems(limit)
            if (response.isSuccessful && response.body()?.status == true) {
                val items = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                Result.success(items)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ========== Shopping Items ==========
    suspend fun getAllShoppingItems(): Result<List<ShoppingItem>> {
        return try {
            val response = apiService.getAllShoppingItems()
            if (response.isSuccessful && response.body()?.status == true) {
                val items = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                Result.success(items)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createShoppingItem(item: ShoppingItem): Result<ShoppingItem> {
        return try {
            val response = apiService.createShoppingItem(ShoppingItemDto.fromDomain(item))
            if (response.isSuccessful && response.body()?.status == true) {
                Result.success(response.body()?.data?.toDomain() ?: item)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to create item"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateShoppingItem(item: ShoppingItem): Result<ShoppingItem> {
        return try {
            val response = apiService.updateShoppingItem(item.id, ShoppingItemDto.fromDomain(item))
            if (response.isSuccessful && response.body()?.status == true) {
                Result.success(response.body()?.data?.toDomain() ?: item)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to update item"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteShoppingItem(id: String): Result<Unit> {
        return try {
            val response = apiService.deleteShoppingItem(id)
            if (response.isSuccessful && response.body()?.status == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to delete item"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ========== Recipes ==========
    suspend fun getAllRecipes(): Result<List<Recipe>> {
        return try {
            val response = apiService.getAllRecipes()
            if (response.isSuccessful && response.body()?.status == true) {
                val recipes = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                Result.success(recipes)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getRecipeById(id: String): Result<Recipe?> {
        return try {
            val response = apiService.getRecipeById(id)
            if (response.isSuccessful && response.body()?.status == true) {
                Result.success(response.body()?.data?.toDomain())
            } else {
                Result.failure(Exception(response.body()?.message ?: "Recipe not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getRecommendedRecipes(limit: Int): Result<List<Recipe>> {
        return try {
            val response = apiService.getRecommendedRecipes(limit)
            if (response.isSuccessful && response.body()?.status == true) {
                val recipes = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                Result.success(recipes)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAvailableIngredientRecipes(): Result<List<Recipe>> {
        return try {
            val response = apiService.getAvailableIngredientRecipes()
            if (response.isSuccessful && response.body()?.status == true) {
                val recipes = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                Result.success(recipes)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ========== Notifications ==========
    suspend fun getAllNotifications(): Result<List<Notification>> {
        return try {
            val response = apiService.getAllNotifications()
            if (response.isSuccessful && response.body()?.status == true) {
                val notifications = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                Result.success(notifications)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createNotification(notification: Notification): Result<Notification> {
        return try {
            val response = apiService.createNotification(NotificationDto.fromDomain(notification))
            if (response.isSuccessful && response.body()?.status == true) {
                Result.success(response.body()?.data?.toDomain() ?: notification)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to create notification"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun markNotificationAsRead(id: String): Result<Unit> {
        return try {
            val response = apiService.markNotificationAsRead(id)
            if (response.isSuccessful && response.body()?.status == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to mark as read"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

