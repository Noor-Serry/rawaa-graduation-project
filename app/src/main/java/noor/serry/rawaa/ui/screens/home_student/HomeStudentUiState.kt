package noor.serry.rawaa.ui.screens.home_student

import noor.serry.rawaa.ui.screens.home_student.components.CourseProgressItem
import noor.serry.rawaa.ui.screens.home_student.components.QuickAction

data class HomeStudentUiState(
    val studentName: String = "",
    val gpa: String = "",
    val homeworkCount: Int = 0,
    val activeCoursesCount: Int = 0,
    val scheduleItems: List<ScheduleItem> = emptyList(),
    val homeworkItems: List<HomeworkItem> = emptyList(),
    val courseProgressItems: List<CourseProgressItem> = emptyList(),
    val quickActions: List<QuickAction> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    data class HomeworkItem(
        val title: String,
        val courseName: String,
        val deadline: String,
        val deadlineColor: DeadlineType = DeadlineType.TOMORROW,
    )

    data class ScheduleItem(
        val courseName: String,
        val professorName: String,
        val time: String,
        val location: String,
        val isOnline: Boolean = false,
    )

    enum class DeadlineType { TOMORROW, DAYS_LATER }
}
