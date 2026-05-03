package noor.serry.rawaa.ui.screens.register

import android.util.Log
import noor.serry.rawaa.data.dto.DepartmentDto
import noor.serry.rawaa.data.repository.AuthRepositoryImpl
import noor.serry.rawaa.data.repository.DepartmentRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class RegisterViewModel(
    private val authRepository: AuthRepositoryImpl,
    private val dispatchers: DispatcherProvider,
    private val departmentRepository: DepartmentRepository,
) : BaseViewModel<RegisterUiState, RegisterEffect>(
    initialState = RegisterUiState(),
    dispatcherProvider = dispatchers,
), RegisterInteractionListener {

    // ── Page 0 — Personal Info ────────────────────────────────────────────────

    init {
        tryToExecute(
            action = { departmentRepository.getDepartments() },
            onSuccess = { departments ->
                updateState { it.copy(departments = departments) }
            }
        )
    }

    override fun onFullNameChange(v: String) =
        updateState { it.copy(fullName = v, fullNameError = null, generalError = null) }

    override fun onEmailChange(v: String) =
        updateState { it.copy(email = v, emailError = null, generalError = null) }

    override fun onPhoneChange(v: String) =
        updateState { it.copy(phone = v) }

    // ── Page 1 — Academic Info ────────────────────────────────────────────────

    override fun onUniversityChange(v: String) =
        updateState { it.copy(university = v, universityError = null) }

    override fun onRoleSelected(role: UserRole) =
        updateState { it.copy(selectedRole = role) }

    // ── Page 2 — Account Security ─────────────────────────────────────────────

    override fun onPasswordChange(v: String) =
        updateState { it.copy(password = v, passwordError = null) }

    override fun onConfirmPasswordChange(v: String) =
        updateState { it.copy(confirmPassword = v, passwordsMatch = it.password == v) }

    // ── Navigation ────────────────────────────────────────────────────────────

    override fun onNextPage() {
        val isValid = when (state.value.currentPage) {
            0 -> validatePersonalInfo()
            1 -> validateAcademicInfo()
            else -> true
        }
        if (isValid) updateState { it.copy(currentPage = it.currentPage + 1) }
    }

    override fun onPreviousPage() {
        if (state.value.currentPage > 0) {
            updateState { it.copy(currentPage = it.currentPage - 1) }
        } else {
            sendNewNavigationEffect(RegisterEffect.NavigateToLogin)
        }
    }

    override fun onNavigateToLogin() =
        sendNewNavigationEffect(RegisterEffect.NavigateToLogin)

    // ── Registration ──────────────────────────────────────────────────────────

    override fun onRegister() {
        if (!validateSecurity()) return
        val current = state.value
        updateState { it.copy(isLoading = true, generalError = null) }

        when (current.selectedRole) {
            UserRole.STUDENT -> registerStudent(current)
            UserRole.TEACHER -> registerDoctor(current)
            else -> updateState {
                it.copy(isLoading = false, generalError = "التسجيل متاح للطلاب والدكاترة فقط")
            }
        }
    }

    private fun registerStudent(current: RegisterUiState) {
        tryToExecute(
            action = {
                authRepository.registerStudent(
                    universitySlug = current.university,
                    name = current.fullName,
                    email = current.email,
                    password = current.password,
                    phone = current.phone,
                    nationalId = "1",
                    departmentId = 1,
                    level = 1,
                    enrollmentYear = 2000,
                )
            },
            onSuccess = {
                sendNewNavigationEffect(RegisterEffect.NavigateToHome)
                updateState { it.copy(isLoading = false) }
            },
            onError = { e ->
                updateState { it.copy(isLoading = false, generalError = e.message) }
            },
            dispatcher = dispatchers.IO,
        )
    }

    private fun registerDoctor(current: RegisterUiState) {
        Log.e("RegisterViewModel.kt", "registerDoctor")
        tryToExecute(
            action = {
                authRepository.registerDoctor(
                    universitySlug = current.university,
                    name = current.fullName,
                    email = current.email,
                    password = current.password,
                    phone = current.phone,
                    roleTitle = current.roleTitle,
                    salary = 6000.0,
                    departmentId = current.selectedDepartment!!.id,
                )
            },
            onSuccess = {
                sendNewNavigationEffect(RegisterEffect.NavigateToHome)
                updateState { it.copy(isLoading = false) }
            },
            onError = { e ->
                Log.e("RegisterViewModel.kt", "" + e.message)
                updateState { it.copy(isLoading = false, generalError = e.message) }
            },
            dispatcher = dispatchers.IO,
        )
    }

    // ── Google sign-up ────────────────────────────────────────────────────────

    override fun onGoogleSignUp() {
        // Trigger the Google sign-in flow from the UI layer.
        // The UI should call registerWithGoogle(idToken) once it receives the token.
        // sendNewEffect(RegisterEffect.LaunchGoogleSignIn)
    }

    fun registerWithGoogle(idToken: String) {
        val current = state.value
        updateState { it.copy(isLoading = true, generalError = null) }
        tryToExecute(
            action = {
                authRepository.loginWithGoogle(
                    idToken = idToken,
                    universitySlug = current.university,
                    role = current.selectedRole.apiValue,
                    departmentId = 1,
                )
            },
            onSuccess = {
                sendNewNavigationEffect(RegisterEffect.NavigateToHome)
                updateState { it.copy(isLoading = false) }
            },
            onError = { e ->
                updateState { it.copy(isLoading = false, generalError = e.message) }
            },
            dispatcher = dispatchers.IO,
        )
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private fun validatePersonalInfo(): Boolean {
        val current = state.value
        var valid = true

        if (current.fullName.isBlank()) {
            updateState { it.copy(fullNameError = "الاسم الكامل مطلوب") }
            valid = false
        }
        if (current.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(current.email)
                .matches()
        ) {
            updateState { it.copy(emailError = "البريد الإلكتروني غير صالح") }
            valid = false
        }
        return valid
    }

    private fun validateAcademicInfo(): Boolean {
        val current = state.value
        var valid = true

        if (current.university.isBlank()) {
            updateState { it.copy(universityError = "الرجاء إدخال رمز الجامعة") }
            valid = false
        }
        if (current.selectedDepartment == null) {
            updateState { it.copy(departmentError = "الرجاء اختيار القسم") }
            valid = false
        }
        if (current.selectedRole == UserRole.TEACHER && current.roleTitle.isBlank()) {
            updateState { it.copy(roleTitleError = "الرجاء إدخال اللقب الوظيفي") }
            valid = false
        }
        return valid
    }

    private fun validateSecurity(): Boolean {
        val current = state.value
        var valid = true

        if (current.password.length < 8) {
            updateState { it.copy(passwordError = "كلمة المرور يجب أن تكون 8 أحرف على الأقل") }
            valid = false
        }
        if (current.password != current.confirmPassword) {
            updateState {
                it.copy(
                    passwordsMatch = false,
                    passwordError = "كلمتا المرور غير متطابقتين"
                )
            }
            valid = false
        }
        return valid
    }

    override fun onDepartmentSelected(department: DepartmentDto) =
        updateState { it.copy(selectedDepartment = department, departmentError = null) }

    override fun onRoleTitleChange(v: String) =
        updateState { it.copy(roleTitle = v, roleTitleError = null) }

}