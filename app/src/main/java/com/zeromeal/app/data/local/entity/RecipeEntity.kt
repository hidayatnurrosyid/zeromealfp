package com.zeromeal.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val cookingTime: Int, // in minutes
    val rating: Double,
    val reviewCount: Int = 0,
    val difficulty: String, // "Mudah", "Sedang", "Sulit"
    val mealType: String, // "Sarapan", "Makan Siang", "Makan Malam", "Pokok"
    val description: String? = null,
    val calories: Int? = null,
    val protein: Double? = null,
    val fat: Double? = null,
    val carbs: Double? = null,
    val fiber: Double? = null,
    val sodium: Double? = null,
    val sugar: Double? = null,
    val ingredients: String = "[]", // JSON string
    val instructions: String = "[]", // JSON string
    val funFacts: String? = null, // JSON string
    val chefName: String? = null,
    val chefImageUrl: String? = null,
    val uploadDate: String? = null,
    val ingredientAvailability: String = "tersedia" // "tersedia", "terbatas"
)

