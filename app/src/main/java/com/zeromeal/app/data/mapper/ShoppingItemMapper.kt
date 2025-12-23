package com.zeromeal.app.data.mapper

import com.zeromeal.app.data.local.entity.ShoppingItemEntity
import com.zeromeal.app.domain.model.ShoppingItem

fun ShoppingItemEntity.toDomain(): ShoppingItem {
    return ShoppingItem(
        id = id,
        name = name,
        quantity = quantity,
        isChecked = isChecked,
        createdAt = createdAt
    )
}

fun ShoppingItem.toEntity(): ShoppingItemEntity {
    return ShoppingItemEntity(
        id = id,
        name = name,
        quantity = quantity,
        isChecked = isChecked,
        createdAt = createdAt
    )
}

