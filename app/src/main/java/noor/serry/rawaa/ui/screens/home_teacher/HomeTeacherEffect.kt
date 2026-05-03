package noor.serry.rawaa.ui.screens.home_teacher

sealed interface HomeTeacherEffect {
    data object NavigateToCourses : HomeTeacherEffect
    data object NavigateToStudents : HomeTeacherEffect
    data object NavigateToGrading : HomeTeacherEffect
    data object NavigateToSchedule : HomeTeacherEffect
    data object NavigateToReports : HomeTeacherEffect
    data object NavigateToAddCourse : HomeTeacherEffect
    data class NavigateToManageCourse(val courseId: String) : HomeTeacherEffect
}