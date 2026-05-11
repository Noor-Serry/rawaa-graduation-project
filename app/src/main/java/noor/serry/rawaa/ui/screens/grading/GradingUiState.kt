package noor.serry.rawaa.ui.screens.grading

import noor.serry.rawaa.data.dto.CourseDto

data class GradingUiState(
    val isLoading: Boolean = true,
    val gradedCount: Int = 0,
    val pendingCount: Int = 0,
    val searchQuery: String = "",
    val selectedTab: GradingTab = GradingTab.PENDING,
    val pendingAssignments: List<GradingItem> = emptyList(),
    val gradedAssignments: List<GradingItem> = emptyList(),
    val error: String? = null,
)

enum class GradingTab { PENDING, GRADED }

data class GradingItem(
    val courseId: Int,
    val assignmentTitle: String,
    val courseName: String,
    val deadlineDaysAgo: String,
    val totalPoints: Int,
    val submittedCount: Int,
    val totalStudents: Int,
    val submittedPercent: Int,
    val avgGradingMinutes: Int,
    val isGraded: Boolean = false,
    val avgGrade: Int? = null,
)

sealed interface GradingEffect {
    data class StartGrading(val courseId: Int) : GradingEffect
    data class ShowError(val message: String) : GradingEffect
}
