package noor.serry.rawaa.ui.screens.schedule

sealed interface ScheduleEffect {
    data class NavigateToSessionDetails(val courseCode: String) : ScheduleEffect
    data object NavigateToFullWeekSchedule : ScheduleEffect
}
