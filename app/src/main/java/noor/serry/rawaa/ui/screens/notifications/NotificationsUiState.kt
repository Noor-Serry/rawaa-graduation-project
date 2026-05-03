package noor.serry.rawaa.ui.screens.notifications

data class NotificationsUiState(
    val todayCount: Int = 0,
    val unreadCount: Int = 0,
    val selectedTab: NotificationsTab = NotificationsTab.ALL,
    val activeFilters: Set<NotificationType> = emptySet(),
    val notifications: List<NotificationItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    val displayedNotifications: List<NotificationItem>
        get() {
            val tabFiltered = when (selectedTab) {
                NotificationsTab.ALL    -> notifications
                NotificationsTab.UNREAD -> notifications.filter { !it.isRead }
            }
            return if (activeFilters.isEmpty()) tabFiltered
            else tabFiltered.filter { it.type in activeFilters }
        }

    val allCount: Int get() = notifications.size
}

enum class NotificationsTab { ALL, UNREAD }

enum class NotificationType(val label: String) {
    GRADE("الدرجات"),
    HOMEWORK("الواجبات"),
    EXAM("الاختبارات"),
    ANNOUNCEMENT("الإعلانات"),
}

data class NotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val timeAgo: String,
    val type: NotificationType,
    val isRead: Boolean = false,
)
