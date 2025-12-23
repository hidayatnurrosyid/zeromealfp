package com.zeromeal.app.data.mapper

import com.zeromeal.app.data.local.entity.NotificationEntity
import com.zeromeal.app.domain.model.Notification
import com.zeromeal.app.domain.model.NotificationType

fun NotificationEntity.toDomain(): Notification {
    return Notification(
        id = id,
        type = NotificationType.valueOf(type),
        title = title,
        message = message,
        timestamp = timestamp,
        isRead = isRead,
        actionText = actionText,
        actionData = actionData
    )
}

fun Notification.toEntity(): NotificationEntity {
    return NotificationEntity(
        id = id,
        type = type.name,
        title = title,
        message = message,
        timestamp = timestamp,
        isRead = isRead,
        actionText = actionText,
        actionData = actionData
    )
}

