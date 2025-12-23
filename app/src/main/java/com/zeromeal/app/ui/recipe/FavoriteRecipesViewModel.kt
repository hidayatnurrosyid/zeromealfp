package com.zeromeal.app.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeromeal.app.domain.model.Recipe
import com.zeromeal.app.domain.repository.FavoriteRecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoriteRecipesUiState(
    val favoriteRecipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class FavoriteRecipesViewModel @Inject constructor(
    private val favoriteRecipeRepository: FavoriteRecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoriteRecipesUiState())
    val uiState: StateFlow<FavoriteRecipesUiState> = _uiState.asStateFlow()

    init {
        loadFavoriteRecipes()
    }

    private fun loadFavoriteRecipes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            favoriteRecipeRepository.getAllFavorites().collect { favoriteInfoList ->
                val recipes = favoriteInfoList.map { info ->
                    Recipe(
                        id = info.recipeId,
                        name = info.recipeName,
                        imageUrl = info.imageUrl,
                        cookingTime = info.cookingTime,
                        rating = info.rating,
                        difficulty = info.difficulty,
                        mealType = "",
                        description = "",
                        ingredients = emptyList(),
                        instructions = emptyList()
                    )
                }
                _uiState.update {
                    it.copy(
                        favoriteRecipes = recipes,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun removeFavorite(recipeId: String) {
        viewModelScope.launch {
            favoriteRecipeRepository.removeFavorite(recipeId)
        }
    }
}

