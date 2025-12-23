package com.zeromeal.app.data.repository

import com.zeromeal.app.data.local.dao.RecipeDao
import com.zeromeal.app.data.mapper.toDomain
import com.zeromeal.app.data.mapper.toEntity
import com.zeromeal.app.domain.model.Recipe
import com.zeromeal.app.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val recipeDao: RecipeDao
) : RecipeRepository {
    override fun getAllRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipes().map { entities ->
            entities.mapNotNull { entity ->
                try {
                    entity.toDomain()
                } catch (e: Exception) {
                    null // Skip corrupted records
                }
            }
        }
    }

    override suspend fun getRecipeById(id: String): Recipe? {
        return try {
            recipeDao.getRecipeById(id)?.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    override fun searchRecipes(query: String): Flow<List<Recipe>> {
        return recipeDao.searchRecipes(query).map { entities ->
            entities.mapNotNull { entity ->
                try {
                    entity.toDomain()
                } catch (e: Exception) {
                    null // Skip corrupted records
                }
            }
        }
    }

    override fun getAvailableIngredientRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAvailableIngredientRecipes().map { entities ->
            entities.mapNotNull { entity ->
                try {
                    entity.toDomain()
                } catch (e: Exception) {
                    null // Skip corrupted records
                }
            }
        }
    }

    override fun getRecommendedRecipes(limit: Int): Flow<List<Recipe>> {
        return recipeDao.getRecommendedRecipes(limit).map { entities ->
            entities.mapNotNull { entity ->
                try {
                    entity.toDomain()
                } catch (e: Exception) {
                    null // Skip corrupted records
                }
            }
        }
    }

    override suspend fun insertRecipe(recipe: Recipe) {
        recipeDao.insertRecipe(recipe.toEntity())
    }

    override suspend fun insertRecipes(recipes: List<Recipe>) {
        recipeDao.insertRecipes(recipes.map { it.toEntity() })
    }
}
