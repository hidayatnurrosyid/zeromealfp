package com.zeromeal.app.domain.usecase.fooditem

import com.zeromeal.app.domain.model.FoodItem
import com.zeromeal.app.domain.repository.FoodItemRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllFoodItemsUseCase @Inject constructor(
    private val repository: FoodItemRepository
) {
    operator fun invoke(): Flow<List<FoodItem>> {
        return repository.getAllFoodItems()
    }
}

