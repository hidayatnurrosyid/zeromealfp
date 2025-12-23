package com.zeromeal.app.data.mapper

import com.zeromeal.app.data.remote.dto.ResepDto
import com.zeromeal.app.domain.model.FunFact
import com.zeromeal.app.domain.model.Ingredient
import com.zeromeal.app.domain.model.Recipe

/**
 * Mapper untuk konversi ResepDto (Laravel) ke domain model Recipe
 */
fun ResepDto.toRecipe(): Recipe {
    return Recipe(
        id = resepId.toString(),
        name = judul,
        imageUrl = imageUrl,
        cookingTime = waktuPembuatanMenit ?: 0,
        rating = rating,
        reviewCount = 0, // Tidak ada di database
        difficulty = when(difficulty.lowercase()) {
            "easy" -> "Mudah"
            "medium" -> "Sedang"
            "hard" -> "Sulit"
            else -> difficulty
        },
        mealType = "", // Tidak ada di database
        description = deskripsi,
        // Nutrition data dari database
        calories = kaloriPerPorsi,
        protein = proteinGram,
        fat = lemakGram,
        carbs = karbohidratGram,
        // Bahan-bahan dari detail_resep_bahan
        ingredients = bahan?.map { 
            Ingredient(
                name = it.namaBarang ?: "Bahan",
                quantity = "${it.jumlahDibutuhkan ?: ""} ${it.satuan ?: ""}".trim()
            )
        } ?: emptyList(),
        // Langkah-langkah
        instructions = langkah?.split("\n")?.filter { it.isNotBlank() } ?: emptyList(),
        // Fun fact dari database
        funFacts = if (funFact != null) {
            listOf(
                FunFact(
                    title = "Tahukah Kamu?",
                    description = funFact,
                    iconType = "tips"
                )
            )
        } else null
    )
}

fun List<ResepDto>.toRecipeList(): List<Recipe> {
    return map { it.toRecipe() }
}
