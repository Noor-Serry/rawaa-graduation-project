package noor.serry.rawaa.ui.screens.schedule

sealed interface ScheduleEffect {
    data class NavigateToSessionDetails(val courseCode: String) : ScheduleEffect
    // Removed: NavigateToFullWeekSchedule — ScheduleScreen is the full-week view,
    //          no sub-screen or route key exists for it
}
