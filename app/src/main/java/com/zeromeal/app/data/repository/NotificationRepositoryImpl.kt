package com.zeromeal.app.data.repository

import com.zeromeal.app.data.local.dao.NotificationDao
import com.zeromeal.app.data.mapper.toDomain
import com.zeromeal.app.data.mapper.toEntity
import com.zeromeal.app.domain.model.Notification
import com.zeromeal.app.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao
) : NotificationRepository {
    override fun getAllNotifications(): Flow<List<Notification>> {
        return notificationDao.getAllNotifications().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getNotificationsByType(type: String): Flow<List<Notification>> {
        return notificationDao.getNotificationsByType(type).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getUnreadNotifications(): Flow<List<Notification>> {
        return notificationDao.getUnreadNotifications().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertNotification(notification: Notification) {
        notificationDao.insertNotification(notification.toEntity())
    }

    override suspend fun markAsRead(id: String) {
        notificationDao.markAsRead(id)
    }

    override suspend fun markAllAsRead() {
        notificationDao.markAllAsRead()
    }

    override suspend fun deleteNotification(id: String) {
        notificationDao.deleteNotificationById(id)
    }
}

