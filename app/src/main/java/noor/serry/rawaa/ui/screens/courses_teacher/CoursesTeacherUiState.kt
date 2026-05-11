package noor.serry.rawaa.ui.screens.courses_teacher

import noor.serry.rawaa.data.dto.CourseDto

data class CoursesTeacherUiState(
    val isLoading: Boolean = true,
    val activeCourses: List<CourseDto> = emptyList(),
    val archivedCourses: List<CourseDto> = emptyList(),
    val selectedTab: CourseTab = CourseTab.ACTIVE,
    // Derived from DoctorDashboardDto.total_students (sum of enrolledCount across all courses)
    val totalStudents: Int = 0,
    val error: String? = null,
)

enum class CourseTab { ACTIVE, ARCHIVED }

// Alias so any pre-existing code referencing CoursesTeacherTab still compiles
typealias CoursesTeacherTab = CourseTab

sealed interface CoursesTeacherEffect {
    data class NavigateToCourseDetail(val courseId: Int) : CoursesTeacherEffect
    data class ShowError(val message: String) : CoursesTeacherEffect
}
