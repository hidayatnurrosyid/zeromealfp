package com.zeromeal.app.domain.usecase.notification

import com.zeromeal.app.domain.model.Notification
import com.zeromeal.app.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationsByTypeUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(type: String): Flow<List<Notification>> {
        return repository.getNotificationsByType(type)
    }
}

