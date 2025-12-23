package com.zeromeal.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.zeromeal.app.domain.model.FoodItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * DTO untuk FoodItem dari API
 * Format tanggal: "yyyy-MM-dd"
 */
data class FoodItemDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("image_url")
    val imageUrl: String? = null,
    
    @SerializedName("category")
    val category: String,
    
    @SerializedName("purchase_date")
    val purchaseDate: String, // Format: "yyyy-MM-dd"
    
    @SerializedName("expiration_date")
    val expirationDate: String, // Format: "yyyy-MM-dd"
    
    @SerializedName("quantity")
    val quantity: Double,
    
    @SerializedName("unit")
    val unit: String,
    
    @SerializedName("storage_location")
    val storageLocation: String,
    
    @SerializedName("notes")
    val notes: String? = null,
    
    @SerializedName("is_finished")
    val isFinished: Boolean = false
) {
    fun toDomain(): FoodItem {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        return FoodItem(
            id = id,
            name = name,
            imageUrl = imageUrl,
            category = category,
            purchaseDate = LocalDate.parse(purchaseDate, formatter),
            expirationDate = LocalDate.parse(expirationDate, formatter),
            quantity = quantity,
            unit = unit,
            storageLocation = storageLocation,
            notes = notes,
            isFinished = isFinished
        )
    }
    
    companion object {
        fun fromDomain(item: FoodItem): FoodItemDto {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            return FoodItemDto(
                id = item.id,
                name = item.name,
                imageUrl = item.imageUrl,
                category = item.category,
                purchaseDate = item.purchaseDate.format(formatter),
                expirationDate = item.expirationDate.format(formatter),
                quantity = item.quantity,
                unit = item.unit,
                storageLocation = item.storageLocation,
                notes = item.notes,
                isFinished = item.isFinished
            )
        }
    }
}

