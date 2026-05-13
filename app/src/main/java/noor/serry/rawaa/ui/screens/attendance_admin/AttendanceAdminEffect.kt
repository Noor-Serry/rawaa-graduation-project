package noor.serry.rawaa.ui.screens.attendance_admin

sealed interface AttendanceAdminEffect {
    data class ShowSuccess(val message: String) : AttendanceAdminEffect
    data class ShowError(val message: String)   : AttendanceAdminEffect
    data class NavigateToStudentAttendance(val studentUserId: Int) : AttendanceAdminEffect
    data class NavigateToCourseAttendance(val courseId: Int)       : AttendanceAdminEffect
}
