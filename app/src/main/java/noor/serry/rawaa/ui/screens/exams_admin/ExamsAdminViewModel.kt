package noor.serry.rawaa.ui.screens.exams_admin

import noor.serry.rawaa.data.dto.ExamDto
import noor.serry.rawaa.data.dto.ExamRequest
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class ExamsAdminViewModel(
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<ExamsAdminUiState, ExamsAdminEffect>(
    initialState       = ExamsAdminUiState(isLoading = true),
    dispatcherProvider = dispatchers,
), ExamsAdminInteractionListener {

    init { load() }

    fun load() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            action = {
                val exams   = repository.getExams()
                val courses = repository.getCourses(isActive = 1)
                exams to courses
            },
            onSuccess = { (examsResp, coursesResp) ->
                updateState { s ->
                    s.copy(
                        isLoading = false,
                        exams     = examsResp.data?.map { it.toExamItem() } ?: emptyList(),
                        courses   = coursesResp.data.map { ExamsAdminUiState.CourseRef(it.id, it.name) },
                    )
                }
            },
            onError   = { e -> updateState { it.copy(isLoading = false, errorMessage = e.message) } },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onExamClicked(examId: Int) {
        sendNewNavigationEffect(ExamsAdminEffect.NavigateToExamDetail(examId))
    }

    override fun onAddExamClicked() {
        updateState { it.copy(showCreateSheet = true, createForm = ExamsAdminUiState.ExamForm(), formError = null) }
    }

    override fun onPublishExam(examId: Int) {
        tryToExecute(
            action    = { repository.publishExam(examId) },
            onSuccess = {
                updateState { s ->
                    s.copy(exams = s.exams.map { if (it.id == examId) it.copy(isPublished = true) else it })
                }
                sendNewEffect(ExamsAdminEffect.ShowSuccess("تم نشر الامتحان بنجاح"))
            },
            onError   = { e -> sendNewEffect(ExamsAdminEffect.ShowError(e.message ?: "فشل النشر")) },
            dispatcher = dispatchers.IO,
        )
    }

    // ── Form handlers ─────────────────────────────────────────────────────────

    override fun onFormCourseSelected(courseId: Int?)    { updateState { it.copy(createForm = it.createForm.copy(courseId = courseId), formError = null) } }
    override fun onFormTitleChanged(title: String)       { updateState { it.copy(createForm = it.createForm.copy(title = title)) } }
    override fun onFormTypeSelected(type: String)        { updateState { it.copy(createForm = it.createForm.copy(type = type)) } }
    override fun onFormTotalMarksChanged(marks: String)  { updateState { it.copy(createForm = it.createForm.copy(totalMarks = marks)) } }
    override fun onFormDurationChanged(duration: String) { updateState { it.copy(createForm = it.createForm.copy(durationMin = duration)) } }
    override fun onFormStartAtChanged(startAt: String)   { updateState { it.copy(createForm = it.createForm.copy(startAt = startAt)) } }
    override fun onFormEndAtChanged(endAt: String)       { updateState { it.copy(createForm = it.createForm.copy(endAt = endAt)) } }

    override fun onFormSubmit() {
        val form = state.value.createForm
        when {
            form.courseId == null    -> { updateState { it.copy(formError = "يرجى اختيار المقرر") }; return }
            form.title.isBlank()     -> { updateState { it.copy(formError = "عنوان الامتحان مطلوب") }; return }
            form.startAt.isBlank()   -> { updateState { it.copy(formError = "وقت البداية مطلوب") }; return }
            form.endAt.isBlank()     -> { updateState { it.copy(formError = "وقت النهاية مطلوب") }; return }
        }
        val totalMarks  = form.totalMarks.toFloatOrNull()  ?: 100f
        val durationMin = form.durationMin.toIntOrNull()   ?: 90

        updateState { it.copy(isSaving = true, formError = null) }
        tryToExecute(
            action    = {
                repository.createExam(
                    ExamRequest(
                        courseId   = form.courseId!!,
                        title      = form.title.trim(),
                        type       = form.type,
                        totalMarks = totalMarks,
                        durationMin = durationMin,
                        startAt    = form.startAt,
                        endAt      = form.endAt,
                    )
                )
            },
            onSuccess = { resp ->
                val newItem = resp.data!!.toExamItem()
                updateState { s -> s.copy(isSaving = false, showCreateSheet = false, exams = listOf(newItem) + s.exams) }
                sendNewEffect(ExamsAdminEffect.ShowSuccess("تم إنشاء الامتحان بنجاح"))
            },
            onError   = { e -> updateState { it.copy(isSaving = false, formError = e.message) } },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onFormDismissed() { updateState { it.copy(showCreateSheet = false, formError = null) } }
}

// Mapper
fun ExamDto.toExamItem() = ExamsAdminUiState.ExamItem(
    id            = id,
    courseId      = courseId,
    courseName    = courseName,
    title         = title,
    type          = type,
    totalMarks    = totalMarks,
    durationMin   = durationMin,
    startAt       = startAt,
    endAt         = endAt,
    isPublished   = isPublished == 1,
    questionCount = questions?.size ?: 0,
)
