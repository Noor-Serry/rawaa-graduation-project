package noor.serry.rawaa.ui.screens.home_teacher

data class HomeTeacherUiState(
    val isLoading: Boolean = true,
    val teacherName: String = "",
    val pendingTasks: Int = 0,
    val totalStudents: Int = 0,
    val activeCourses: Int = 0,
    val todaySessions: List<TeacherSessionUiModel> = emptyList(),
    val pendingAssignments: List<TeacherPendingAssignmentUiModel> = emptyList(),
    val courses: List<TeacherCourseSummaryUiModel> = emptyList(),
    val weeklySubmissionRate: Int = 0,
    val weeklyAttendanceRate: Int = 0,
)

data class TeacherSessionUiModel(
    val id: String,
    val courseName: String,
    val time: String,
    val location: String,
    val studentsCount: Int,
)

data class TeacherPendingAssignmentUiModel(
    val id: String,
    val title: String,
    val courseName: String,
    val pendingCount: Int,
    val deadline: String,
)

data class TeacherCourseSummaryUiModel(
    val id: String,
    val name: String,
    val averageGrade: Int,
    val totalAssignments: Int,
    val totalStudents: Int,
    val averageProgress: Float,
)