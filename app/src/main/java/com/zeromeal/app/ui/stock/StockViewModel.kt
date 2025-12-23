package com.zeromeal.app.ui.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeromeal.app.domain.model.FoodItem
import com.zeromeal.app.domain.model.ShoppingItem
import com.zeromeal.app.domain.usecase.fooditem.DeleteFoodItemUseCase
import com.zeromeal.app.domain.usecase.fooditem.GetAllFoodItemsUseCase
import com.zeromeal.app.domain.usecase.notification.GetAllNotificationsUseCase
import com.zeromeal.app.domain.usecase.shopping.InsertShoppingItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StockUiState(
    val allItems: List<FoodItem> = emptyList(),
    val filteredItems: List<FoodItem> = emptyList(),
    val selectedFilter: StockFilter = StockFilter.ALL,
    val searchQuery: String = "",
    val sortOption: StockSortOption = StockSortOption.NAME_ASC,
    val hasUnreadNotifications: Boolean = false
)

enum class StockSortOption {
    NAME_ASC,
    NAME_DESC,
    PURCHASE_DATE_DESC
}

@HiltViewModel
class StockViewModel @Inject constructor(
    private val getAllFoodItemsUseCase: GetAllFoodItemsUseCase,
    private val deleteFoodItemUseCase: DeleteFoodItemUseCase,
    private val insertShoppingItemUseCase: InsertShoppingItemUseCase,
    private val getAllNotificationsUseCase: GetAllNotificationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StockUiState())
    val uiState: StateFlow<StockUiState> = _uiState.asStateFlow()

    init {
        observeFoodItems()
    }

    private fun observeFoodItems() {
        viewModelScope.launch {
            // Observe items and notifications
            // Note: Simplest way is two coroutines or combine.
            // Let's use two launches or combine if possible.
            // Using launch for separate flows to avoid complex combine logic for now, 
            // although combine is better.
            launch {
                getAllFoodItemsUseCase().collectLatest { items ->
                    _uiState.update { state ->
                        val activeItems = items.filter { !it.isFinished }
                        state.copy(
                            allItems = activeItems
                        )
                    }
                    applyFilterAndSearch()
                }
            }
            launch {
                getAllNotificationsUseCase().collectLatest { notifications ->
                    _uiState.update { it.copy(hasUnreadNotifications = notifications.any { !it.isRead }) }
                }
            }
        }
    }

    fun onFilterSelected(filter: StockFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
        applyFilterAndSearch()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilterAndSearch()
    }

    fun onSortOptionSelected(option: StockSortOption) {
        _uiState.update { it.copy(sortOption = option) }
        applyFilterAndSearch()
    }

    fun onSortButtonClick() {
        _uiState.update { state ->
            val nextSort = when (state.sortOption) {
                StockSortOption.NAME_ASC -> StockSortOption.NAME_DESC
                StockSortOption.NAME_DESC -> StockSortOption.PURCHASE_DATE_DESC
                StockSortOption.PURCHASE_DATE_DESC -> StockSortOption.NAME_ASC
            }
            state.copy(sortOption = nextSort)
        }
        applyFilterAndSearch()
    }

    fun deleteItem(id: String) {
        viewModelScope.launch {
            deleteFoodItemUseCase(id)
        }
    }

    fun addItemToShoppingList(item: FoodItem) {
        viewModelScope.launch {
            val shoppingItem = ShoppingItem(
                id = item.id,
                name = item.name,
                quantity = "${item.quantity} ${item.unit}"
            )
            insertShoppingItemUseCase(shoppingItem)
        }
    }

    private fun applyFilterAndSearch() {
        val state = _uiState.value

        var items = when (state.selectedFilter) {
            StockFilter.URGENT -> state.allItems.filter { it.expiryStatus.name == "URGENT" }
            StockFilter.WARNING -> state.allItems.filter { it.expiryStatus.name == "WARNING" }
            StockFilter.NORMAL -> state.allItems.filter { it.expiryStatus.name == "NORMAL" }
            StockFilter.ALL -> state.allItems
        }

        if (state.searchQuery.isNotBlank()) {
            val q = state.searchQuery.lowercase()
            items = items.filter { item ->
                item.name.lowercase().contains(q) || item.category.lowercase().contains(q)
            }
        }
        // Sorting
        items = when (state.sortOption) {
            StockSortOption.NAME_ASC -> items.sortedBy { it.name.lowercase() }
            StockSortOption.NAME_DESC -> items.sortedByDescending { it.name.lowercase() }
            StockSortOption.PURCHASE_DATE_DESC -> items.sortedByDescending { it.purchaseDate }
        }

        _uiState.update { it.copy(filteredItems = items) }
    }
}


