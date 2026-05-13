package noor.serry.rawaa.ui.screens.schedules_admin

interface SchedulesAdminInteractionListener {
    fun onCourseFilterSelected(courseId: Int?)
    fun onAddSessionClicked()
    fun onEditSessionClicked(session: SchedulesAdminUiState.ScheduleItem)
    fun onDeleteSessionClicked(sessionId: Int)
    fun onDeleteConfirmed()
    fun onDeleteDismissed()
    // Form
    fun onFormCourseSelected(courseId: Int?)
    fun onFormDaySelected(day: String)
    fun onFormStartTimeChanged(time: String)
    fun onFormEndTimeChanged(time: String)
    fun onFormTypeSelected(type: String)
    fun onFormSubmit()
    fun onFormDismissed()
}
