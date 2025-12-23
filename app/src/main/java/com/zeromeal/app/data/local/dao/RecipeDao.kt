package com.zeromeal.app.data.local.dao

import androidx.room.*
import com.zeromeal.app.data.local.entity.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY rating DESC")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: String): RecipeEntity?

    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :query || '%' ORDER BY rating DESC")
    fun searchRecipes(query: String): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE ingredientAvailability = 'tersedia' ORDER BY rating DESC")
    fun getAvailableIngredientRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes ORDER BY rating DESC LIMIT :limit")
    fun getRecommendedRecipes(limit: Int = 10): Flow<List<RecipeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)

    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)
}

