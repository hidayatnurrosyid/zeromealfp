package com.zeromeal.app.domain.usecase.fooditem

import com.zeromeal.app.domain.model.FoodItem
import com.zeromeal.app.domain.repository.FoodItemRepository
import javax.inject.Inject

class UpdateFoodItemUseCase @Inject constructor(
    private val repository: FoodItemRepository
) {
    suspend operator fun invoke(foodItem: FoodItem) {
        repository.updateFoodItem(foodItem)
    }
}

