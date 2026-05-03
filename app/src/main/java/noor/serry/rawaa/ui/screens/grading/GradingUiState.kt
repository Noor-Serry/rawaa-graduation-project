package noor.serry.rawaa.ui.screens.grading

data class GradingUiState(
    val isLoading: Boolean = true,
    val pendingAssignments: List<PendingAssignmentUiModel> = emptyList(),
    val gradedAssignments: List<GradedAssignmentUiModel> = emptyList(),
    val totalGradedCount: Int = 0,
    val totalPendingCount: Int = 0,
    val selectedTab: GradingTab = GradingTab.PENDING,
    val searchQuery: String = "",
) {
    val displayedPending: List<PendingAssignmentUiModel>
        get() = if (searchQuery.isBlank()) pendingAssignments
        else pendingAssignments.filter { it.title.contains(searchQuery) || it.courseName.contains(searchQuery) }

    val displayedGraded: List<GradedAssignmentUiModel>
        get() = if (searchQuery.isBlank()) gradedAssignments
        else gradedAssignments.filter { it.title.contains(searchQuery) || it.courseName.contains(searchQuery) }
}

enum class GradingTab { PENDING, GRADED }

data class PendingAssignmentUiModel(
    val id: String,
    val title: String,
    val courseName: String,
    val deadline: String,
    val submittedCount: Int,
    val totalStudents: Int,
    val completionPercent: Int,
    val completionProgress: Float,
    val averageGradingMinutes: Int,
)

data class GradedAssignmentUiModel(
    val id: String,
    val title: String,
    val courseName: String,
    val gradedDate: String,
    val totalStudents: Int,
    val averageGrade: Int,
)