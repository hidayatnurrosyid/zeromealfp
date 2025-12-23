package com.zeromeal.app.domain.repository

import com.zeromeal.app.data.remote.dto.DaftarBelanjaDto
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface untuk daftar belanja dari Laravel API
 */
interface DaftarBelanjaRepository {
    fun getDaftarBelanjaByUser(userId: Int): Flow<List<DaftarBelanjaDto>>
    suspend fun createDaftarBelanja(item: DaftarBelanjaDto): DaftarBelanjaDto?
    suspend fun updateDaftarBelanja(id: Int, item: DaftarBelanjaDto): DaftarBelanjaDto?
    suspend fun deleteDaftarBelanja(id: Int): Boolean
    suspend fun refreshDaftarBelanja(userId: Int)
}
