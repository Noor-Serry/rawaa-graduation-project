package noor.serry.rawaa.ui

/**
 * Represents the three possible startup states of the app.
 *
 * [Loading]        – DataStore read is still in-flight; show splash / keep
 *                    the splash screen condition active.
 * [ShowOnboarding] – First-ever launch; user has never seen onboarding.
 * [ShowMain]       – Onboarding done AND user is logged in; go straight to
 *                    the main screen. [role] lets the root nav decide which
 *                    entry point to open (student / doctor / admin …).
 * [ShowAuth]       – Onboarding done but no valid token; go to login/register.
 */
sealed interface MainUiState {
    data object Loading : MainUiState
    data object ShowOnboarding : MainUiState
    data class ShowMain(val role: String) : MainUiState
    data object ShowAuth : MainUiState
}