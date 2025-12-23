package com.zeromeal.app.data.local.dao

import androidx.room.*
import com.zeromeal.app.data.local.entity.FavoriteRecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteRecipeDao {
    @Query("SELECT * FROM favorite_recipes ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteRecipeEntity>>

    @Query("SELECT * FROM favorite_recipes WHERE recipeId = :recipeId")
    suspend fun getFavoriteById(recipeId: String): FavoriteRecipeEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_recipes WHERE recipeId = :recipeId)")
    fun isFavorite(recipeId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteRecipeEntity)

    @Query("DELETE FROM favorite_recipes WHERE recipeId = :recipeId")
    suspend fun deleteFavorite(recipeId: String)
}
