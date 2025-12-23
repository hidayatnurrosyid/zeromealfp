package com.zeromeal.app.ui.manualinput

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeromeal.app.data.remote.dto.DaftarBelanjaDto
import com.zeromeal.app.data.remote.dto.InventarisDto
import com.zeromeal.app.domain.model.FoodItem
import com.zeromeal.app.domain.repository.BarangRepository
import com.zeromeal.app.domain.repository.DaftarBelanjaRepository
import com.zeromeal.app.domain.usecase.fooditem.InsertFoodItemUseCase
import com.zeromeal.app.domain.usecase.fooditem.UpdateFoodItemUseCase
import com.zeromeal.app.domain.usecase.inventaris.SaveInventarisUseCase
import com.zeromeal.app.domain.repository.FoodItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

data class ManualInputUiState(
    val id: String? = null,
    val name: String = "",
    val category: String = "",
    val purchaseDate: LocalDate? = null,
    val expirationDate: LocalDate? = null,
    val quantity: String = "0",
    val unit: String = "pcs",
    val storageLocation: String = "",
    val notes: String = "",
    val imageUri: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ManualInputViewModel @Inject constructor(
    private val insertFoodItemUseCase: InsertFoodItemUseCase,
    private val updateFoodItemUseCase: UpdateFoodItemUseCase,
    private val foodItemRepository: FoodItemRepository,
    private val saveInventarisUseCase: SaveInventarisUseCase,
    private val barangRepository: BarangRepository,
    private val daftarBelanjaRepository: DaftarBelanjaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManualInputUiState())
    val uiState: StateFlow<ManualInputUiState> = _uiState.asStateFlow()

    fun loadItem(id: String) {
        viewModelScope.launch {
            val item = foodItemRepository.getFoodItemById(id)
            item?.let {
                _uiState.update { state ->
                    state.copy(
                        id = it.id,
                        name = it.name,
                        category = it.category,
                        purchaseDate = it.purchaseDate,
                        expirationDate = it.expirationDate,
                        quantity = it.quantity.toString(),
                        unit = it.unit,
                        storageLocation = it.storageLocation,
                        notes = it.notes ?: "",
                        imageUri = it.imageUrl
                    )
                }
            }
        }
    }

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateCategory(category: String) {
        _uiState.update { it.copy(category = category) }
    }

    fun updatePurchaseDate(date: LocalDate) {
        _uiState.update { it.copy(purchaseDate = date) }
    }

    fun updateExpirationDate(date: LocalDate) {
        _uiState.update { it.copy(expirationDate = date) }
    }

    fun updateQuantity(quantity: String) {
        _uiState.update { it.copy(quantity = quantity) }
    }

    fun updateUnit(unit: String) {
        _uiState.update { it.copy(unit = unit) }
    }

    fun updateStorageLocation(location: String) {
        _uiState.update { it.copy(storageLocation = location) }
    }

    fun updateNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun updateImageUri(uri: String?) {
        _uiState.update { it.copy(imageUri = uri) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun validateForm(): Boolean {
        val state = _uiState.value

        // Kategori yang boleh tanpa tanggal kedaluwarsa spesifik
        val categoriesWithoutStrictExpiry = listOf(
            "Buah",
            "Sayuran",
            "Daging",
            "Ikan",
            "Bumbu & Rempah"
        )
        val requiresExpirationDate = state.category !in categoriesWithoutStrictExpiry

        return state.name.isNotBlank() &&
                state.category.isNotBlank() &&
                state.purchaseDate != null &&
                (!requiresExpirationDate || state.expirationDate != null) &&
                state.quantity.isNotBlank() &&
                state.unit.isNotBlank() &&
                state.storageLocation.isNotBlank()
    }

    suspend fun saveFoodItem(): Boolean {
        val state = _uiState.value
        
        if (!validateForm()) {
            _uiState.update { it.copy(error = "Mohon lengkapi semua field yang wajib diisi") }
            return false
        }

        return try {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val quantityValue = state.quantity.toDoubleOrNull() ?: 0.0
            val purchaseDate = state.purchaseDate ?: LocalDate.now()
            val expirationDate = state.expirationDate
                ?: state.purchaseDate?.plusDays(3)
                ?: LocalDate.now().plusDays(3)
            
            val foodItem = FoodItem(
                id = state.id ?: UUID.randomUUID().toString(),
                name = state.name,
                imageUrl = state.imageUri,
                category = state.category,
                purchaseDate = purchaseDate,
                expirationDate = expirationDate,
                quantity = quantityValue,
                unit = state.unit,
                storageLocation = state.storageLocation,
                notes = state.notes.ifBlank { null },
                isFinished = false
            )
            
            // Save ke Room (local)
            if (state.id == null) {
                insertFoodItemUseCase(foodItem)
            } else {
                updateFoodItemUseCase(foodItem)
            }
            
            // POST ke MySQL (remote) - cari barang_id yang cocok
            saveToMySql(state, purchaseDate, expirationDate, quantityValue.toInt())
            
            // Juga tambahkan ke daftar belanja
            saveToDaftarBelanja(state, quantityValue.toInt())
            
            _uiState.update { it.copy(isLoading = false) }
            true
        } catch (e: Exception) {
            _uiState.update { 
                it.copy(
                    isLoading = false, 
                    error = e.message ?: "Terjadi kesalahan saat menyimpan data"
                ) 
            }
            false
        }
    }
    
    /**
     * Simpan ke MySQL melalui API Laravel
     * Mencari barang_id yang cocok dengan nama barang
     */
    private suspend fun saveToMySql(
        state: ManualInputUiState,
        purchaseDate: LocalDate,
        expirationDate: LocalDate,
        quantity: Int
    ) {
        try {
            // Ambil daftar barang dari database
            val barangList = barangRepository.getSemuaBarang().first()
            
            // Cari barang_id yang cocok (case-insensitive partial match)
            val matchedBarang = barangList.find { barang ->
                barang.namaBarang.lowercase().contains(state.name.lowercase()) ||
                state.name.lowercase().contains(barang.namaBarang.lowercase())
            }
            
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            
            val inventarisDto = InventarisDto(
                inventarisId = null,
                userId = 1, // TODO: Get from logged-in user
                barangId = matchedBarang?.id, // null jika tidak ada match
                namaBarangInput = state.name,
                kategoriMakanan = state.category,
                gambar = state.imageUri,
                jumlah = quantity,
                satuanInput = state.unit,
                lokasiPenyimpanan = state.storageLocation,
                catatan = state.notes.ifBlank { null },
                tanggalPembelian = purchaseDate.atStartOfDay().format(dateFormatter),
                tanggalKadaluarsa = expirationDate.atStartOfDay().format(dateFormatter)
            )
            
            // POST ke API
            saveInventarisUseCase(inventarisDto)
        } catch (e: Exception) {
            // Log error tapi tidak gagalkan save ke Room
            e.printStackTrace()
        }
    }
    
    /**
     * Simpan ke daftar belanja MySQL
     * Agar stok yang ditambahkan juga muncul di daftar belanja
     */
    private suspend fun saveToDaftarBelanja(state: ManualInputUiState, quantity: Int) {
        try {
            val dto = DaftarBelanjaDto(
                belanjaId = null,
                userId = 1, // TODO: Get actual user ID
                namaProduk = state.name,
                jumlahProduk = quantity,
                statusBeli = true // Sudah dibeli karena ini dari stok
            )
            daftarBelanjaRepository.createDaftarBelanja(dto)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resetForm() {
        _uiState.value = ManualInputUiState()
    }
}
