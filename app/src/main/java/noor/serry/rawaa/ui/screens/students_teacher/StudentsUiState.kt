package noor.serry.rawaa.ui.screens.students_teacher

data class StudentsUiState(
    val isLoading: Boolean = true,
    val students: List<StudentUiModel> = emptyList(),
    val searchQuery: String = "",
    val needsFollowUpCount: Int = 0,
    // Removed: failingCount — was computed from a synthetic grade conversion, not a real server field
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
    // Nullable — StudentDto.departmentName is nullable
    val departmentName: String?,
    // Nullable — StudentDto.level is nullable
    val level: Int?,
    // Nullable — StudentDto.gpa is nullable (String on server)
    val gpa: String?,
    val statusLabel: String,
    val statusType: StudentStatusType,
    // Nullable — StudentDto.enrollmentYear is nullable
    val enrollmentYear: Int?,
    val isActive: Boolean,
    // Removed: attendance — no attendance field in StudentDto
    // Removed: grade (Int) — server sends gpa as String; a synthetic Int conversion was misleading
    // Removed: assignmentsSubmitted / totalAssignments / assignmentProgress — no assignments data in StudentDto
    // Removed: isTrendingUp — no backend signal for trend direction
)

enum class StudentStatusType { EXCELLENT, GOOD, NEEDS_FOLLOW_UP }