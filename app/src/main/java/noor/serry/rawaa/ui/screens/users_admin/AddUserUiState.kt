package noor.serry.rawaa.ui.screens.users_admin

/**
 * UI state for the Add-User bottom sheet / flow.
 *
 * Kept separate from [UsersAdminUiState] so the ViewModel that owns
 * this screen can be scoped independently (e.g. with a dedicated
 * Koin/Hilt scope) and not carry form state inside the list screen's
 * state object.
 *
 * Submission maps to:
 *   • POST /api/students   → [UniversityRepository.createStudent]  (when [selectedRole] == STUDENT)
 *   • POST /api/employees  → [UniversityRepository.createEmployee] (when [selectedRole] == DOCTOR | EMPLOYEE)
 */
data class AddUserUiState(

    // ── Role ──────────────────────────────────────────────────────────────────
    val selectedRole: NewUserRole = NewUserRole.STUDENT,

    // ── Common fields ─────────────────────────────────────────────────────────
    val name: String          = "",
    val email: String         = "",
    val password: String      = "",
    val phone: String         = "",

    // ── Department ────────────────────────────────────────────────────────────
    val departmentId: Int?    = null,
    val departmentName: String = "",
    /** Populated from GET /api/departments; passed down from [UsersAdminUiState.departments]. */
    val availableDepartments: List<UsersAdminUiState.DepartmentFilterItem> = emptyList(),
    val showDepartmentPicker: Boolean = false,

    // ── Student-only fields ───────────────────────────────────────────────────
    val level: String          = "1",
    val enrollmentYear: String = "2025",

    // ── Employee-only fields ──────────────────────────────────────────────────
    val roleTitle: String      = "",

    // ── Async / feedback ──────────────────────────────────────────────────────
    val isSubmitting: Boolean  = false,
    val errorMessage: String?  = null,

    // ── Validation (derived; computed via [validationErrors]) ────────────────
) {

    // ── Validation ────────────────────────────────────────────────────────────

    val nameError: String?
        get() = if (name.isBlank()) "الاسم مطلوب" else null

    val emailError: String?
        get() = when {
            email.isBlank()            -> "البريد مطلوب"
            !email.contains("@")       -> "صيغة البريد غير صحيحة"
            else                       -> null
        }

    val passwordError: String?
        get() = if (password.length < 6) "كلمة المرور 6 أحرف على الأقل" else null

    val departmentError: String?
        get() = if (departmentId == null) "القسم مطلوب" else null

    val roleTitleError: String?
        get() = if (selectedRole != NewUserRole.STUDENT && roleTitle.isBlank())
            "المسمى الوظيفي مطلوب" else null

    val isValid: Boolean
        get() = nameError == null &&
                emailError == null &&
                passwordError == null &&
                departmentError == null &&
                roleTitleError == null

    // ── Convenience mapper ────────────────────────────────────────────────────

    /** Flatten into [AddUserFormState] for the submit action. */
    fun toFormState() = AddUserFormState(
        selectedRole   = selectedRole,
        name           = name,
        email          = email,
        password       = password,
        phone          = phone,
        departmentId   = departmentId,
        departmentName = departmentName,
        level          = level,
        enrollmentYear = enrollmentYear,
        roleTitle      = roleTitle,
    )
}
