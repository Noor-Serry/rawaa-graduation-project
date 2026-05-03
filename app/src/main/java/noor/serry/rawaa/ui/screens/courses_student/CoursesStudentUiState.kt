package noor.serry.rawaa.ui.screens.courses_student

data class CoursesStudentUiState(
    val selectedTab: CoursesTab = CoursesTab.MY_COURSES,
    val myCourses: List<EnrolledCourseItem> = emptyList(),
    val availableCourses: List<AvailableCourseItem> = emptyList(),
    val isLoading: Boolean = false,
)

enum class CoursesTab { MY_COURSES, AVAILABLE }

data class EnrolledCourseItem(
    val courseCode: String,
    val courseName: String,
    val professorName: String,
    val progressPercent: Int,
    val nextSessionTime: String,
    val studentCount: Int,
    val isYellowIcon: Boolean = false,
)

data class AvailableCourseItem(
    val courseCode: String,
    val courseName: String,
    val professorName: String,
    val level: CourseLevel,
    val studentCount: Int,
    val durationWeeks: Int,
)

enum class CourseLevel { ADVANCED, INTERMEDIATE, BEGINNER }
