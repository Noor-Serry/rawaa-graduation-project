package noor.serry.rawaa.ui.screens.users_admin

// ─────────────────────────────────────────────────────────────────────────────
// Role enum
// ─────────────────────────────────────────────────────────────────────────────

/**
 * The role the admin selects when creating a new user.
 * Maps to the POST body:
 *   STUDENT  → POST /api/students
 *   DOCTOR   → POST /api/employees  (role = "doctor")
 *   EMPLOYEE → POST /api/employees  (role = "employee")
 */
enum class NewUserRole(val labelAr: String) {
    STUDENT("طالب"),
    DOCTOR("دكتور"),
    EMPLOYEE("موظف"),
}

// ─────────────────────────────────────────────────────────────────────────────
// Form snapshot  (passed to the submit action)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Immutable snapshot of the add-user form at submission time.
 * Produced by [AddUserUiState.toFormState] and consumed by [AddUserViewModel].
 */
data class AddUserFormState(
    // Common
    val selectedRole: NewUserRole  = NewUserRole.STUDENT,
    val name: String               = "",
    val email: String              = "",
    val password: String           = "",
    val phone: String              = "",
    val departmentId: Int?         = null,
    val departmentName: String     = "",
    // Student-only
    val level: String              = "1",
    val enrollmentYear: String     = "2025",
    // Employee-only
    val roleTitle: String          = "",
)