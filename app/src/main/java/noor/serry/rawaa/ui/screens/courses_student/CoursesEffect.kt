package noor.serry.rawaa.ui.screens.courses_student

sealed interface CoursesEffect {
    data class NavigateToCourseDetails(val courseCode: String) : CoursesEffect
    data class NavigateToLectures(val courseCode: String) : CoursesEffect
    data class NavigateToHomework(val courseCode: String) : CoursesEffect
    data class NavigateToMaterials(val courseCode: String) : CoursesEffect
    data class NavigateToEnroll(val courseCode: String) : CoursesEffect
}
