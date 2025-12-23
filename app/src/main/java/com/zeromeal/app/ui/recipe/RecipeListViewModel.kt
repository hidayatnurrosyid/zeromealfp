package com.zeromeal.app.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeromeal.app.data.mapper.toRecipeList
import com.zeromeal.app.domain.model.FoodItem
import com.zeromeal.app.domain.model.Recipe
import com.zeromeal.app.domain.repository.ResepLaravelRepository
import com.zeromeal.app.domain.usecase.fooditem.GetAllFoodItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecipeListUiState(
    val availableIngredientRecipes: List<Recipe> = emptyList(),
    val recommendedRecipes: List<Recipe> = emptyList(),
    val inventoryItems: List<FoodItem> = emptyList(),
    val searchQuery: String = "",
    val filteredAvailable: List<Recipe> = emptyList(),
    val filteredRecommended: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class RecipeListViewModel @Inject constructor(
    private val resepLaravelRepository: ResepLaravelRepository,
    private val getAllFoodItemsUseCase: GetAllFoodItemsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeListUiState())
    val uiState: StateFlow<RecipeListUiState> = _uiState.asStateFlow()

    init {
        loadRecipes()
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // Refresh resep dari Laravel
                resepLaravelRepository.refreshResep()
                
                // Combine resep dari Laravel dengan food items
                combine(
                    resepLaravelRepository.getAllResep(),
                    getAllFoodItemsUseCase()
                ) { resepList, foodItems ->
                    // Convert ResepDto ke Recipe
                    val allRecipes = resepList.toRecipeList()
                    
                    // Ambil nama bahan yang dimiliki user
                    val userIngredientNames = foodItems.map { it.name.lowercase() }
                    
                    // Filter resep yang bahannya tersedia (minimal 60%)
                    val availableRecipes = allRecipes.filter { recipe ->
                        if (recipe.ingredients.isEmpty()) return@filter false
                        val matchCount = recipe.ingredients.count { ingredient ->
                            userIngredientNames.any { userIngredient ->
                                ingredient.name.lowercase().contains(userIngredient) ||
                                userIngredient.contains(ingredient.name.lowercase())
                            }
                        }
                        val matchPercentage = (matchCount.toDouble() / recipe.ingredients.size) * 100
                        matchPercentage >= 60.0
                    }
                    
                    // Resep rekomendasi adalah semua resep
                    val recommendedRecipes = allRecipes
                    
                    RecipeListUiState(
                        availableIngredientRecipes = availableRecipes,
                        recommendedRecipes = recommendedRecipes,
                        filteredAvailable = availableRecipes,
                        filteredRecommended = recommendedRecipes,
                        inventoryItems = foodItems,
                        isLoading = false
                    )
                }.collectLatest { state ->
                    _uiState.update { state }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Gagal memuat resep"
                    )
                }
            }
        }
    }
    
    fun refresh() {
        loadRecipes()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            val q = query.lowercase()
            val filteredAvail = if (q.isBlank()) {
                state.availableIngredientRecipes
            } else {
                state.availableIngredientRecipes.filter { it.name.lowercase().contains(q) }
            }
            val filteredRec = if (q.isBlank()) {
                state.recommendedRecipes
            } else {
                state.recommendedRecipes.filter { it.name.lowercase().contains(q) }
            }
            state.copy(
                searchQuery = query,
                filteredAvailable = filteredAvail,
                filteredRecommended = filteredRec
            )
        }
    }
}
