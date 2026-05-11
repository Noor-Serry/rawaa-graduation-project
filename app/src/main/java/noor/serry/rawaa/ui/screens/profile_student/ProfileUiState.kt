package noor.serry.rawaa.ui.screens.profile_student

data class ProfileUiState(
    val fullName: String = "",           // UserDto.name
    val studentId: String = "",          // UserProfileDto.nationalId or UserDto.id
    val avatarUrl: String? = null,       // UserDto.avatar

    // Stats — computed from server data
    val studyYears: Int = 0,             // current year - UserProfileDto.enrollmentYear
    val completedCourses: Int = 0,       // StudentDashboardDto.courses.count { status == "completed" }
    val gpa: String = "",                // UserProfileDto.gpa or StudentDashboardDto.student.gpa

    // Academic info — all from UserProfileDto
    val faculty: String = "",            // UserProfileDto.departmentName
    val major: String = "",              // UserProfileDto.departmentName
    val level: String = "",              // UserProfileDto.level
    val enrollmentDate: String = "",     // UserProfileDto.enrollmentYear

    // Personal info — from UserDto and UserProfileDto
    val email: String = "",              // UserDto.email
    val phone: String = "",              // UserProfileDto.phone

    // Removed: birthDate — not in UserDto or UserProfileDto
    // Removed: address   — not in UserDto or UserProfileDto
    // Removed: achievementsCount — no achievements endpoint on server
    // Removed: certificatesCount — no certificates endpoint on server

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
