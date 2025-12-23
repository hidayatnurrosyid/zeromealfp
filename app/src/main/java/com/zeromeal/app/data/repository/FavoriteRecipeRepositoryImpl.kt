package com.zeromeal.app.data.repository

import com.zeromeal.app.data.local.dao.FavoriteRecipeDao
import com.zeromeal.app.data.local.entity.FavoriteRecipeEntity
import com.zeromeal.app.domain.model.Recipe
import com.zeromeal.app.domain.repository.FavoriteRecipeInfo
import com.zeromeal.app.domain.repository.FavoriteRecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteRecipeRepositoryImpl @Inject constructor(
    private val favoriteRecipeDao: FavoriteRecipeDao
) : FavoriteRecipeRepository {

    override fun getAllFavorites(): Flow<List<FavoriteRecipeInfo>> {
        return favoriteRecipeDao.getAllFavorites().map { favorites ->
            favorites.map { entity ->
                FavoriteRecipeInfo(
                    recipeId = entity.recipeId,
                    recipeName = entity.recipeName,
                    imageUrl = entity.imageUrl,
                    cookingTime = entity.cookingTime,
                    rating = entity.rating,
                    difficulty = entity.difficulty
                )
            }
        }
    }

    override fun isFavorite(recipeId: String): Flow<Boolean> {
        return favoriteRecipeDao.isFavorite(recipeId)
    }

    override suspend fun addFavorite(recipe: Recipe) {
        favoriteRecipeDao.insertFavorite(
            FavoriteRecipeEntity(
                recipeId = recipe.id,
                recipeName = recipe.name,
                imageUrl = recipe.imageUrl,
                cookingTime = recipe.cookingTime,
                rating = recipe.rating,
                difficulty = recipe.difficulty ?: ""
            )
        )
    }

    override suspend fun removeFavorite(recipeId: String) {
        favoriteRecipeDao.deleteFavorite(recipeId)
    }

    override suspend fun toggleFavorite(recipe: Recipe): Boolean {
        val existing = favoriteRecipeDao.getFavoriteById(recipe.id)
        return if (existing != null) {
            favoriteRecipeDao.deleteFavorite(recipe.id)
            false
        } else {
            addFavorite(recipe)
            true
        }
    }
}
