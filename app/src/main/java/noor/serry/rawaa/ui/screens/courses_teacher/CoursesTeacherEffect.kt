package noor.serry.rawaa.ui.screens.courses_teacher

sealed interface CoursesTeacherEffect {
    data class NavigateToManageCourse(val courseId: String) : CoursesTeacherEffect
    data object NavigateToAddCourse : CoursesTeacherEffect
}