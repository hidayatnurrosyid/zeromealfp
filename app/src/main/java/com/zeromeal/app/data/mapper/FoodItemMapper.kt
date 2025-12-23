package com.zeromeal.app.data.mapper

import com.zeromeal.app.data.local.entity.FoodItemEntity
import com.zeromeal.app.domain.model.FoodItem

fun FoodItemEntity.toDomain(): FoodItem {
    return FoodItem(
        id = id,
        name = name,
        imageUrl = imageUrl,
        category = category,
        purchaseDate = purchaseDate,
        expirationDate = expirationDate,
        quantity = quantity,
        unit = unit,
        storageLocation = storageLocation,
        notes = notes,
        isFinished = isFinished
    )
}

fun FoodItem.toEntity(): FoodItemEntity {
    return FoodItemEntity(
        id = id,
        name = name,
        imageUrl = imageUrl,
        category = category,
        purchaseDate = purchaseDate,
        expirationDate = expirationDate,
        quantity = quantity,
        unit = unit,
        storageLocation = storageLocation,
        notes = notes,
        isFinished = isFinished
    )
}

