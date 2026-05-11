package noor.serry.rawaa.ui.screens.home_student

sealed interface HomeStudentEffect {
    data object NavigateToAllSchedule : HomeStudentEffect
    data object NavigateToAllCourses  : HomeStudentEffect
    data object NavigateToMyCourses   : HomeStudentEffect
    data object NavigateToSchedule    : HomeStudentEffect
    // Removed: NavigateToGrades      — no grades screen in student navigation (StudentRouteKeys)
    // Removed: NavigateToAllHomework — no homework endpoint on the server
    // Removed: NavigateToHomework    — no homework endpoint on the server
}
