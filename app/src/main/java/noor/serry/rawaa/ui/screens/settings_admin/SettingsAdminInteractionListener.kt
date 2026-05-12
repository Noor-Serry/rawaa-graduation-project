package noor.serry.rawaa.ui.screens.settings_admin

interface SettingsAdminInteractionListener {
    fun onLogoutClick()
    fun onChangePasswordConfirm(current: String, new: String, confirm: String)
    fun onDismissChangePassword()
}
