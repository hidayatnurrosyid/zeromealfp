package com.zeromeal.app.domain.repository

import com.zeromeal.app.domain.model.Barang
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface untuk Barang
 */
interface BarangRepository {
    fun getSemuaBarang(): Flow<List<Barang>>
    suspend fun getBarangById(id: Int): Barang?
    suspend fun refreshBarang()
}
