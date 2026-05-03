package noor.serry.rawaa.domain.repository

import noor.serry.rawaa.domain.entity.NotificationEntity

interface NotificationRepository {
    suspend fun getNotifications(): List<NotificationEntity>
    suspend fun markAllAsRead()
    suspend fun deleteAll()
}