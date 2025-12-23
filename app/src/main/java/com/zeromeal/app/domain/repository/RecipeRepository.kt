package com.zeromeal.app.domain.repository

import com.zeromeal.app.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    fun getAllRecipes(): Flow<List<Recipe>>
    suspend fun getRecipeById(id: String): Recipe?
    fun searchRecipes(query: String): Flow<List<Recipe>>
    fun getAvailableIngredientRecipes(): Flow<List<Recipe>>
    fun getRecommendedRecipes(limit: Int = 10): Flow<List<Recipe>>
    suspend fun insertRecipe(recipe: Recipe)
    suspend fun insertRecipes(recipes: List<Recipe>)
}

