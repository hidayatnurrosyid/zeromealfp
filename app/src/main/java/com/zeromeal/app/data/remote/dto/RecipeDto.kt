package com.zeromeal.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.zeromeal.app.domain.model.FunFact
import com.zeromeal.app.domain.model.Ingredient
import com.zeromeal.app.domain.model.Recipe

data class RecipeDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("image_url")
    val imageUrl: String? = null,
    
    @SerializedName("cooking_time")
    val cookingTime: Int,
    
    @SerializedName("rating")
    val rating: Double,
    
    @SerializedName("review_count")
    val reviewCount: Int = 0,
    
    @SerializedName("difficulty")
    val difficulty: String,
    
    @SerializedName("meal_type")
    val mealType: String,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("calories")
    val calories: Int? = null,
    
    @SerializedName("protein")
    val protein: Double? = null,
    
    @SerializedName("fat")
    val fat: Double? = null,
    
    @SerializedName("carbs")
    val carbs: Double? = null,
    
    @SerializedName("fiber")
    val fiber: Double? = null,
    
    @SerializedName("sodium")
    val sodium: Double? = null,
    
    @SerializedName("sugar")
    val sugar: Double? = null,
    
    @SerializedName("ingredients")
    val ingredients: List<IngredientDto>,
    
    @SerializedName("instructions")
    val instructions: List<String>,
    
    @SerializedName("fun_facts")
    val funFacts: List<FunFactDto>? = null,
    
    @SerializedName("chef_name")
    val chefName: String? = null,
    
    @SerializedName("chef_image_url")
    val chefImageUrl: String? = null,
    
    @SerializedName("upload_date")
    val uploadDate: String? = null,
    
    @SerializedName("ingredient_availability")
    val ingredientAvailability: String = "tersedia"
) {
    fun toDomain(): Recipe {
        return Recipe(
            id = id,
            name = name,
            imageUrl = imageUrl,
            cookingTime = cookingTime,
            rating = rating,
            reviewCount = reviewCount,
            difficulty = difficulty,
            mealType = mealType,
            description = description,
            calories = calories,
            protein = protein,
            fat = fat,
            carbs = carbs,
            fiber = fiber,
            sodium = sodium,
            sugar = sugar,
            ingredients = ingredients.map { it.toDomain() },
            instructions = instructions,
            funFacts = funFacts?.map { it.toDomain() },
            chefName = chefName,
            chefImageUrl = chefImageUrl,
            uploadDate = uploadDate,
            ingredientAvailability = ingredientAvailability
        )
    }
}

data class IngredientDto(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("quantity")
    val quantity: String
) {
    fun toDomain(): Ingredient {
        return Ingredient(name = name, quantity = quantity)
    }
}

data class FunFactDto(
    @SerializedName("title")
    val title: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("icon_type")
    val iconType: String
) {
    fun toDomain(): FunFact {
        return FunFact(
            title = title,
            description = description,
            iconType = iconType
        )
    }
}

