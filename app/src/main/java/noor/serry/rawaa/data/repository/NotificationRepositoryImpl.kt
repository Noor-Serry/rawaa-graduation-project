package noor.serry.rawaa.data.repository

import noor.serry.rawaa.data.mapper.toEntity
import noor.serry.rawaa.data.remote.ApiClient
import noor.serry.rawaa.domain.entity.NotificationEntity
import noor.serry.rawaa.domain.repository.NotificationRepository

class NotificationRepositoryImpl(
    private val api: ApiClient,
) : NotificationRepository {

    /**
     * GET /api/notifications
     * Response is { success, unread_count, data: [...], pagination: {...} }.
     * We use [NotificationsResponseDto] which matches this unique structure.
     */
    override suspend fun getNotifications(): List<NotificationEntity> {
        val response = api.getNotifications()
        return response.data.map { it.toEntity() }
    }

    /** PUT /api/notifications/read-all */
    override suspend fun markAllAsRead() {
        api.markAllNotificationsAsRead()
    }

    /**
     * DELETE /api/notifications/{id} for every notification the user has.
     * Fetches the current list first, then deletes each one individually.
     */
    override suspend fun deleteAll() {
        val notifications = api.getNotifications().data
        notifications.forEach { dto ->
            runCatching { api.deleteNotification(dto.id) }
        }
    }
}
