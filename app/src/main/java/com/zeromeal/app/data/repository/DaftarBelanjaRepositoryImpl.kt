package com.zeromeal.app.data.repository

import com.zeromeal.app.data.remote.ApiService
import com.zeromeal.app.data.remote.dto.DaftarBelanjaDto
import com.zeromeal.app.domain.repository.DaftarBelanjaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementasi repository untuk daftar belanja dari Laravel API
 */
@Singleton
class DaftarBelanjaRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : DaftarBelanjaRepository {
    
    private val _daftarBelanja = MutableStateFlow<List<DaftarBelanjaDto>>(emptyList())
    
    override fun getDaftarBelanjaByUser(userId: Int): Flow<List<DaftarBelanjaDto>> {
        return _daftarBelanja.asStateFlow()
    }
    
    override suspend fun createDaftarBelanja(item: DaftarBelanjaDto): DaftarBelanjaDto? {
        return try {
            val response = apiService.createDaftarBelanja(item)
            if (response.isSuccessful && response.body()?.status == true) {
                val newItem = response.body()?.data
                if (newItem != null) {
                    _daftarBelanja.value = _daftarBelanja.value + newItem
                }
                newItem
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    override suspend fun updateDaftarBelanja(id: Int, item: DaftarBelanjaDto): DaftarBelanjaDto? {
        return try {
            val response = apiService.updateDaftarBelanja(id, item)
            if (response.isSuccessful && response.body()?.status == true) {
                val updatedItem = response.body()?.data
                if (updatedItem != null) {
                    _daftarBelanja.value = _daftarBelanja.value.map {
                        if (it.belanjaId == id) updatedItem else it
                    }
                }
                updatedItem
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    override suspend fun deleteDaftarBelanja(id: Int): Boolean {
        return try {
            val response = apiService.deleteDaftarBelanja(id)
            if (response.isSuccessful && response.body()?.status == true) {
                _daftarBelanja.value = _daftarBelanja.value.filter { it.belanjaId != id }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    override suspend fun refreshDaftarBelanja(userId: Int) {
        try {
            val response = apiService.getDaftarBelanjaByUser(userId)
            if (response.isSuccessful && response.body()?.status == true) {
                _daftarBelanja.value = response.body()?.data ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
