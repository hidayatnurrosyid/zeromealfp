package com.zeromeal.app.domain.usecase.shopping

import com.zeromeal.app.domain.model.ShoppingItem
import com.zeromeal.app.domain.repository.ShoppingItemRepository
import javax.inject.Inject

class UpdateShoppingItemUseCase @Inject constructor(
    private val repository: ShoppingItemRepository
) {
    suspend operator fun invoke(item: ShoppingItem) {
        repository.updateShoppingItem(item)
    }
}

