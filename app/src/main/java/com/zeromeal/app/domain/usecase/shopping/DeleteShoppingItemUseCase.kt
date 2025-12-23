package com.zeromeal.app.domain.usecase.shopping

import com.zeromeal.app.domain.repository.ShoppingItemRepository
import javax.inject.Inject

class DeleteShoppingItemUseCase @Inject constructor(
    private val repository: ShoppingItemRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteShoppingItemById(id)
    }
}

