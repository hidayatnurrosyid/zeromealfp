package com.zeromeal.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.zeromeal.app.domain.model.Notification
import com.zeromeal.app.domain.model.NotificationType

data class NotificationDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("type")
    val type: String, // "URGENT", "WARNING", "INFO", "REMINDER", "RECIPE"
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("action_text")
    val actionText: String? = null,
    
    @SerializedName("action_data")
    val actionData: String? = null,
    
    @SerializedName("is_read")
    val isRead: Boolean = false,
    
    @SerializedName("timestamp")
    val timestamp: Long // Unix timestamp in milliseconds
) {
    fun toDomain(): Notification {
        val notificationType = when (type.uppercase()) {
            "URGENT" -> NotificationType.URGENT
            "WARNING" -> NotificationType.WARNING
            "INFO" -> NotificationType.INFO
            "REMINDER" -> NotificationType.REMINDER
            // "RECIPE" not supported in domain model
            else -> NotificationType.INFO
        }
        
        return Notification(
            id = id,
            type = notificationType,
            title = title,
            message = message,
            actionText = actionText,
            actionData = actionData,
            isRead = isRead,
            timestamp = timestamp
        )
    }
    
    companion object {
        fun fromDomain(notification: Notification): NotificationDto {
            val typeString = when (notification.type) {
                NotificationType.URGENT -> "URGENT"
                NotificationType.WARNING -> "WARNING"
                NotificationType.INFO -> "INFO"
                NotificationType.REMINDER -> "REMINDER"
            }
            
            return NotificationDto(
                id = notification.id,
                type = typeString,
                title = notification.title,
                message = notification.message,
                actionText = notification.actionText,
                actionData = notification.actionData,
                isRead = notification.isRead,
                timestamp = notification.timestamp
            )
        }
    }
}

