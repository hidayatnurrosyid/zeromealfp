package com.zeromeal.app.domain.usecase.notification

import com.zeromeal.app.domain.repository.NotificationRepository
import javax.inject.Inject

class MarkAllNotificationsAsReadUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke() {
        repository.markAllAsRead()
    }
}
