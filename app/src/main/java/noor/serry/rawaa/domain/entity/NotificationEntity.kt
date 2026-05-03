package noor.serry.rawaa.domain.entity

data class NotificationEntity(
    val id: String,
    val title: String,
    val body: String,
    val timeAgo: String,
    val type: NotificationType,
    val isRead: Boolean,
)

enum class NotificationType { GRADE, ASSIGNMENT, EXAM, ANNOUNCEMENT, DEADLINE }