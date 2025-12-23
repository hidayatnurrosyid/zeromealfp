package com.zeromeal.app.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zeromeal.app.data.local.entity.RecipeEntity
import com.zeromeal.app.domain.model.FunFact
import com.zeromeal.app.domain.model.Ingredient
import com.zeromeal.app.domain.model.Recipe

private val gson = Gson()

fun RecipeEntity.toDomain(): Recipe {
    val ingredientsType = object : TypeToken<List<Ingredient>>() {}.type
    val instructionsType = object : TypeToken<List<String>>() {}.type
    val funFactsType = object : TypeToken<List<FunFact>>() {}.type

    // Safely parse ingredients with try-catch
    val parsedIngredients: List<Ingredient> = try {
        gson.fromJson(ingredients, ingredientsType) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    // Safely parse instructions with try-catch
    val parsedInstructions: List<String> = try {
        gson.fromJson(instructions, instructionsType) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    // Safely parse fun facts with try-catch
    val parsedFunFacts: List<FunFact>? = try {
        funFacts?.let { gson.fromJson(it, funFactsType) }
    } catch (e: Exception) {
        null
    }

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
        ingredients = parsedIngredients,
        instructions = parsedInstructions,
        funFacts = parsedFunFacts,
        chefName = chefName,
        chefImageUrl = chefImageUrl,
        uploadDate = uploadDate,
        ingredientAvailability = ingredientAvailability
    )
}

fun Recipe.toEntity(): RecipeEntity {
    return RecipeEntity(
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
        ingredients = gson.toJson(ingredients),
        instructions = gson.toJson(instructions),
        funFacts = funFacts?.let { gson.toJson(it) },
        chefName = chefName,
        chefImageUrl = chefImageUrl,
        uploadDate = uploadDate,
        ingredientAvailability = ingredientAvailability
    )
}

