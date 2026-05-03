package noor.serry.rawaa.ui.screens.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val universitySlug: String = "",
    val selectedRole: LoginRole = LoginRole.STUDENT,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    // Field-level errors
    val emailError: String? = null,
    val passwordError: String? = null,
    val slugError: String? = null,
    // General API error
    val generalError: String? = null,
)

enum class LoginRole(val label: String, val apiValue: String) {
    STUDENT("طالب", "student"),
    TEACHER("دكتور / موظف", "doctor"),
}
