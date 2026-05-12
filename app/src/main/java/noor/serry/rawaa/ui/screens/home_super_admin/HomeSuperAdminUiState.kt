package noor.serry.rawaa.ui.screens.home_super_admin

// ─────────────────────────────────────────────────────────────────────────────
// HomeSuperAdminUiState
//
// Data sources:
//   • adminName          ← SuperAdminDto.name         (POST /api/super/login)
//   • stats.*            ← PlatformStatsDto            (GET  /api/super/stats)
//   • universities       ← List<UniversityDto>[0..4]   (GET  /api/super/universities, perPage=5)
//   • universityAdmins   ← List<UniversityAdminDto>    (GET  /api/super/universities/{id}/admins)
// ─────────────────────────────────────────────────────────────────────────────

data class HomeSuperAdminUiState(

    // ── Identity ─────────────────────────────────────────────────────────────
    /** SuperAdminDto.name from the stored login response */
    val adminName: String = "",

    // ── Platform stats (PlatformStatsDto) ────────────────────────────────────
    val totalUniversities: Int = 0,       // PlatformStatsDto.totalUniversities
    val activeUniversities: Int = 0,      // PlatformStatsDto.activeUniversities
    val totalStudents: Int = 0,           // PlatformStatsDto.totalStudents
    val totalDoctors: Int = 0,            // PlatformStatsDto.totalDoctors
    val totalEmployees: Int = 0,          // PlatformStatsDto.totalEmployees
    val planTrial: Int = 0,               // PlatformStatsDto.planTrial
    val planBasic: Int = 0,               // PlatformStatsDto.planBasic
    val planPro: Int = 0,                 // PlatformStatsDto.planPro
    val planEnterprise: Int = 0,          // PlatformStatsDto.planEnterprise

    // ── Universities preview list (GET /api/super/universities, perPage = 5) ─
    // Only the first 5 are displayed on the home screen.
    // The full list lives in UniversitiesScreen / UniversitiesViewModel.
    val universities: List<UniversityItem> = emptyList(),
    val universitiesLoading: Boolean = false,

    // ── University Admins (GET /api/super/universities/{id}/admins) ───────────
    /** Which university is currently expanded to show its admins */
    val selectedUniversityId: Int? = null,
    val selectedUniversityName: String = "",
    val universityAdmins: List<UniversityAdminItem> = emptyList(),
    val adminsLoading: Boolean = false,

    // ── Page-level state ─────────────────────────────────────────────────────
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {

    // ── Derived ───────────────────────────────────────────────────────────────
    /** totalStudents + totalDoctors + totalEmployees */
    val totalUsers: Int get() = totalStudents + totalDoctors + totalEmployees

    // ─────────────────────────────────────────────────────────────────────────
    // Nested item models
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Maps to UniversityDto (preview subset shown on the home card).
     *   id           → UniversityDto.id
     *   name         → UniversityDto.name
     *   slug         → UniversityDto.slug
     *   plan         → UniversityDto.plan
     *   isActive     → UniversityDto.isActive  (1 = active)
     *   studentCount → UniversityDto.studentCount
     *   doctorCount  → UniversityDto.doctorCount
     *   planExpiresAt→ UniversityDto.planExpiresAt
     */
    data class UniversityItem(
        val id: Int,
        val name: String,
        val slug: String,
        val plan: String?,
        val isActive: Int,
        val studentCount: Int,
        val doctorCount: Int,
        val planExpiresAt: String?,
    ) {
        val isActiveBool: Boolean get() = isActive == 1
    }

    /**
     * Maps to UniversityAdminDto:
     *   id          → UniversityAdminDto.id
     *   name        → UniversityAdminDto.name
     *   email       → UniversityAdminDto.email
     *   isActive    → UniversityAdminDto.isActive  (1 = active)
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
}
