package noor.serry.rawaa.ui.screens.notifications

import noor.serry.rawaa.data.dto.NotificationDto
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class NotificationsViewModel(
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<NotificationsUiState, NotificationsEffect>(
    initialState = NotificationsUiState(isLoading = true),
    dispatcherProvider = dispatchers,
), NotificationsInteractionListener {

    init { load() }

    fun load() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            // GET /api/notifications  → NotificationsResponseDto (data: List<NotificationDto>)
            action = { repository.getNotifications().data ?: emptyList() },
            onSuccess = { list ->
                val items = list.map { it.toNotificationItem() }
                updateState {
                    it.copy(
                        isLoading   = false,
                        notifications = items,
                        unreadCount = items.count { n -> !n.isRead },
                        todayCount  = items.size,
                    )
                }
            },
            onError = { e -> updateState { it.copy(isLoading = false, errorMessage = e.message) } },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onTabSelected(tab: NotificationsTab) = updateState { it.copy(selectedTab = tab) }

    override fun onMarkAllAsRead() = markAllAsRead()

    /**
     * "Delete all" — the server has no bulk-delete endpoint (/api/notifications/delete-all
     * does not exist). We delete each notification individually via DELETE /api/notifications/{id},
     * then reload. For large lists this could be slow; a local clear is applied immediately
     * as optimistic UI.
     */
    override fun onDeleteAll() {
        val ids = state.value.notifications.map { it.id }
        // Optimistic clear
        updateState { it.copy(notifications = emptyList(), unreadCount = 0, todayCount = 0) }
        ids.forEach { id ->
            tryToExecute(
                action = { repository.deleteNotification(id.toInt()) },
                onSuccess = { /* individual deletes, no reload needed */ },
                dispatcher = dispatchers.IO,
            )
        }
    }

    override fun onMarkAsRead(notificationId: String) {
        // PUT /api/notifications/{id}/read
        tryToExecute(
            action    = { repository.markNotificationRead(notificationId.toInt()) },
            onSuccess = { load() },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onViewDetails(notificationId: String) {
        sendNewEffect(NotificationsEffect.NavigateToDetails(notificationId))
    }

    override fun onToggleFilter(type: NotificationType) {
        updateState {
            val current = it.activeFilters.toMutableSet()
            if (!current.add(type)) current.remove(type)
            it.copy(activeFilters = current)
        }
    }

    fun markAllAsRead() {
        // PUT /api/notifications/read-all
        tryToExecute(
            action     = { repository.markAllNotificationsRead() },
            onSuccess  = { load() },
            dispatcher = dispatchers.IO,
        )
    }

    fun onNotificationClick(id: String) = sendNewEffect(NotificationsEffect.NavigateToDetails(id))
}

// ── Mapper ────────────────────────────────────────────────────────────────────

/**
 * Maps NotificationDto.type (server string) to NotificationType enum.
 *
 * Server type values: "grade", "assignment", "exam", "announcement"
 * Note: enum was renamed HOMEWORK → ASSIGNMENT to match the server type "assignment".
 */
private fun NotificationDto.toNotificationItem() = NotificationItem(
    id      = id.toString(),
    title   = title,
    body    = body,
    timeAgo = createdAt ?: "",
    type    = when (type.lowercase()) {
        "grade"        -> NotificationType.GRADE
        "assignment"   -> NotificationType.ASSIGNMENT
        "exam"         -> NotificationType.EXAM
        "announcement" -> NotificationType.ANNOUNCEMENT
        else           -> NotificationType.ANNOUNCEMENT
    },
    isRead = isRead == 1,
)
