package noor.serry.rawaa.ui.screens.profile_student

data class ProfileUiState(
    // ── Display data ──────────────────────────────────────────────────────────
    val userId: Int = 0,                     // UserDto.id  — needed for updateStudent
    val fullName: String = "",               // UserDto.name
    val studentId: String = "",              // UserProfileDto.nationalId or UserDto.id
    val avatarUrl: String? = null,           // UserDto.avatar (URL string)

    // Stats
    val studyYears: Int = 0,                 // current year - UserProfileDto.enrollmentYear
    val completedCourses: Int = 0,           // StudentDashboardDto.courses.count { status == "completed" }
    val creditHours: Int = 0,                // StudentDashboardDto.creditHours
    val attendanceRate: Float = 0f,          // StudentDashboardDto.attendance.attendanceRate
    val gpa: String = "",                    // UserProfileDto.gpa or StudentDashboardDto.gpa

    // Academic info
    val faculty: String = "",                // UserProfileDto.departmentName
    val level: String = "",                  // UserProfileDto.level
    val enrollmentDate: String = "",         // UserProfileDto.enrollmentYear

    // Personal info
    val email: String = "",                  // UserDto.email
    val phone: String = "",                  // UserProfileDto.phone

    // ── Edit mode ─────────────────────────────────────────────────────────────
    val isEditMode: Boolean = false,
    val editName: String = "",
    val editPhone: String = "",
    val editEmail: String = "",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,

    // ── State ─────────────────────────────────────────────────────────────────
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

enum class ProfileField { NAME, EMAIL, PHONE }
