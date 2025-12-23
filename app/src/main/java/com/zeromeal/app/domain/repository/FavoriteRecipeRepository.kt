package com.zeromeal.app.domain.repository

import com.zeromeal.app.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

data class FavoriteRecipeInfo(
    val recipeId: String,
    val recipeName: String,
    val imageUrl: String?,
    val cookingTime: Int,
    val rating: Double,
    val difficulty: String
)

interface FavoriteRecipeRepository {
    fun getAllFavorites(): Flow<List<FavoriteRecipeInfo>>
    fun isFavorite(recipeId: String): Flow<Boolean>
    suspend fun addFavorite(recipe: Recipe)
    suspend fun removeFavorite(recipeId: String)
    suspend fun toggleFavorite(recipe: Recipe): Boolean
}
