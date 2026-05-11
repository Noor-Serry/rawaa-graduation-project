package noor.serry.rawaa.ui.screens.courses_student

sealed interface CoursesEffect {
    data class NavigateToCourseDetails(val courseCode: String) : CoursesEffect
    data class NavigateToEnroll(val courseCode: String) : CoursesEffect
    data class ShowError(val messageResId: Int) : CoursesEffect
    data class ShowMessage(val messageResId: Int) : CoursesEffect

    data class ShowErrorMessage(val message: String) : CoursesEffect
}
