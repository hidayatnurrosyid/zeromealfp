package com.zeromeal.app.domain.model

import java.time.LocalDate

data class FoodItem(
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
) {
    val daysUntilExpiration: Long
        get() = java.time.temporal.ChronoUnit.DAYS.between(
            java.time.LocalDate.now(),
            expirationDate
        )

    val expiryStatus: ExpiryStatus
        get() = when {
            daysUntilExpiration <= 1 -> ExpiryStatus.URGENT
            daysUntilExpiration in 2..3 -> ExpiryStatus.WARNING
            else -> ExpiryStatus.NORMAL
        }
}

enum class ExpiryStatus {
    URGENT, WARNING, NORMAL
}

