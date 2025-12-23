package com.zeromeal.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeromeal.app.data.mapper.toRecipeList
import com.zeromeal.app.data.remote.dto.ResepDto
import com.zeromeal.app.domain.model.Barang
import com.zeromeal.app.domain.model.FoodItem
import com.zeromeal.app.domain.model.Ingredient
import com.zeromeal.app.domain.model.Notification
import com.zeromeal.app.domain.model.NotificationType
import com.zeromeal.app.domain.model.Recipe
import com.zeromeal.app.domain.model.ShoppingItem
import com.zeromeal.app.domain.usecase.barang.GetSemuaBarangUseCase
import com.zeromeal.app.domain.usecase.barang.RefreshBarangUseCase
import com.zeromeal.app.domain.usecase.fooditem.GetExpiringItemsUseCase
import com.zeromeal.app.domain.usecase.notification.InsertNotificationUseCase
import com.zeromeal.app.domain.usecase.notification.GetAllNotificationsUseCase
import com.zeromeal.app.domain.usecase.recipe.GetRecommendedRecipesUseCase
import com.zeromeal.app.domain.usecase.resep.GetRekomendasiResepUseCase
import com.zeromeal.app.domain.usecase.shopping.GetAllShoppingItemsUseCase
import com.zeromeal.app.domain.usecase.shopping.UpdateShoppingItemUseCase
import com.zeromeal.app.domain.repository.InventarisRepository
import com.zeromeal.app.domain.repository.ResepLaravelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class HomeUiState(
    val searchQuery: String = "",
    val expiringItems: List<FoodItem> = emptyList(),
    val recommendedRecipes: List<Recipe> = emptyList(),
    val shoppingItems: List<ShoppingItem> = emptyList(),
    val filteredExpiringItems: List<FoodItem> = emptyList(),
    val filteredRecipes: List<Recipe> = emptyList(),
    val filteredShoppingItems: List<ShoppingItem> = emptyList(),
    val hasUnreadNotifications: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    // Data dari API Laravel
    val listBarang: List<Barang> = emptyList(),
    val isLoadingBarang: Boolean = false,
    val errorBarang: String? = null,
    // Rekomendasi Resep dari Laravel (matching ≥60%)
    val rekomendasiResep: List<ResepDto> = emptyList(),
    val isLoadingRekomendasi: Boolean = false,
    val errorRekomendasi: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getExpiringItemsUseCase: GetExpiringItemsUseCase,
    private val getRecommendedRecipesUseCase: GetRecommendedRecipesUseCase,
    private val getAllShoppingItemsUseCase: GetAllShoppingItemsUseCase,
    private val updateShoppingItemUseCase: UpdateShoppingItemUseCase,
    private val insertNotificationUseCase: InsertNotificationUseCase,
    private val getAllNotificationsUseCase: GetAllNotificationsUseCase,
    private val getSemuaBarangUseCase: GetSemuaBarangUseCase,
    private val refreshBarangUseCase: RefreshBarangUseCase,
    private val getRekomendasiResepUseCase: GetRekomendasiResepUseCase,
    private val inventarisRepository: InventarisRepository,
    private val resepLaravelRepository: ResepLaravelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
        observeExpiringItemsForNotifications()
        loadBarangFromApi()
        loadRekomendasiResep()
    }

    /**
     * Load rekomendasi resep dari Laravel berdasarkan matching bahan ≥60%
     */
    private fun loadRekomendasiResep() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingRekomendasi = true, errorRekomendasi = null) }
            try {
                // Refresh inventaris dulu
                inventarisRepository.refreshInventaris(1) // TODO: Get actual user ID
                
                // Dapatkan rekomendasi
                val rekomendasi = getRekomendasiResepUseCase(userId = 1, minMatchPercentage = 60.0)
                
                _uiState.update { it.copy(
                    isLoadingRekomendasi = false,
                    rekomendasiResep = rekomendasi
                )}
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoadingRekomendasi = false,
                    errorRekomendasi = e.message ?: "Gagal mengambil rekomendasi resep"
                )}
            }
        }
    }
    
    fun refreshRekomendasi() {
        loadRekomendasiResep()
    }

    /**
     * Load data Barang dari API Laravel
     */
    private fun loadBarangFromApi() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingBarang = true, errorBarang = null) }
            try {
                // Refresh data dari API
                refreshBarangUseCase()
                // Observe data dari repository
                getSemuaBarangUseCase().collect { barangList ->
                    _uiState.update { it.copy(
                        isLoadingBarang = false,
                        listBarang = barangList
                    )}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoadingBarang = false,
                    errorBarang = e.message ?: "Gagal mengambil data barang dari server"
                )}
            }
        }
    }

    fun refreshBarang() {
        loadBarangFromApi()
    }

    private fun observeExpiringItemsForNotifications() {
        viewModelScope.launch {
            try {
                getExpiringItemsUseCase(3).collect { items ->
                    generateExpiryNotifications(items)
                }
            } catch (e: Exception) {
                // Silently ignore errors in notification generation
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            // Refresh resep dari Laravel dulu
            // Refresh resep dari Laravel secara parallel agar tidak blocking UI
            launch {
                try {
                    resepLaravelRepository.refreshResep()
                } catch (e: Exception) {
                    // Ignore refresh error, continue with local data
                }
            }
            
            combine(
                getExpiringItemsUseCase(3),
                resepLaravelRepository.getAllResep(), // Gunakan resep dari Laravel
                getAllShoppingItemsUseCase(),
                getAllNotificationsUseCase()
            ) { expiring, resepList, shopping, notifications ->
                val activeShoppingItems = shopping.filter { !it.isChecked }
                // Konversi ResepDto ke Recipe dan ambil 2 teratas untuk grid
                val laravelRecipes = resepList.toRecipeList().take(2)
                HomeUiState(
                    expiringItems = expiring,
                    recommendedRecipes = laravelRecipes,
                    shoppingItems = activeShoppingItems.take(3),
                    filteredExpiringItems = expiring,
                    filteredRecipes = laravelRecipes,
                    filteredShoppingItems = activeShoppingItems.take(3),
                    hasUnreadNotifications = notifications.any { !it.isRead },
                    isLoading = false
                )
            }.catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }.collect { state ->
                _uiState.update { currentState -> 
                    state.copy(
                        // Preserve rekomendasi state
                        rekomendasiResep = currentState.rekomendasiResep,
                        isLoadingRekomendasi = currentState.isLoadingRekomendasi,
                        errorRekomendasi = currentState.errorRekomendasi,
                        listBarang = currentState.listBarang
                    )
                }
            }
        }
    }
    
    fun refreshData() {
        loadData()
    }

    fun onHomeShoppingItemChecked(id: String, checked: Boolean) {
        if (!checked) return // uncheck hanya dari layar daftar belanja
        val currentItem = _uiState.value.shoppingItems.find { it.id == id } ?: return
        viewModelScope.launch {
            updateShoppingItemUseCase(currentItem.copy(isChecked = true))
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            val q = query.lowercase().trim()
            
            val filteredExpiring = if (q.isBlank()) {
                state.expiringItems
            } else {
                state.expiringItems.filter { it.name.lowercase().contains(q) }
            }
            
            val filteredRec = if (q.isBlank()) {
                state.recommendedRecipes
            } else {
                state.recommendedRecipes.filter { it.name.lowercase().contains(q) }
            }
            
            val filteredShopping = if (q.isBlank()) {
                state.shoppingItems
            } else {
                state.shoppingItems.filter { it.name.lowercase().contains(q) }
            }
            
            state.copy(
                searchQuery = query,
                filteredExpiringItems = filteredExpiring,
                filteredRecipes = filteredRec,
                filteredShoppingItems = filteredShopping
            )
        }
    }

    private suspend fun generateExpiryNotifications(items: List<FoodItem>) {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        items.forEach { item ->
            val type = when (item.expiryStatus) {
                com.zeromeal.app.domain.model.ExpiryStatus.URGENT -> NotificationType.URGENT
                com.zeromeal.app.domain.model.ExpiryStatus.WARNING -> NotificationType.WARNING
                com.zeromeal.app.domain.model.ExpiryStatus.NORMAL -> return@forEach
            }

            val title = when (type) {
                NotificationType.URGENT -> "Produk Akan Kedaluwarsa"
                NotificationType.WARNING -> "Peringatan Kedaluwarsa"
                else -> "Info Kedaluwarsa"
            }

            val message = "Stok ${item.name} akan kedaluwarsa pada ${item.expirationDate.format(formatter)}. Segera gunakan sebelum basi."

            val notification = Notification(
                id = "expiry_${item.id}", // id deterministik per item
                type = type,
                title = title,
                message = message,
                actionText = "Lihat Stok",
                actionData = item.id
            )
            insertNotificationUseCase(notification)
        }
    }
}
