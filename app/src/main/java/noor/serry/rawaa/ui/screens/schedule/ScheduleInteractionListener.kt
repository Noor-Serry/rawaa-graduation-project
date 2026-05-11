package noor.serry.rawaa.ui.screens.schedule

interface ScheduleInteractionListener {
    fun onDaySelected(day: DayOfWeek)
    fun onViewSessionDetails(courseCode: String)
    // Removed: onViewFullWeekSchedule — ScheduleScreen IS the full week view;
    //          there is no deeper schedule screen and no matching route in StudentRouteKeys
}
