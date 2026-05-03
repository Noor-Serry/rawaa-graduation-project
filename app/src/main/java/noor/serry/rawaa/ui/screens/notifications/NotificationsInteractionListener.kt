package noor.serry.rawaa.ui.screens.notifications

interface NotificationsInteractionListener {
    fun onTabSelected(tab: NotificationsTab)
    fun onMarkAllAsRead()
    fun onDeleteAll()
    fun onMarkAsRead(notificationId: String)
    fun onViewDetails(notificationId: String)
    fun onToggleFilter(type: NotificationType)
}
