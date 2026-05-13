package noor.serry.rawaa.ui.screens.schedules_admin

data class SchedulesAdminUiState(
    val sessions: List<ScheduleItem> = emptyList(),
    val courses: List<CourseRef> = emptyList(),         // for the create/edit dropdown
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // Filter
    val selectedCourseId: Int? = null,
    // Create / Edit sheet
    val showFormSheet: Boolean = false,
    val editingSession: ScheduleItem? = null,
    val form: ScheduleForm = ScheduleForm(),
    val isSaving: Boolean = false,
    val formError: String? = null,
    // Delete confirmation
    val pendingDeleteId: Int? = null,
) {
    data class ScheduleItem(
        val id: Int,
        val courseId: Int?,
        val courseName: String?,
        val day: String?,
        val startTime: String,
        val endTime: String,
        val roomName: String?,
        val type: String,
        val doctorName: String?,
    )

    data class CourseRef(val id: Int, val name: String)

    data class ScheduleForm(
        val courseId: Int? = null,
        val day: String = "Sunday",
        val startTime: String = "08:00",
        val endTime: String = "10:00",
        val type: String = "lecture",
        val roomId: Int? = null,
    )

    val isEditing: Boolean get() = editingSession != null

    val filteredSessions: List<ScheduleItem>
        get() = if (selectedCourseId == null) sessions
        else sessions.filter { it.courseId == selectedCourseId }

    companion object {
        val days = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val types = listOf("lecture", "lab", "tutorial")
    }
}
