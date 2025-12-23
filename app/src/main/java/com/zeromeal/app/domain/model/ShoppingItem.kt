package com.zeromeal.app.domain.model

data class ShoppingItem(
    val id: String,
    val name: String,
    val quantity: String? = null,
    val isChecked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

