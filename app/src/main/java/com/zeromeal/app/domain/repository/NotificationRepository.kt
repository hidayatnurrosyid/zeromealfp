package com.zeromeal.app.domain.repository

import com.zeromeal.app.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getAllNotifications(): Flow<List<Notification>>
    fun getNotificationsByType(type: String): Flow<List<Notification>>
    fun getUnreadNotifications(): Flow<List<Notification>>
    suspend fun insertNotification(notification: Notification)
    suspend fun markAsRead(id: String)
    suspend fun markAllAsRead()
    suspend fun deleteNotification(id: String)
}

