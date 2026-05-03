package noor.serry.rawaa.domain.usecase

import noor.serry.rawaa.domain.entity.NotificationEntity
import noor.serry.rawaa.domain.repository.NotificationRepository

class GetNotificationsUseCase(private val repository: NotificationRepository) {
    suspend operator fun invoke(): List<NotificationEntity> = repository.getNotifications()
}

class MarkAllNotificationsReadUseCase(private val repository: NotificationRepository) {
    suspend operator fun invoke() = repository.markAllAsRead()
}

class DeleteAllNotificationsUseCase(private val repository: NotificationRepository) {
    suspend operator fun invoke() = repository.deleteAll()
}