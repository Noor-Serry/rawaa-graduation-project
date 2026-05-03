package noor.serry.rawaa.ui.screens.courses_teacher

data class CoursesTeacherUiState(
    val isLoading: Boolean = false,
    val activeCourses: List<TeacherCourseUiModel> = emptyList(),
    val archivedCourses: List<TeacherCourseUiModel> = emptyList(),
    val totalPendingGrades: Int = 0,
    val totalStudents: Int = 0,
    val activeCourseCount: Int = 0,
    val selectedTab: CoursesTeacherTab = CoursesTeacherTab.ACTIVE,
    val errorMessage: String? = null,
) {
    val displayedCourses: List<TeacherCourseUiModel>
        get() = if (selectedTab == CoursesTeacherTab.ACTIVE) activeCourses else archivedCourses
}

enum class CoursesTeacherTab { ACTIVE, ARCHIVED }

data class TeacherCourseUiModel(
    val courseId: String,
    val courseCode: String,
    val courseName: String,
    val semester: String = "",
    val totalStudents: Int = 0,
    val totalAssignments: Int = 0,
    val pendingGrades: Int = 0,
    val averageGrade: Int = 0,
    val averageProgress: Float = 0f,
)
