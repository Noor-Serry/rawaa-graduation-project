package noor.serry.rawaa.ui.screens.reports_admin

data class ReportsAdminUiState(
    val selectedTab: ReportTab = ReportTab.GRADES,
    val gradesRows: List<GradesRowItem> = emptyList(),
    val attendanceRows: List<AttendanceRowItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    // From GradesReportRowDto
    data class GradesRowItem(
        val courseName: String,
        val code: String,
        val enrolled: Int,
        val avgGrade: Float?,
        val maxGrade: Float?,
        val minGrade: Float?,
        val passed: Int,
        val failed: Int,
    )

    // From AttendanceReportRowDto
    data class AttendanceRowItem(
        val studentName: String,
        val level: Int?,
        val dept: String?,
        val totalSessions: Int,
        val present: Int,
        val absent: Int,
        val rate: Float,
    )

    enum class ReportTab { GRADES, ATTENDANCE }
}
