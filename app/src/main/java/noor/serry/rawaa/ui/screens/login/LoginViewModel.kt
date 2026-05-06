package noor.serry.rawaa.ui.screens.login

import noor.serry.rawaa.data.repository.AuthRepositoryImpl
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class LoginViewModel(
    private val authRepository: AuthRepositoryImpl,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<LoginUiState, LoginEffect>(
    initialState = LoginUiState(),
    dispatcherProvider = dispatchers,
), LoginInteractionListener {

    override fun onEmailChange(value: String) =
        updateState { it.copy(email = value, emailError = null, generalError = null) }

    override fun onPasswordChange(value: String) =
        updateState { it.copy(password = value, passwordError = null, generalError = null) }

    override fun onUniversitySlugChange(value: String) = onSlugChange(value)

    fun onSlugChange(value: String) =
        updateState { it.copy(universitySlug = value, slugError = null) }

    override fun onRoleSelected(role: LoginRole) =
        updateState { it.copy(selectedRole = role) }

    override fun onPasswordVisibilityToggle() = togglePasswordVisibility()

    fun togglePasswordVisibility() =
        updateState { it.copy(isPasswordVisible = !it.isPasswordVisible) }

    override fun onLoginClick() = login()

    override fun onGoogleLoginClick() {
        // Trigger Google sign-in from the UI layer
    }

    override fun onNavigateToRegister() =
        sendNewNavigationEffect(LoginEffect.NavigateToRegister)

    override fun onForgotPasswordClick() = onNavigateToForgotPassword()

    fun onNavigateToForgotPassword() =
        sendNewNavigationEffect(LoginEffect.NavigateToForgotPassword)

    fun login() {
        val current = state.value
        var hasError = false

        if (current.email.isBlank()) {
            updateState { it.copy(emailError = "يرجى إدخال البريد الإلكتروني") }
            hasError = true
        }
        if (current.password.isBlank()) {
            updateState { it.copy(passwordError = "يرجى إدخال كلمة المرور") }
            hasError = true
        }
        if (hasError) return

        updateState { it.copy(isLoading = true, generalError = null) }
        tryToExecute(
            action = {
                authRepository.loginWithEmail(current.email, current.password, current.universitySlug)
            },
            onSuccess = {
                val effect = if (current.selectedRole == LoginRole.TEACHER)
                    LoginEffect.NavigateToTeacherHome
                else
                    LoginEffect.NavigateToStudentHome
                sendNewNavigationEffect(effect)
                updateState { it.copy(isLoading = false) }
            },
            onError = { e ->
                updateState { it.copy(isLoading = false, generalError = e.message ?: "فشل تسجيل الدخول") }
            },
            dispatcher = dispatchers.IO,
        )
    }



    suspend fun getSavedRole(): String? = authRepository.getSavedRole()
}