package noor.serry.rawaa.ui.screens.schedule

interface ScheduleInteractionListener {
    fun onDaySelected(day: DayOfWeek)
    fun onViewSessionDetails(courseCode: String)
    fun onViewFullWeekSchedule()
}
