package com.zeromeal.app.domain.model

data class Notification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val actionText: String? = null,
    val actionData: String? = null
)

enum class NotificationType {
    URGENT, WARNING, INFO, REMINDER
}

