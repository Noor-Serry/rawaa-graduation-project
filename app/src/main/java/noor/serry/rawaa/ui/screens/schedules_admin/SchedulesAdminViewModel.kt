package noor.serry.rawaa.ui.screens.schedules_admin

import noor.serry.rawaa.data.dto.ScheduleRequest
import noor.serry.rawaa.data.dto.ScheduleSessionDto
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class SchedulesAdminViewModel(
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<SchedulesAdminUiState, SchedulesAdminEffect>(
    initialState       = SchedulesAdminUiState(isLoading = true),
    dispatcherProvider = dispatchers,
), SchedulesAdminInteractionListener {

    init { load() }

    fun load() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            action = {
                val schedules = repository.getMySchedule()
                val courses   = repository.getCourses(isActive = 1)
                schedules to courses
            },
            onSuccess = { (schedResp, coursesResp) ->
                updateState { s ->
                    s.copy(
                        isLoading = false,
                        sessions  = schedResp.data?.map { it.toScheduleItem() } ?: emptyList(),
                        courses   = coursesResp.data.map { SchedulesAdminUiState.CourseRef(it.id, it.name) },
                    )
                }
            },
            onError   = { e ->
                updateState { it.copy(isLoading = false, errorMessage = e.message) }
            },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onCourseFilterSelected(courseId: Int?) {
        updateState { it.copy(selectedCourseId = courseId) }
        if (courseId != null) loadByCourse(courseId) else load()
    }

    private fun loadByCourse(courseId: Int) {
        updateState { it.copy(isLoading = true) }
        tryToExecute(
            action    = { repository.getCourseSchedule(courseId) },
            onSuccess = { resp ->
                updateState { s ->
                    s.copy(isLoading = false, sessions = resp.data?.map { it.toScheduleItem() } ?: emptyList())
                }
            },
            dispatcher = dispatchers.IO,
        )
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    override fun onAddSessionClicked() {
        updateState { it.copy(showFormSheet = true, editingSession = null, form = SchedulesAdminUiState.ScheduleForm(), formError = null) }
    }

    override fun onEditSessionClicked(session: SchedulesAdminUiState.ScheduleItem) {
        updateState {
            it.copy(
                showFormSheet  = true,
                editingSession = session,
                form           = SchedulesAdminUiState.ScheduleForm(
                    courseId  = session.courseId,
                    day       = session.day ?: "Sunday",
                    startTime = session.startTime,
                    endTime   = session.endTime,
                    type      = session.type,
                ),
                formError = null,
            )
        }
    }

    override fun onDeleteSessionClicked(sessionId: Int) {
        updateState { it.copy(pendingDeleteId = sessionId) }
    }

    override fun onDeleteConfirmed() {
        val id = state.value.pendingDeleteId ?: return
        updateState { it.copy(pendingDeleteId = null) }
        tryToExecute(
            action    = { repository.deleteSchedule(id) },
            onSuccess = {
                updateState { s -> s.copy(sessions = s.sessions.filter { it.id != id }) }
                sendNewEffect(SchedulesAdminEffect.ShowSuccess("تم حذف الجلسة بنجاح"))
            },
            onError   = { e -> sendNewEffect(SchedulesAdminEffect.ShowError(e.message ?: "فشل الحذف")) },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onDeleteDismissed() { updateState { it.copy(pendingDeleteId = null) } }

    // ── Form field handlers ───────────────────────────────────────────────────

    override fun onFormCourseSelected(courseId: Int?)     { updateState { it.copy(form = it.form.copy(courseId = courseId), formError = null) } }
    override fun onFormDaySelected(day: String)           { updateState { it.copy(form = it.form.copy(day = day)) } }
    override fun onFormStartTimeChanged(time: String)     { updateState { it.copy(form = it.form.copy(startTime = time)) } }
    override fun onFormEndTimeChanged(time: String)       { updateState { it.copy(form = it.form.copy(endTime = time)) } }
    override fun onFormTypeSelected(type: String)         { updateState { it.copy(form = it.form.copy(type = type)) } }

    override fun onFormSubmit() {
        val s = state.value
        val form = s.form
        if (form.courseId == null) {
            updateState { it.copy(formError = "يرجى اختيار المقرر") }
            return
        }
        updateState { it.copy(isSaving = true, formError = null) }
        val request = ScheduleRequest(
            courseId  = form.courseId,
            day       = form.day,
            startTime = form.startTime,
            endTime   = form.endTime,
            type      = form.type,
            roomId    = form.roomId,
        )
        val editing = s.editingSession
        if (editing == null) {
            tryToExecute(
                action    = { repository.createSchedule(request) },
                onSuccess = { resp ->
                    val newItem = resp.data!!.toScheduleItem()
                    updateState { st -> st.copy(isSaving = false, showFormSheet = false, sessions = st.sessions + newItem) }
                    sendNewEffect(SchedulesAdminEffect.ShowSuccess("تم إضافة الجلسة بنجاح"))
                },
                onError   = { e -> updateState { it.copy(isSaving = false, formError = e.message) } },
                dispatcher = dispatchers.IO,
            )
        } else {
            tryToExecute(
                action    = { repository.updateSchedule(editing.id, request) },
                onSuccess = { resp ->
                    val updated = resp.data!!.toScheduleItem()
                    updateState { st ->
                        st.copy(
                            isSaving      = false,
                            showFormSheet = false,
                            sessions      = st.sessions.map { if (it.id == updated.id) updated else it },
                        )
                    }
                    sendNewEffect(SchedulesAdminEffect.ShowSuccess("تم تحديث الجلسة بنجاح"))
                },
                onError   = { e -> updateState { it.copy(isSaving = false, formError = e.message) } },
                dispatcher = dispatchers.IO,
            )
        }
    }

    override fun onFormDismissed() { updateState { it.copy(showFormSheet = false, formError = null) } }
}

// Mapper
fun ScheduleSessionDto.toScheduleItem() = SchedulesAdminUiState.ScheduleItem(
    id         = id,
    courseId   = courseId,
    courseName = courseName,
    day        = day,
    startTime  = startTime,
    endTime    = endTime,
    roomName   = roomName,
    type       = type,
    doctorName = doctorName,
)
