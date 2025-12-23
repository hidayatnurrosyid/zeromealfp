package com.zeromeal.app.domain.usecase.recipe

import com.zeromeal.app.domain.model.Recipe
import com.zeromeal.app.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecommendedRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(limit: Int = 10): Flow<List<Recipe>> {
        return repository.getRecommendedRecipes(limit)
    }
}

