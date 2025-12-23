package com.zeromeal.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_items")
data class ShoppingItemEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val quantity: String? = null,
    val isChecked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

