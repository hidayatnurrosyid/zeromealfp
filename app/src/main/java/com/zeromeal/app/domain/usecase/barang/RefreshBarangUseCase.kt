package com.zeromeal.app.domain.usecase.barang

import com.zeromeal.app.domain.repository.BarangRepository
import javax.inject.Inject

/**
 * UseCase untuk refresh data Barang dari API
 */
class RefreshBarangUseCase @Inject constructor(
    private val repository: BarangRepository
) {
    suspend operator fun invoke() {
        repository.refreshBarang()
    }
}
