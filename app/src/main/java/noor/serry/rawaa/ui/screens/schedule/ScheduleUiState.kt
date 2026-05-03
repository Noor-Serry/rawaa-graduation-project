package noor.serry.rawaa.ui.screens.schedule

data class ScheduleUiState(
    val totalLecturesPerWeek: Int = 0,
    val totalCourses: Int = 0,
    val totalDays: Int = 0,
    val selectedDay: DayOfWeek = DayOfWeek.SUNDAY,
    val scheduleByDay: Map<DayOfWeek, List<SessionItem>> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    val sessionsForSelectedDay: List<SessionItem>
        get() = scheduleByDay[selectedDay] ?: emptyList()
}

enum class DayOfWeek(val label: String, val shortLabel: String) {
    SUNDAY("الأحد", "ح"),
    MONDAY("الاثنين", "ن"),
    TUESDAY("الثلاثاء", "ث"),
    WEDNESDAY("الأربعاء", "ع"),
    THURSDAY("الخميس", "خ"),
    FRIDAY("الجمعة", "ج"),
    SATURDAY("السبت", "س"),
}

data class SessionItem(
    val courseCode: String,
    val courseName: String,
    val professorName: String,
    val location: String,
    val timeRange: String,
    val type: SessionType,
)

enum class SessionType(val label: String) {
    LECTURE("محاضرة"),
    LAB("عملي"),
}
