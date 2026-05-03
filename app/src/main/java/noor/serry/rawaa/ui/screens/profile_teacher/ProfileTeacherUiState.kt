package noor.serry.rawaa.ui.screens.profile_teacher

data class ProfileTeacherUiState(
    val isLoading: Boolean = true,
    val employeeId: String = "",
    val name: String = "",
    val department: String = "",
    val specialization: String = "",
    val degree: String = "",
    val experienceYears: Int = 0,
    val enrollmentDate: String = "",
    val email: String = "",
    val phone: String = "",
    val office: String = "",
    val officeHours: String = "",
    val totalStudents: Int = 0,
    val activeCourses: Int = 0,
    val errorMessage: String? = null,
    val currentCourses: List<CourseRefUiModel> = emptyList(),
    val achievements: List<AchievementUiModel> = emptyList(),
)

data class CourseRefUiModel(
    val name: String,
    val code: String,
    val studentsCount: Int,
)

data class AchievementUiModel(
    val title: String,
    val year: String,
)