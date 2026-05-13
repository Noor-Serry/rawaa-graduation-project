package noor.serry.rawaa.ui.screens.users_admin

/**
 * UI state for the Edit-User screen.
 *
 * Pre-populated from the detail endpoint, then mutated as the admin edits fields.
 *
 * Submission maps to:
 *   • PATCH /api/students/{id}   → [UniversityRepository.updateStudent]
 *   • PATCH /api/employees/{id}  → [UniversityRepository.updateEmployee]
 */
data class EditUserUiState(
    val isLoading: Boolean     = true,
    val isSubmitting: Boolean  = false,
    val errorMessage: String?  = null,

    // ── Identity (immutable after load) ───────────────────────────────────────
    val userId: Int            = 0,
    val userType: UsersAdminUiState.UserType = UsersAdminUiState.UserType.STUDENT,

    // ── Common editable fields ────────────────────────────────────────────────
    val name: String           = "",
    val email: String          = "",
    val phone: String          = "",
    val isActive: Boolean      = true,

    // ── Department ────────────────────────────────────────────────────────────
    val departmentId: Int?     = null,
    val departmentName: String = "",
    val availableDepartments: List<UsersAdminUiState.DepartmentFilterItem> = emptyList(),
    val showDepartmentPicker: Boolean = false,

    // ── Student-only ──────────────────────────────────────────────────────────
    val level: String          = "",
    val enrollmentYear: String = "",

    // ── Employee / Doctor-only ────────────────────────────────────────────────
    val roleTitle: String      = "",
) {

    // ── Inline validation ─────────────────────────────────────────────────────

    val nameError: String?
        get() = if (name.isBlank()) "الاسم مطلوب" else null

    val emailError: String?
        get() = when {
            email.isBlank()      -> "البريد مطلوب"
            !email.contains("@") -> "صيغة البريد غير صحيحة"
            else                 -> null
        }

    val roleTitleError: String?
        get() = if (userType != UsersAdminUiState.UserType.STUDENT && roleTitle.isBlank())
            "المسمى الوظيفي مطلوب" else null

    val isValid: Boolean
        get() = nameError == null && emailError == null && roleTitleError == null
}
