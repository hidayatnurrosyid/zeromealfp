package com.zeromeal.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.zeromeal.app.domain.model.ShoppingItem

data class ShoppingItemDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("quantity")
    val quantity: String? = null,
    
    @SerializedName("is_checked")
    val isChecked: Boolean = false
) {
    fun toDomain(): ShoppingItem {
        return ShoppingItem(
            id = id,
            name = name,
            quantity = quantity,
            isChecked = isChecked
        )
    }
    
    companion object {
        fun fromDomain(item: ShoppingItem): ShoppingItemDto {
            return ShoppingItemDto(
                id = item.id,
                name = item.name,
                quantity = item.quantity,
                isChecked = item.isChecked
            )
        }
    }
}

