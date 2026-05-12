package noor.serry.rawaa.ui.screens.add_university_super_admin

// ─────────────────────────────────────────────────────────────────────────────
// AddUniversityUiState
//
// Drives the standalone Add-University screen.
//
// API endpoint:   POST /api/super/universities   →  CreateUniversityRequest
// ─────────────────────────────────────────────────────────────────────────────

data class AddUniversityUiState(

    // ── University info ───────────────────────────────────────────────────────
    val name: String = "",
    val nameEn: String = "",
    val slug: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val country: String = "",

    // ── Plan ──────────────────────────────────────────────────────────────────
    /** One of: "trial" | "basic" | "pro" | "enterprise" */
    val plan: String = "trial",
    val planExpiresAt: String = "",
    val maxStudents: String = "500",
    val maxStaff: String = "50",

    // ── First admin ───────────────────────────────────────────────────────────
    val adminName: String = "",
    val adminEmail: String = "",
    val adminPassword: String = "",
    val isPasswordVisible: Boolean = false,

    // ── Page-level state ──────────────────────────────────────────────────────
    val isSubmitting: Boolean = false,

    // ── Field-level validation errors ─────────────────────────────────────────
    val nameError: String? = null,
    val slugError: String? = null,
    val emailError: String? = null,
    val adminNameError: String? = null,
    val adminEmailError: String? = null,
    val adminPasswordError: String? = null,
) {

    /** True when the minimum required fields are filled (no validation errors). */
    val canSubmit: Boolean
        get() = name.isNotBlank()
            && slug.isNotBlank()
            && adminName.isNotBlank()
            && adminEmail.isNotBlank()
            && adminPassword.isNotBlank()
            && nameError == null
            && slugError == null
            && emailError == null
            && adminNameError == null
            && adminEmailError == null
            && adminPasswordError == null
            && !isSubmitting

    companion object {
        val PLAN_OPTIONS = listOf("trial", "basic", "pro", "enterprise")
    }
}
