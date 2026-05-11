package noor.serry.rawaa.ui.screens.login

import noor.serry.rawaa.data.dto.LoginRequest
import noor.serry.rawaa.data.dto.SuperAdminLoginRequest
import noor.serry.rawaa.data.local.TokenDataStore
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class LoginViewModel(
    private val repository: UniversityRepository,
    private val tokenDataStore: TokenDataStore,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<LoginUiState, LoginEffect>(
    initialState = LoginUiState(),
    dispatcherProvider = dispatchers,
), LoginInteractionListener {

    override fun onEmailChange(value: String) =
        updateState { it.copy(email = value, emailError = null, generalError = null) }

    override fun onPasswordChange(value: String) =
        updateState { it.copy(password = value, passwordError = null, generalError = null) }

    override fun onUniversitySlugChange(value: String) =
        updateState { it.copy(universitySlug = value, slugError = null) }

    override fun onRoleSelected(role: LoginRole) =
        updateState { it.copy(selectedRole = role) }

    override fun onPasswordVisibilityToggle() =
        updateState { it.copy(isPasswordVisible = !it.isPasswordVisible) }

    override fun onLoginClick() = login()

    override fun onGoogleLoginClick() {
        // Trigger Google sign-in from the UI layer
    }

    override fun onNavigateToRegister() =
        sendNewNavigationEffect(LoginEffect.NavigateToRegister)

    override fun onForgotPasswordClick() =
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

        // SuperAdmin uses a dedicated login endpoint and has no university slug
        if (current.selectedRole == LoginRole.ADMIN &&
            current.universitySlug.isBlank()
        ) {
            loginAsSuperAdmin(current)
        } else {
            loginAsUniversityUser(current)
        }
    }

    /** Regular university users: student / doctor / employee / admin */
    private fun loginAsUniversityUser(current: LoginUiState) {
        tryToExecute(
            action = {
                val slug = current.universitySlug.takeIf { it.isNotBlank() } ?: "default"
                val response = repository.login(LoginRequest(current.email, current.password, slug))
                val authData = response.data ?: error("فشل تسجيل الدخول: لا توجد بيانات")
                tokenDataStore.saveToken(authData.token)
                tokenDataStore.saveRole(authData.user.role)
                authData.user.role
            },
            onSuccess = { role ->
                val effect = roleToEffect(role, current.selectedRole)
                sendNewNavigationEffect(effect)
                updateState { it.copy(isLoading = false) }
            },
            onError = { e ->
                updateState { it.copy(isLoading = false, generalError = e.message ?: "فشل تسجيل الدخول") }
            },
            dispatcher = dispatchers.IO,
        )
    }

    /** SuperAdmin: POST /api/super/login — no slug required */
    private fun loginAsSuperAdmin(current: LoginUiState) {
        tryToExecute(
            action = {
                val response = repository.superAdminLogin(
                        current.email,
                        current.password
                )
                val authData = response.data ?: error("فشل تسجيل الدخول: لا توجد بيانات")
                tokenDataStore.saveToken(authData.token)
                tokenDataStore.saveRole("super")
                "super"
            },
            onSuccess = { _ ->
                sendNewNavigationEffect(LoginEffect.NavigateToSuperAdminHome)
                updateState { it.copy(isLoading = false) }
            },
            onError = { e ->
                updateState { it.copy(isLoading = false, generalError = e.message ?: "فشل تسجيل الدخول") }
            },
            dispatcher = dispatchers.IO,
        )
    }

    /**
     * Maps the role string returned by the API to the correct navigation effect.
     * Falls back to the role the user selected on the login screen when the API
     * role is ambiguous (e.g. "employee" is treated as teacher/staff).
     */
    private fun roleToEffect(apiRole: String, selectedRole: LoginRole): LoginEffect =
        when (apiRole) {
            "student"  -> LoginEffect.NavigateToStudentHome
            "doctor"   -> LoginEffect.NavigateToTeacherHome
            "employee" -> LoginEffect.NavigateToTeacherHome   // employee shares Teacher entry point
            "admin"    -> LoginEffect.NavigateToAdminHome
            "super"    -> LoginEffect.NavigateToSuperAdminHome
            else       -> when (selectedRole) {
                LoginRole.STUDENT  -> LoginEffect.NavigateToStudentHome
                LoginRole.TEACHER  -> LoginEffect.NavigateToTeacherHome
                LoginRole.EMPLOYEE -> LoginEffect.NavigateToTeacherHome
                LoginRole.ADMIN    -> LoginEffect.NavigateToAdminHome
            }
        }

    suspend fun getSavedRole(): String? = tokenDataStore.getRole()
}
