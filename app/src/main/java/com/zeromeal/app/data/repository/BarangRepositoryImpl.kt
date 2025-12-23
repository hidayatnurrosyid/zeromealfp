package com.zeromeal.app.data.repository

import com.zeromeal.app.data.mapper.toDomain
import com.zeromeal.app.data.mapper.toDomainList
import com.zeromeal.app.data.remote.ApiService
import com.zeromeal.app.domain.model.Barang
import com.zeromeal.app.domain.repository.BarangRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementasi repository untuk Barang
 * Mengambil data dari API Laravel
 */
@Singleton
class BarangRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : BarangRepository {
    
    private val _barangList = MutableStateFlow<List<Barang>>(emptyList())
    
    override fun getSemuaBarang(): Flow<List<Barang>> {
        return _barangList.asStateFlow()
    }
    
    override suspend fun getBarangById(id: Int): Barang? {
        return try {
            val response = apiService.getBarangById(id)
            if (response.isSuccessful && response.body()?.status == true) {
                response.body()?.data?.toDomain()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun refreshBarang() {
        try {
            val response = apiService.getBarang()
            if (response.isSuccessful && response.body()?.status == true) {
                val barangList = response.body()?.data?.toDomainList() ?: emptyList()
                _barangList.value = barangList
            }
        } catch (e: Exception) {
            throw e
        }
    }
}
