package com.zeromeal.app.data.repository

import com.zeromeal.app.data.remote.ApiService
import com.zeromeal.app.data.remote.dto.ResepDto
import com.zeromeal.app.domain.repository.ResepLaravelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementasi repository untuk resep dari Laravel API
 * Dengan logic matching untuk rekomendasi
 */
@Singleton
class ResepLaravelRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ResepLaravelRepository {
    
    private val _resepList = MutableStateFlow<List<ResepDto>>(emptyList())
    
    override fun getAllResep(): Flow<List<ResepDto>> {
        return _resepList.asStateFlow()
    }
    
    override suspend fun getResepById(id: Int): ResepDto? {
        // Coba dari cache dulu
        val cachedResep = _resepList.value.find { it.resepId == id }
        if (cachedResep != null) {
            return cachedResep
        }
        
        // Jika tidak ada di cache, refresh dan cari lagi
        try {
            if (_resepList.value.isEmpty()) {
                refreshResep()
            }
            val fromCache = _resepList.value.find { it.resepId == id }
            if (fromCache != null) {
                return fromCache
            }
            
            // Coba fetch dari API by ID
            val response = apiService.getResepById(id)
            if (response.isSuccessful && response.body()?.status == true) {
                return response.body()?.data
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    
    override suspend fun refreshResep() {
        try {
            val response = apiService.getResep()
            if (response.isSuccessful && response.body()?.status == true) {
                _resepList.value = response.body()?.data ?: emptyList()
            }
        } catch (e: Exception) {
            throw e
        }
    }
    
    /**
     * Mendapatkan resep rekomendasi berdasarkan kecocokan bahan
     * Algorithm: Jika user memiliki ≥60% bahan yang dibutuhkan resep,
     * maka resep tersebut direkomendasikan
     */
    override suspend fun getRekomendasiResep(
        userBarangIds: List<Int>,
        minMatchPercentage: Double
    ): List<ResepDto> {
        // Refresh resep terlebih dahulu jika kosong
        if (_resepList.value.isEmpty()) {
            refreshResep()
        }
        
        val allResep = _resepList.value
        val userBarangSet = userBarangIds.toSet()
        
        return allResep.filter { resep ->
            val bahanList = resep.bahan ?: return@filter false
            if (bahanList.isEmpty()) return@filter false
            
            val resepBarangIds = bahanList.map { it.barangId }.toSet()
            val matchedCount = resepBarangIds.intersect(userBarangSet).size
            val totalRequired = resepBarangIds.size
            
            val matchPercentage = if (totalRequired > 0) {
                (matchedCount.toDouble() / totalRequired) * 100
            } else 0.0
            
            matchPercentage >= minMatchPercentage
        }.sortedByDescending { resep ->
            // Sort by match percentage (higher first)
            val bahanList = resep.bahan ?: emptyList()
            val resepBarangIds = bahanList.map { it.barangId }.toSet()
            val matchedCount = resepBarangIds.intersect(userBarangSet).size
            val totalRequired = resepBarangIds.size
            if (totalRequired > 0) (matchedCount.toDouble() / totalRequired) * 100 else 0.0
        }
    }
}
