package com.zeromeal.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "food_items")
data class FoodItemEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val category: String,
    val purchaseDate: LocalDate,
    val expirationDate: LocalDate,
    val quantity: Double,
    val unit: String,
    val storageLocation: String,
    val notes: String? = null,
    val isFinished: Boolean = false
)

