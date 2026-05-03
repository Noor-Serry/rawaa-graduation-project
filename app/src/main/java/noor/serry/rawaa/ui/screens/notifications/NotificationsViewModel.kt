package noor.serry.rawaa.ui.screens.notifications

import noor.serry.rawaa.domain.entity.NotificationEntity
import noor.serry.rawaa.domain.entity.NotificationType as DomainNotificationType
import noor.serry.rawaa.domain.usecase.DeleteAllNotificationsUseCase
import noor.serry.rawaa.domain.usecase.GetNotificationsUseCase
import noor.serry.rawaa.domain.usecase.MarkAllNotificationsReadUseCase
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class NotificationsViewModel(
    private val getNotifications: GetNotificationsUseCase,
    private val markAllRead: MarkAllNotificationsReadUseCase,
    private val deleteAll: DeleteAllNotificationsUseCase,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<NotificationsUiState, NotificationsEffect>(
    initialState = NotificationsUiState(isLoading = true),
    dispatcherProvider = dispatchers,
) ,NotificationsInteractionListener{

    init { load() }

    fun load() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            action = { getNotifications() },
            onSuccess = { list ->
                val items = list.map { it.toNotificationItem() }
                updateState {
                    it.copy(
                        isLoading = false,
                        notifications = items,
                        unreadCount = items.count { n -> !n.isRead },
                        todayCount = items.size,
                    )
                }
            },
            onError = { e -> updateState { it.copy(isLoading = false, errorMessage = e.message) } },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onTabSelected(tab: NotificationsTab) = updateState { it.copy(selectedTab = tab) }
    override fun onMarkAllAsRead() {
        TODO("Not yet implemented")
    }

    override fun onDeleteAll() {
        TODO("Not yet implemented")
    }

    override fun onMarkAsRead(notificationId: String) {
        TODO("Not yet implemented")
    }

    override fun onViewDetails(notificationId: String) {
        TODO("Not yet implemented")
    }

    override fun onToggleFilter(type: NotificationType) {
        TODO("Not yet implemented")
    }

    fun onFilterToggled(type: NotificationType) = updateState {
        val current = it.activeFilters.toMutableSet()
        if (!current.add(type)) current.remove(type)
        it.copy(activeFilters = current)
    }

    fun markAllAsRead() {
        tryToExecute(
            action = { markAllRead() },
            onSuccess = { load() },
            dispatcher = dispatchers.IO,
        )
    }

    fun deleteAllNotifications() {
        tryToExecute(
            action = { deleteAll() },
            onSuccess = { updateState { it.copy(notifications = emptyList(), unreadCount = 0, todayCount = 0) } },
            dispatcher = dispatchers.IO,
        )
    }

    fun onNotificationClick(id: String) = sendNewEffect(NotificationsEffect.NavigateToDetails(id))
}

private fun NotificationEntity.toNotificationItem() = NotificationItem(
    id = id,
    title = title,
    body = body,
    timeAgo = timeAgo,
    type = when (type) {
        DomainNotificationType.GRADE        -> NotificationType.GRADE
        DomainNotificationType.ASSIGNMENT   -> NotificationType.HOMEWORK
        DomainNotificationType.EXAM         -> NotificationType.EXAM
        DomainNotificationType.ANNOUNCEMENT -> NotificationType.ANNOUNCEMENT
        DomainNotificationType.DEADLINE     -> NotificationType.HOMEWORK
    },
    isRead = isRead,
)
