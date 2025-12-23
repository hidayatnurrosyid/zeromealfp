package com.zeromeal.app.domain.usecase.barang

import com.zeromeal.app.domain.model.Barang
import com.zeromeal.app.domain.repository.BarangRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase untuk mengambil semua data Barang dari API
 */
class GetSemuaBarangUseCase @Inject constructor(
    private val repository: BarangRepository
) {
    operator fun invoke(): Flow<List<Barang>> {
        return repository.getSemuaBarang()
    }
}
