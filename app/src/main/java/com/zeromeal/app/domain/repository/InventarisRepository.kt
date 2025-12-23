package com.zeromeal.app.domain.repository

import com.zeromeal.app.data.remote.dto.InventarisDto
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface untuk inventaris user (stok barang)
 */
interface InventarisRepository {
    fun getInventarisByUser(userId: Int): Flow<List<InventarisDto>>
    suspend fun saveInventaris(item: InventarisDto): Result<InventarisDto>
    suspend fun updateInventaris(item: InventarisDto): Result<InventarisDto>
    suspend fun deleteInventaris(id: Int): Result<Unit>
    suspend fun refreshInventaris(userId: Int)
}
