package noor.serry.rawaa.ui.screens.grading_teacher

/**
 * UI state for the teacher grading screen.
 *
 * Data source: GET /api/doctor/dashboard  →  DoctorDashboardDto
 *
 * DoctorDashboardDto fields used:
 *   • totalStudents              → totalStudents summary card
 *   • totalCourses               → (informational)
 *   • courses: List<CourseDto>   → mapped to CourseGradingUiModel
 *
 * There is no dedicated assignment-grading endpoint in the API.
 * The grading list shows courses; grading progress per course is derived
 * from CourseDto.enrolledCount (total) — a graded count is not available
 * from the dashboard endpoint alone and is therefore not shown.
 */
data class GradingUiState(
    val isLoading: Boolean = true,
    val totalStudents: Int = 0,
    val totalCourses: Int = 0,
    val allCourses: List<CourseGradingUiModel> = emptyList(),
    val filteredCourses: List<CourseGradingUiModel> = emptyList(),
    val searchQuery: String = "",
    val selectedTab: GradingTab = GradingTab.ACTIVE,
    val error: String? = null,
)

enum class GradingTab { ACTIVE, INACTIVE }

/**
 * Flat UI model for a single course row on the grading screen.
 * Every field maps 1-to-1 to a field in [CourseDto].
 */
data class CourseGradingUiModel(
    val id: Int,
    val name: String,
    val code: String,
    val departmentName: String?,
    val semester: String?,
    val creditHours: Int,
    val enrolledCount: Int,   // CourseDto.enrolledCount ?: 0
    val maxStudents: Int,     // CourseDto.maxStudents
    val isActive: Boolean,    // CourseDto.isActive == 1
) {
    /** Enrolment fill ratio 0..1 — computed client-side. */
    val enrolmentProgress: Float
        get() = if (maxStudents == 0) 0f
                else enrolledCount.toFloat() / maxStudents.toFloat()
}
