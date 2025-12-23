package com.zeromeal.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    val id: String,
    val type: String, // "Urgent", "Warning", "Info", "Reminder"
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val actionText: String? = null,
    val actionData: String? = null // JSON string for action data
)

