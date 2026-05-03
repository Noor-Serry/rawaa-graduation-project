package noor.serry.rawaa.ui.screens.profile_student

data class ProfileUiState(
    val fullName: String = "",
    val studentId: String = "",
    val avatarUrl: String? = null,
    // Stats
    val studyYears: Int = 0,
    val completedCourses: Int = 0,
    val gpa: String = "",
    // Academic info
    val faculty: String = "",
    val major: String = "",
    val level: String = "",
    val enrollmentDate: String = "",
    // Personal info
    val email: String = "",
    val phone: String = "",
    val birthDate: String = "",
    val address: String = "",
    // Bottom cards
    val achievementsCount: Int = 0,
    val certificatesCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
