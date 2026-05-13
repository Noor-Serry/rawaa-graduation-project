package noor.serry.rawaa.ui.screens.attendance_admin

data class AttendanceAdminUiState(
    val selectedTab: AttendanceTab = AttendanceTab.STUDENT,
    // Student attendance list
    val studentRecords: List<StudentAttendanceRow> = emptyList(),
    // Course attendance
    val courseRecords: List<CourseAttendanceRow> = emptyList(),
    // Employee attendance
    val employeeRecords: List<EmployeeAttendanceRow> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // Mark attendance sheet
    val showMarkSheet: Boolean = false,
    val markForm: MarkForm = MarkForm(),
    val isSaving: Boolean = false,
    val markError: String? = null,
    // Available courses for the dropdown
    val courses: List<CourseRef> = emptyList(),
) {
    enum class AttendanceTab { STUDENT, COURSE, EMPLOYEE }

    data class StudentAttendanceRow(
        val id: Int,
        val userId: Int,
        val studentName: String?,
        val courseName: String?,
        val date: String,
        val status: String,
        val notes: String?,
    )

    data class CourseAttendanceRow(
        val id: Int,
        val userId: Int,
        val userName: String?,
        val date: String,
        val status: String,
    )

    data class EmployeeAttendanceRow(
        val id: Int,
        val userId: Int,
        val userName: String?,
        val date: String,
        val checkIn: String?,
        val checkOut: String?,
        val status: String,
    )

    data class CourseRef(val id: Int, val name: String)

    data class MarkForm(
        val studentUserId: Int? = null,
        val courseId: Int? = null,
        val date: String = "",
        val status: String = "present",
        val notes: String = "",
    )

    companion object {
        val statuses = listOf("present", "absent", "late", "excused")
    }
}
