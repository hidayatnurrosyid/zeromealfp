package com.zeromeal.app.data.repository

import com.zeromeal.app.data.remote.ApiService
import com.zeromeal.app.data.remote.dto.InventarisDto
import com.zeromeal.app.domain.repository.InventarisRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementasi repository untuk inventaris user
 * Menyimpan dan mengambil stok barang user dari MySQL
 */
@Singleton
class InventarisRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : InventarisRepository {
    
    private val _inventarisList = MutableStateFlow<List<InventarisDto>>(emptyList())
    
    override fun getInventarisByUser(userId: Int): Flow<List<InventarisDto>> {
        return _inventarisList.asStateFlow()
    }
    
    override suspend fun saveInventaris(item: InventarisDto): Result<InventarisDto> {
        return try {
            val response = apiService.createInventaris(item)
            if (response.isSuccessful && response.body()?.status == true) {
                response.body()?.data?.let { savedItem ->
                    // Update local cache
                    _inventarisList.value = _inventarisList.value + savedItem
                    Result.success(savedItem)
                } ?: Result.failure(Exception("Data not returned"))
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to save"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateInventaris(item: InventarisDto): Result<InventarisDto> {
        return try {
            val id = item.inventarisId ?: return Result.failure(Exception("ID is required"))
            val response = apiService.updateInventaris(id, item)
            if (response.isSuccessful && response.body()?.status == true) {
                response.body()?.data?.let { updatedItem ->
                    // Update local cache
                    _inventarisList.value = _inventarisList.value.map { 
                        if (it.inventarisId == id) updatedItem else it 
                    }
                    Result.success(updatedItem)
                } ?: Result.failure(Exception("Data not returned"))
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to update"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteInventaris(id: Int): Result<Unit> {
        return try {
            val response = apiService.deleteInventaris(id)
            if (response.isSuccessful && response.body()?.status == true) {
                // Remove from local cache
                _inventarisList.value = _inventarisList.value.filter { it.inventarisId != id }
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to delete"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun refreshInventaris(userId: Int) {
        try {
            val response = apiService.getInventarisByUser(userId)
            if (response.isSuccessful && response.body()?.status == true) {
                _inventarisList.value = response.body()?.data ?: emptyList()
            }
        } catch (e: Exception) {
            throw e
        }
    }
}
