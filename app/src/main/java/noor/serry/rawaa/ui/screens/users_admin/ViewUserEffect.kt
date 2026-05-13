package noor.serry.rawaa.ui.screens.users_admin

/**
 * One-time side-effects emitted by [ViewUserViewModel].
 */
sealed interface ViewUserEffect {
    /** Navigate back (back button tapped). */
    data object NavigateBack : ViewUserEffect

    /** Navigate to the edit screen for this user. */
    data class NavigateToEdit(
        val userId: Int,
        val userType: UsersAdminUiState.UserType,
    ) : ViewUserEffect

    /** Show a toast / snackbar error without leaving the screen. */
    data class ShowError(val message: String) : ViewUserEffect
}
