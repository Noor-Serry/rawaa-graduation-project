package noor.serry.rawaa.ui.screens.exams_admin

data class ExamsAdminUiState(
    val exams: List<ExamItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val courses: List<CourseRef> = emptyList(),
    // Create exam sheet
    val showCreateSheet: Boolean = false,
    val createForm: ExamForm = ExamForm(),
    val isSaving: Boolean = false,
    val formError: String? = null,
) {
    data class ExamItem(
        val id: Int,
        val courseId: Int?,
        val courseName: String?,
        val title: String,
        val type: String,
        val totalMarks: Float?,
        val durationMin: Int?,
        val startAt: String?,
        val endAt: String?,
        val isPublished: Boolean,
        val questionCount: Int,
    )

    data class CourseRef(val id: Int, val name: String)

    data class ExamForm(
        val courseId: Int? = null,
        val title: String = "",
        val type: String = "midterm",
        val totalMarks: String = "100",
        val durationMin: String = "90",
        val startAt: String = "",
        val endAt: String = "",
    )

    companion object {
        val examTypes = listOf("midterm", "final", "quiz", "assignment")
    }
}
