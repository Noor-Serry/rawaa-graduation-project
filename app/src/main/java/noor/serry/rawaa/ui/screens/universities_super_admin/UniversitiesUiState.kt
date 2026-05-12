package noor.serry.rawaa.ui.screens.universities_super_admin

// ─────────────────────────────────────────────────────────────────────────────
// UniversitiesUiState
//
// Drives the full University Management screen.
//
// Data sources:
//   • universities    ← PaginatedResponse<UniversityDto>   GET /api/super/universities
//   • detail          ← ApiResponse<UniversityDto>          GET /api/super/universities/{id}
//   • admins          ← ApiResponse<List<UniversityAdminDto>> GET /api/super/universities/{id}/admins
//   • Create          POST /api/super/universities
//   • Update          PUT  /api/super/universities/{id}
//   • Activate        PUT  /api/super/universities/{id}/activate
//   • Deactivate      PUT  /api/super/universities/{id}/deactivate
//   • Change plan     PUT  /api/super/universities/{id}/plan
// ─────────────────────────────────────────────────────────────────────────────

data class UniversitiesUiState(

    // ── List ─────────────────────────────────────────────────────────────────
    val universities: List<UniversityItem> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,         // pagination "load next page"

    // ── Pagination ────────────────────────────────────────────────────────────
    val currentPage: Int = 1,
    val lastPage: Int = 1,
    val totalCount: Int = 0,

    // ── Filters ───────────────────────────────────────────────────────────────
    val searchQuery: String = "",
    val filterPlan: String? = null,             // null = all | "trial"|"basic"|"pro"|"enterprise"
    val filterIsActive: Int? = null,            // null = all | 1 = active | 0 = inactive

    // ── Detail / admin panel ──────────────────────────────────────────────────
    val selectedUniversity: UniversityDetailItem? = null,
    val admins: List<UniversityAdminItem> = emptyList(),
    val isAdminsLoading: Boolean = false,
    val isDetailLoading: Boolean = false,

    // ── Create / Edit sheet ───────────────────────────────────────────────────
    val showCreateSheet: Boolean = false,
    val showEditSheet: Boolean = false,
    val editTarget: UniversityItem? = null,     // university being edited

    // ── Change-plan dialog ────────────────────────────────────────────────────
    val showPlanDialog: Boolean = false,
    val planDialogTarget: UniversityItem? = null,

    // ── Action loading (activate / deactivate / change-plan) ─────────────────
    val actionLoadingId: Int? = null,           // id of university undergoing an action

    // ── Errors & feedback ─────────────────────────────────────────────────────
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val createForm: UniversityFormState = UniversityFormState(),
    val editForm: UniversityFormState = UniversityFormState(),

    // ── Change-plan form ──────────────────────────────────────────────────────
    val changePlanForm: ChangePlanForm = ChangePlanForm(),
) {

    /** True when there are more pages to load */
    val canLoadMore: Boolean get() = currentPage < lastPage

    // ─────────────────────────────────────────────────────────────────────────
    // Nested models
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * List-row model — maps UniversityDto.
     *
     *   id             → UniversityDto.id
     *   name           → UniversityDto.name
     *   slug           → UniversityDto.slug
     *   plan           → UniversityDto.plan
     *   isActive       → UniversityDto.isActive  (1 = active)
     *   studentCount   → UniversityDto.studentCount ?: stats?.totalStudents
     *   doctorCount    → UniversityDto.doctorCount  ?: stats?.totalDoctors
     *   employeeCount  → UniversityDto.employeeCount ?: stats?.totalEmployees
     *   departmentCount→ UniversityDto.departmentCount ?: stats?.totalDepartments
     *   activeCourses  → UniversityDto.courseCount  ?: stats?.activeCourses
     *   planExpiresAt  → UniversityDto.planExpiresAt
     *   maxStudents    → UniversityDto.maxStudents
     *   maxStaff       → UniversityDto.maxStaff
     *   createdAt      → UniversityDto.createdAt
     */
    data class UniversityItem(
        val id: Int,
        val name: String,
        val slug: String,
        val plan: String?,
        val isActive: Int,
        val studentCount: Int,
        val doctorCount: Int,
        val employeeCount: Int,
        val departmentCount: Int,
        val activeCourses: Int,
        val planExpiresAt: String?,
        val maxStudents: Int,
        val maxStaff: Int,
        val createdAt: String?,
        // Raw fields kept for the edit form
        val nameEn: String?,
        val email: String?,
        val phone: String?,
        val address: String?,
        val country: String?,
    ) {
        val isActiveBool: Boolean get() = isActive == 1
    }

    /**
     * Full detail model — same as UniversityItem but includes nested stats
     * returned by GET /api/super/universities/{id}.
     */
    data class UniversityDetailItem(
        val base: UniversityItem,
        val admins: List<UniversityAdminItem> = emptyList(),
    )

    /**
     * Maps UniversityAdminDto.
     *   id          → UniversityAdminDto.id
     *   name        → UniversityAdminDto.name
     *   email       → UniversityAdminDto.email
     *   isActive    → UniversityAdminDto.isActive
     *   lastLoginAt → UniversityAdminDto.lastLoginAt
     *   createdAt   → UniversityAdminDto.createdAt
     */
    data class UniversityAdminItem(
        val id: Int,
        val name: String,
        val email: String,
        val isActive: Int,
        val lastLoginAt: String?,
        val createdAt: String?,
    ) {
        val isActiveBool: Boolean get() = isActive == 1
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Create / Edit form state
    // Kept inside UiState so the sheet survives recomposition.
    // ─────────────────────────────────────────────────────────────────────────

    data class UniversityFormState(
        val name: String = "",
        val nameEn: String = "",
        val slug: String = "",
        val email: String = "",
        val phone: String = "",
        val address: String = "",
        val country: String = "",
        val plan: String = "trial",
        val planExpiresAt: String = "",
        val maxStudents: String = "500",
        val maxStaff: String = "50",
        // Create-only: first admin credentials
        val adminName: String = "",
        val adminEmail: String = "",
        val adminPassword: String = "",
        // Validation
        val nameError: String? = null,
        val slugError: String? = null,
        val adminNameError: String? = null,
        val adminEmailError: String? = null,
        val adminPasswordError: String? = null,
    )



    // ─────────────────────────────────────────────────────────────────────────
    // Change-plan dialog state
    // ─────────────────────────────────────────────────────────────────────────

    data class ChangePlanForm(
        val plan: String = "trial",
        val planExpiresAt: String = "",
        val maxStudents: String = "",
        val maxStaff: String = "",
    )

}
