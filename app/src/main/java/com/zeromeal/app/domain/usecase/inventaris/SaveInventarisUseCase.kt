package com.zeromeal.app.domain.usecase.inventaris

import com.zeromeal.app.data.remote.dto.InventarisDto
import com.zeromeal.app.domain.repository.InventarisRepository
import javax.inject.Inject

/**
 * UseCase untuk menyimpan inventaris user ke MySQL
 */
class SaveInventarisUseCase @Inject constructor(
    private val repository: InventarisRepository
) {
    suspend operator fun invoke(item: InventarisDto): Result<InventarisDto> {
        return repository.saveInventaris(item)
    }
}
