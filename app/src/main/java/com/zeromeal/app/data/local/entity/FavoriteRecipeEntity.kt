package com.zeromeal.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_recipes")
data class FavoriteRecipeEntity(
    @PrimaryKey
    val recipeId: String,
    val recipeName: String,
    val imageUrl: String? = null,
    val cookingTime: Int = 0,
    val rating: Double = 0.0,
    val difficulty: String = "",
    val addedAt: Long = System.currentTimeMillis()
)
