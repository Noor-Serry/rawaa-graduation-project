package noor.serry.rawaa.ui.screens.users_admin

/**
 * UI state for the Add-User screen.
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

    // ── University ────────────────────────────────────────────────────────────
    /** Resolved from GET /api/admin/dashboard during init; required by create-user endpoints. */
    val universitySlug: String = "",

    // ── Async / feedback ──────────────────────────────────────────────────────
    val isSubmitting: Boolean  = false,
    val errorMessage: String?  = null,
) {

    // ── Validation ────────────────────────────────────────────────────────────

    val nameError: String?
        get() = if (name.isBlank()) "الاسم مطلوب" else null

    val emailError: String?
        get() = when {
            email.isBlank()      -> "البريد مطلوب"
            !email.contains("@") -> "صيغة البريد غير صحيحة"
            else                 -> null
        }

    val passwordError: String?
        get() = if (password.length < 6) "كلمة المرور 6 أحرف على الأقل" else null

    val departmentError: String?
        get() = if (departmentId == null) "القسم مطلوب" else null

    val levelError: String?
        get() = if (selectedRole == NewUserRole.STUDENT) {
            val v = level.toIntOrNull()
            when {
                level.isBlank() -> "المستوى مطلوب"
                v == null       -> "المستوى يجب أن يكون رقماً"
                v < 1           -> "المستوى يجب أن يكون 1 على الأقل"
                else            -> null
            }
        } else null

    val enrollmentYearError: String?
        get() = if (selectedRole == NewUserRole.STUDENT) {
            val v = enrollmentYear.toIntOrNull()
            when {
                enrollmentYear.isBlank() -> "سنة الالتحاق مطلوبة"
                v == null                -> "سنة الالتحاق يجب أن تكون رقماً"
                else                     -> null
            }
        } else null

    val roleTitleError: String?
        get() = if (selectedRole != NewUserRole.STUDENT && roleTitle.isBlank())
            "المسمى الوظيفي مطلوب" else null

    val isValid: Boolean
        get() = nameError == null &&
                emailError == null &&
                passwordError == null &&
                departmentError == null &&
                levelError == null &&
                enrollmentYearError == null &&
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