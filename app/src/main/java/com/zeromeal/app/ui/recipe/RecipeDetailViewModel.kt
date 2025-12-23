package com.zeromeal.app.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeromeal.app.data.mapper.toRecipe
import com.zeromeal.app.domain.model.Recipe
import com.zeromeal.app.domain.repository.FavoriteRecipeRepository
import com.zeromeal.app.domain.repository.ResepLaravelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val recommendedRecipes: List<Recipe> = emptyList(),
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val userRating: Int = 0,
    val hasSubmittedRating: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val favoriteRecipeRepository: FavoriteRecipeRepository,
    private val resepLaravelRepository: ResepLaravelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    private var currentRecipeId: String? = null

    fun loadRecipe(recipeId: String) {
        currentRecipeId = recipeId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Refresh resep dari Laravel
                try {
                    resepLaravelRepository.refreshResep()
                } catch (e: Exception) {
                    // Ignore refresh error, continue with cached data
                    android.util.Log.e("RecipeDetail", "Refresh failed: ${e.message}")
                }
                
                // Ambil resep by ID dari Laravel
                val resepIdInt = recipeId.toIntOrNull()
                val resepDto = if (resepIdInt != null) {
                    resepLaravelRepository.getResepById(resepIdInt)
                } else {
                    // Coba cari di cache berdasarkan string ID
                    resepLaravelRepository.getAllResep().first().find { 
                        it.resepId.toString() == recipeId 
                    }
                }
                
                if (resepDto == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Resep dengan ID $recipeId tidak ditemukan. Pastikan server Laravel berjalan."
                        )
                    }
                    return@launch
                }
                
                val recipe = resepDto.toRecipe()
                
                // Ambil semua resep untuk rekomendasi
                val allResep = resepLaravelRepository.getAllResep().first()
                val recommended = allResep
                    .filter { it.resepId.toString() != recipeId }
                    .take(4)
                    .map { it.toRecipe() }
                
                val isFavorite = favoriteRecipeRepository.isFavorite(recipeId).firstOrNull() ?: false

                _uiState.update {
                    it.copy(
                        recipe = recipe,
                        recommendedRecipes = recommended,
                        isFavorite = isFavorite,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("RecipeDetail", "Error: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Gagal mengambil data resep: ${e.message}"
                    )
                }
            }
        }
    }

    fun toggleFavorite() {
        val recipe = _uiState.value.recipe ?: return
        viewModelScope.launch {
            val newFavoriteStatus = favoriteRecipeRepository.toggleFavorite(recipe)
            _uiState.update { it.copy(isFavorite = newFavoriteStatus) }
        }
    }

    fun onUserRatingChange(rating: Int) {
        _uiState.update { it.copy(userRating = rating) }
    }

    fun submitRating() {
        val currentRating = _uiState.value.userRating
        if (currentRating > 0) {
            _uiState.update { it.copy(hasSubmittedRating = true) }
            // TODO: Save rating to backend/database
        }
    }
}
