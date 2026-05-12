package noor.serry.rawaa.ui.screens.settings_admin

sealed interface SettingsAdminEffect {
    data object LoggedOut : SettingsAdminEffect
}
