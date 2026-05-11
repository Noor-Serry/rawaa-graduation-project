package noor.serry.rawaa.ui.screens.home_student

import noor.serry.rawaa.ui.screens.home_student.components.CourseProgressItem

data class HomeStudentUiState(
    val studentName: String = "",
    val gpa: String = "",
    val activeCoursesCount: Int = 0,
    val upcomingExamsCount: Int = 0,
    val scheduleItems: List<ScheduleItem> = emptyList(),
    val upcomingExams: List<ExamItem> = emptyList(),
    val courseProgressItems: List<CourseProgressItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    // Removed: homeworkCount — no homework API endpoint exists in the server
    // Removed: homeworkItems — same reason
    // Removed: quickActions — these were static labels with no server data

    data class ScheduleItem(
        val courseName: String,
        val professorName: String,
        val time: String,
        val location: String,
        val type: SessionType = SessionType.LECTURE,
    )

    data class ExamItem(
        val title: String,
        val courseName: String,
        val courseCode: String,
        val startAt: String,
        val totalMarks: Float?,
        val type: String,
    )

    enum class SessionType { LECTURE, LAB }
}
