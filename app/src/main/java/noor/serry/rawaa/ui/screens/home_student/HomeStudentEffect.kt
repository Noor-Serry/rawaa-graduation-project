package noor.serry.rawaa.ui.screens.home_student

sealed interface HomeStudentEffect {
    data object NavigateToAllSchedule : HomeStudentEffect
    data object NavigateToAllHomework : HomeStudentEffect
    data object NavigateToAllCourses : HomeStudentEffect
    data object NavigateToHomework : HomeStudentEffect
    data object NavigateToMyCourses : HomeStudentEffect
    data object NavigateToSchedule : HomeStudentEffect
    data object NavigateToGrades : HomeStudentEffect
}
