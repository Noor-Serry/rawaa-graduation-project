package noor.serry.rawaa.ui.screens.courses_student

data class CoursesStudentUiState(
    val selectedTab: CoursesTab = CoursesTab.MY_COURSES,
    val myCourses: List<EnrolledCourseItem> = emptyList(),
    val availableCourses: List<AvailableCourseItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

enum class CoursesTab { MY_COURSES, AVAILABLE }

data class EnrolledCourseItem(
    val courseCode: String,
    val courseName: String,
    val professorName: String,
    val creditHours: Int,
    val semester: String,
    val progressPercent: Int,
    // Removed: nextSessionTime — no endpoint returns next session time per enrolled course
    // Removed: studentCount    — StudentCourseDto has no enrolled_count field
    val isYellowIcon: Boolean = false,
)

data class AvailableCourseItem(
    val courseId: Int,       // Needed for navigation to course details
    val courseCode: String,
    val courseName: String,
    val professorName: String,
    val level: CourseLevel,
    val studentCount: Int,       // CourseDto.enrolledCount — available on the full course list
    val creditHours: Int,
    val semester: String?,
)

enum class CourseLevel { ADVANCED, INTERMEDIATE, BEGINNER }
