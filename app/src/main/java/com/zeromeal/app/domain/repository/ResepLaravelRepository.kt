package com.zeromeal.app.domain.repository

import com.zeromeal.app.data.remote.dto.ResepDto
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface untuk resep dari Laravel API
 */
interface ResepLaravelRepository {
    fun getAllResep(): Flow<List<ResepDto>>
    suspend fun getResepById(id: Int): ResepDto?
    suspend fun refreshResep()
    
    /**
     * Mendapatkan resep rekomendasi berdasarkan kecocokan bahan
     * @param userBarangIds List barang_id yang dimiliki user
     * @param minMatchPercentage Minimum persentase kecocokan (default 60%)
     */
    suspend fun getRekomendasiResep(
        userBarangIds: List<Int>,
        minMatchPercentage: Double = 60.0
    ): List<ResepDto>
}
