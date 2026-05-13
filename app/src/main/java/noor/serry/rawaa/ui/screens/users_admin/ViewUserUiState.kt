package noor.serry.rawaa.ui.screens.users_admin

/**
 * UI state for the View-User (profile) screen.
 *
 * Populated from:
 *   • GET /api/students/{id}   → StudentDto
 *   • GET /api/employees/{id}  → EmployeeDto  (covers DOCTOR & EMPLOYEE)
 */
data class ViewUserUiState(
    val isLoading: Boolean        = true,
    val errorMessage: String?     = null,

    // ── Identity ──────────────────────────────────────────────────────────────
    val userId: Int               = 0,
    val userType: UsersAdminUiState.UserType = UsersAdminUiState.UserType.STUDENT,

    // ── Common fields ─────────────────────────────────────────────────────────
    val name: String              = "",
    val email: String             = "",
    val phone: String             = "",
    val isActive: Boolean         = true,
    val departmentName: String    = "",
    val createdAt: String         = "",

    // ── Student-only ──────────────────────────────────────────────────────────
    val level: Int?               = null,
    val enrollmentYear: Int?      = null,
    val gpa: Double?              = null,

    // ── Employee / Doctor-only ────────────────────────────────────────────────
    val roleTitle: String?        = null,
    val role: String              = "",
)
