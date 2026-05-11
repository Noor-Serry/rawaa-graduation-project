package noor.serry.rawaa.ui.screens.studentScreens.menu

data class MenuUiState(
    val userName: String = "",
    val userRole: String = "",
    val userInitial: String = "",
    val isLoading: Boolean = false,
    val isOpen: Boolean = false,
    val isLoggingOut: Boolean = false,
    val loggedOut: Boolean = false,
    val errorMessage: String? = null,
    val unreadNotificationCount: Int = 0,
)
