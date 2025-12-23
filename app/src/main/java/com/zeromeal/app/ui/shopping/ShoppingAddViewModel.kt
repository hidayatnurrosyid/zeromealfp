package com.zeromeal.app.ui.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeromeal.app.data.remote.dto.DaftarBelanjaDto
import com.zeromeal.app.domain.model.ShoppingItem
import com.zeromeal.app.domain.repository.DaftarBelanjaRepository
import com.zeromeal.app.domain.usecase.shopping.InsertShoppingItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ShoppingAddUiState(
    val name: String = "",
    val quantity: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ShoppingAddViewModel @Inject constructor(
    private val insertShoppingItemUseCase: InsertShoppingItemUseCase,
    private val daftarBelanjaRepository: DaftarBelanjaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShoppingAddUiState())
    val uiState: StateFlow<ShoppingAddUiState> = _uiState

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateQuantity(quantity: String) {
        _uiState.update { it.copy(quantity = quantity) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun reset() {
        _uiState.value = ShoppingAddUiState()
    }

    fun save(onFinished: (Boolean) -> Unit) {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(error = "Nama barang wajib diisi") }
            onFinished(false)
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                // Save to local (Room)
                val item = ShoppingItem(
                    id = UUID.randomUUID().toString(),
                    name = state.name,
                    quantity = state.quantity.ifBlank { null },
                    isChecked = false
                )
                insertShoppingItemUseCase(item)
                
                // POST to MySQL
                saveToMySql(state)
                
                _uiState.update { it.copy(isLoading = false) }
                onFinished(true)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Gagal menyimpan item"
                    )
                }
                onFinished(false)
            }
        }
    }
    
    private suspend fun saveToMySql(state: ShoppingAddUiState) {
        try {
            val dto = DaftarBelanjaDto(
                belanjaId = null,
                userId = 1, // TODO: Get actual user ID
                namaProduk = state.name,
                jumlahProduk = state.quantity.toIntOrNull() ?: 1,
                statusBeli = false
            )
            daftarBelanjaRepository.createDaftarBelanja(dto)
        } catch (e: Exception) {
            // Log error tapi tidak gagalkan save ke Room
            e.printStackTrace()
        }
    }
}
