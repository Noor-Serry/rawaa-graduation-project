package noor.serry.rawaa.ui.screens.attendance_admin

interface AttendanceAdminInteractionListener {
    fun onTabSelected(tab: AttendanceAdminUiState.AttendanceTab)
    fun onStudentRowClicked(studentUserId: Int)
    fun onCourseRowClicked(courseId: Int)
    fun onMarkAttendanceClicked()
    fun onMarkFormStudentIdChanged(id: String)
    fun onMarkFormCourseSelected(courseId: Int?)
    fun onMarkFormDateChanged(date: String)
    fun onMarkFormStatusSelected(status: String)
    fun onMarkFormNotesChanged(notes: String)
    fun onMarkFormSubmit()
    fun onMarkFormDismissed()
    fun onEmployeeCheckIn()
}
