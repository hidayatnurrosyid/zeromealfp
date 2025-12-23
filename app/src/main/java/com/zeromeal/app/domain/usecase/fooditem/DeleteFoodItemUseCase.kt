package com.zeromeal.app.domain.usecase.fooditem

import com.zeromeal.app.domain.repository.FoodItemRepository
import javax.inject.Inject

class DeleteFoodItemUseCase @Inject constructor(
    private val repository: FoodItemRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteFoodItemById(id)
    }
}

