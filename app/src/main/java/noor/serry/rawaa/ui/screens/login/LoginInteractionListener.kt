package noor.serry.rawaa.ui.screens.login

interface LoginInteractionListener {
    fun onEmailChange(value: String)
    fun onPasswordChange(value: String)
    fun onUniversitySlugChange(value: String)
    fun onRoleSelected(role: LoginRole)
    fun onPasswordVisibilityToggle()
    fun onLoginClick()
    fun onNavigateToRegister()
    fun onForgotPasswordClick()
}
