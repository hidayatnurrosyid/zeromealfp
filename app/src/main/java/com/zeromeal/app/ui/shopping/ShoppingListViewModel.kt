package com.zeromeal.app.ui.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeromeal.app.data.remote.dto.DaftarBelanjaDto
import com.zeromeal.app.domain.model.ShoppingItem
import com.zeromeal.app.domain.repository.DaftarBelanjaRepository
import com.zeromeal.app.domain.usecase.shopping.DeleteShoppingItemUseCase
import com.zeromeal.app.domain.usecase.shopping.GetAllShoppingItemsUseCase
import com.zeromeal.app.domain.usecase.shopping.GetShoppingStatsUseCase
import com.zeromeal.app.domain.usecase.shopping.ShoppingStats
import com.zeromeal.app.domain.usecase.shopping.UpdateShoppingItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShoppingListUiState(
    val items: List<ShoppingItem> = emptyList(),
    val stats: ShoppingStats? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val getAllShoppingItemsUseCase: GetAllShoppingItemsUseCase,
    private val getShoppingStatsUseCase: GetShoppingStatsUseCase,
    private val updateShoppingItemUseCase: UpdateShoppingItemUseCase,
    private val deleteShoppingItemUseCase: DeleteShoppingItemUseCase,
    private val daftarBelanjaRepository: DaftarBelanjaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShoppingListUiState())
    val uiState: StateFlow<ShoppingListUiState> = _uiState.asStateFlow()

    init {
        observeItems()
        observeStats()
        syncFromMySql()
    }

    private fun observeItems() {
        viewModelScope.launch {
            getAllShoppingItemsUseCase().collectLatest { items ->
                _uiState.update { it.copy(items = items) }
            }
        }
    }

    private fun observeStats() {
        viewModelScope.launch {
            getShoppingStatsUseCase().collectLatest { stats ->
                _uiState.update { it.copy(stats = stats) }
            }
        }
    }
    
    private fun syncFromMySql() {
        viewModelScope.launch {
            try {
                daftarBelanjaRepository.refreshDaftarBelanja(1) // TODO: Get actual user ID
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onItemCheckedChange(id: String, checked: Boolean) {
        val item = _uiState.value.items.find { it.id == id } ?: return
        viewModelScope.launch {
            // Update local
            updateShoppingItemUseCase(item.copy(isChecked = checked))
            
            // Sync ke MySQL
            try {
                val belanjaId = id.toIntOrNull()
                if (belanjaId != null) {
                    val dto = DaftarBelanjaDto(
                        belanjaId = belanjaId,
                        userId = 1, // TODO: Get actual user ID
                        namaProduk = item.name,
                        jumlahProduk = item.quantity?.toIntOrNull() ?: 1,
                        statusBeli = checked
                    )
                    daftarBelanjaRepository.updateDaftarBelanja(belanjaId, dto)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteItem(id: String) {
        viewModelScope.launch {
            // Delete local
            deleteShoppingItemUseCase(id)
            
            // Delete from MySQL
            try {
                val belanjaId = id.toIntOrNull()
                if (belanjaId != null) {
                    daftarBelanjaRepository.deleteDaftarBelanja(belanjaId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
