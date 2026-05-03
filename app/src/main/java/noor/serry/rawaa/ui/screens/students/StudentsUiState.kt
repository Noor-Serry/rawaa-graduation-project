package noor.serry.rawaa.ui.screens.students

data class StudentsUiState(
    val isLoading: Boolean = true,
    val students: List<StudentUiModel> = emptyList(),
    val searchQuery: String = "",
    val needsFollowUpCount: Int = 0,
    val failingCount: Int = 0,
    val totalCount: Int = 0,
) {
    val displayedStudents: List<StudentUiModel>
        get() = if (searchQuery.isBlank()) students
        else students.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.email.contains(searchQuery, ignoreCase = true)
        }
}

data class StudentUiModel(
    val id: String,
    val name: String,
    val email: String,
    val statusLabel: String,
    val statusType: StudentStatusType,
    val attendance: Int,
    val grade: Int,
    val assignmentsSubmitted: Int,
    val totalAssignments: Int,
    val assignmentProgress: Float,
    val assignmentProgressPercent: Int,
    val isTrendingUp: Boolean,
)

enum class StudentStatusType { EXCELLENT, GOOD, NEEDS_FOLLOW_UP }