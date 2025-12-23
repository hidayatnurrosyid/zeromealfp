package com.zeromeal.app.domain.usecase.shopping

import com.zeromeal.app.domain.repository.ShoppingItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class ShoppingStats(
    val totalItems: Int,
    val purchasedItems: Int
)

class GetShoppingStatsUseCase @Inject constructor(
    private val repository: ShoppingItemRepository
) {
    operator fun invoke(): Flow<ShoppingStats> {
        return combine(
            repository.getTotalCount(),
            repository.getPurchasedCount()
        ) { total, purchased ->
            ShoppingStats(total, purchased)
        }
    }
}

