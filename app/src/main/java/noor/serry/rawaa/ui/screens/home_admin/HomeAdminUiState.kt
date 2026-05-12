package noor.serry.rawaa.ui.screens.home_admin

data class HomeAdminUiState(
    // From GET /api/auth/me  →  UserDto.name
    val adminName: String = "",

    // From AdminDashboardDto.university  →  AdminUniversityRefDto.name
    val universityName: String = "",

    // From AdminDashboardDto.stats  →  AdminStatsDto
    val totalStudents: Int = 0,       // AdminStatsDto.totalStudents
    val totalDoctors: Int = 0,        // AdminStatsDto.totalDoctors
    val totalEmployees: Int = 0,      // AdminStatsDto.totalEmployees
    val totalDepartments: Int = 0,    // AdminStatsDto.totalDepartments
    val activeCourses: Int = 0,       // AdminStatsDto.activeCourses
    val activeRegistrations: Int = 0, // AdminStatsDto.activeRegistrations
    val upcomingExams: Int = 0,       // AdminStatsDto.upcomingExams

    // From AdminDashboardDto.recentRegistrations  →  List<RecentRegistrationDto>
    val recentRegistrations: List<RecentRegistrationItem> = emptyList(),

    // Computed: totalStudents + totalDoctors + totalEmployees (all in DTO)
    val totalUsers: Int = 0,

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    /**
     * Maps directly to RecentRegistrationDto:
     *   studentName  → RecentRegistrationDto.studentName
     *   courseName   → RecentRegistrationDto.courseName
     *   courseCode   → RecentRegistrationDto.code
     *   registeredAt → RecentRegistrationDto.registeredAt
     */
    data class RecentRegistrationItem(
        val studentName: String,
        val courseName: String,
        val courseCode: String,
        val registeredAt: String,
    )
}
