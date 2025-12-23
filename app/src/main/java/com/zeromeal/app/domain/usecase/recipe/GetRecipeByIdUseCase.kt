package com.zeromeal.app.domain.usecase.recipe

import com.zeromeal.app.domain.model.Recipe
import com.zeromeal.app.domain.repository.RecipeRepository
import javax.inject.Inject

class GetRecipeByIdUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(id: String): Recipe? {
        return repository.getRecipeById(id)
    }
}

