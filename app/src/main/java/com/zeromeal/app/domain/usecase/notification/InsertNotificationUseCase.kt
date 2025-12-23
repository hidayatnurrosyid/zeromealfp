package com.zeromeal.app.domain.usecase.notification

import com.zeromeal.app.domain.model.Notification
import com.zeromeal.app.domain.repository.NotificationRepository
import javax.inject.Inject

class InsertNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(notification: Notification) {
        repository.insertNotification(notification)
    }
}

