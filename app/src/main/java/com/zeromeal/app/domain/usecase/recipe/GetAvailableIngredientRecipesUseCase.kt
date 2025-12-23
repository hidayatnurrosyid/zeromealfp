package com.zeromeal.app.domain.usecase.recipe

import com.zeromeal.app.domain.model.Recipe
import com.zeromeal.app.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAvailableIngredientRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(): Flow<List<Recipe>> {
        return repository.getAvailableIngredientRecipes()
    }
}

