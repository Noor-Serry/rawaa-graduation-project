package noor.serry.rawaa.ui.screens.notifications

sealed interface NotificationsEffect {
    data class NavigateToDetails(val notificationId: String) : NotificationsEffect
}
