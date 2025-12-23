package com.zeromeal.app.domain.usecase.resep

import com.zeromeal.app.data.remote.dto.ResepDto
import com.zeromeal.app.domain.repository.InventarisRepository
import com.zeromeal.app.domain.repository.ResepLaravelRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * UseCase untuk mendapatkan rekomendasi resep
 * Berdasarkan kecocokan bahan yang dimiliki user (≥60%)
 */
class GetRekomendasiResepUseCase @Inject constructor(
    private val resepRepository: ResepLaravelRepository,
    private val inventarisRepository: InventarisRepository
) {
    /**
     * @param userId ID user untuk mengambil inventaris
     * @param minMatchPercentage Minimum persentase kecocokan (default 60%)
     */
    suspend operator fun invoke(
        userId: Int,
        minMatchPercentage: Double = 60.0
    ): List<ResepDto> {
        // Ambil inventaris user
        val inventaris = inventarisRepository.getInventarisByUser(userId).first()
        
        // Extract barang_id dari inventaris user (filter null)
        val userBarangIds = inventaris.mapNotNull { it.barangId }
        
        // Dapatkan rekomendasi berdasarkan matching
        return resepRepository.getRekomendasiResep(userBarangIds, minMatchPercentage)
    }
}
