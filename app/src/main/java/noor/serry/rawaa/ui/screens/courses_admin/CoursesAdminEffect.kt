package noor.serry.rawaa.ui.screens.courses_admin

sealed interface CoursesAdminEffect {
    data class NavigateToCourseDetail(val courseId: Int) : CoursesAdminEffect
}
