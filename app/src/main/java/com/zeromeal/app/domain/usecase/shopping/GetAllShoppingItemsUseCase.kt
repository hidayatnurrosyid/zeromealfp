package com.zeromeal.app.domain.usecase.shopping

import com.zeromeal.app.domain.model.ShoppingItem
import com.zeromeal.app.domain.repository.ShoppingItemRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllShoppingItemsUseCase @Inject constructor(
    private val repository: ShoppingItemRepository
) {
    operator fun invoke(): Flow<List<ShoppingItem>> {
        return repository.getAllShoppingItems()
    }
}

