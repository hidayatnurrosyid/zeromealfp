package com.zeromeal.app.domain.model

data class Recipe(
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
    val ingredients: List<Ingredient> = emptyList(),
    val instructions: List<String> = emptyList(),
    val funFacts: List<FunFact>? = null,
    val chefName: String? = null,
    val chefImageUrl: String? = null,
    val uploadDate: String? = null,
    val ingredientAvailability: String = "tersedia" // "tersedia", "terbatas"
)

data class Ingredient(
    val name: String,
    val quantity: String
)

data class FunFact(
    val title: String,
    val description: String,
    val iconType: String // "history", "tips", "variation"
)

