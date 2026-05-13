package noor.serry.rawaa.ui.screens.attendance_admin

import noor.serry.rawaa.data.dto.MarkAttendanceRequest
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class AttendanceAdminViewModel(
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<AttendanceAdminUiState, AttendanceAdminEffect>(
    initialState       = AttendanceAdminUiState(isLoading = true),
    dispatcherProvider = dispatchers,
), AttendanceAdminInteractionListener {

    init { loadCourses(); loadStudentList() }

    private fun loadCourses() {
        tryToExecute(
            action    = { repository.getCourses(isActive = 1) },
            onSuccess = { resp ->
                updateState { s ->
                    s.copy(courses = resp.data.map { AttendanceAdminUiState.CourseRef(it.id, it.name) })
                }
            },
            dispatcher = dispatchers.IO,
        )
    }

    /**
     * Loads the student list so the admin can tap a student to view their attendance history.
     * Each row carries the student's userId which is required for the detail navigation.
     */
    private fun loadStudentList() {
        tryToExecute(
            action    = { repository.getStudents(perPage = 100) },
            onSuccess = { resp ->
                updateState { s ->
                    s.copy(
                        isLoading      = false,
                        studentRecords = resp.data.map { student ->
                            AttendanceAdminUiState.StudentAttendanceRow(
                                id          = student.id,
                                userId      = student.userId ?: student.id,
                                studentName = student.name,
                                courseName  = student.departmentName,
                                date        = student.createdAt.orEmpty(),
                                status      = if (student.isActive == 1) "active" else "inactive",
                                notes       = null,
                            )
                        },
                    )
                }
            },
            onError   = { e -> updateState { it.copy(isLoading = false, errorMessage = e.message) } },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onTabSelected(tab: AttendanceAdminUiState.AttendanceTab) {
        updateState { it.copy(selectedTab = tab, isLoading = true, errorMessage = null) }
        when (tab) {
            AttendanceAdminUiState.AttendanceTab.STUDENT  -> loadStudentList()
            AttendanceAdminUiState.AttendanceTab.COURSE   -> updateState { it.copy(isLoading = false) }
            AttendanceAdminUiState.AttendanceTab.EMPLOYEE -> loadEmployeeAttendance()
        }
    }

    /**
     * Loads the attendance report for a representative employee (first available).
     * The admin-level employee attendance list is best surfaced via the report endpoint.
     */
    private fun loadEmployeeAttendance() {
        tryToExecute(
            action    = { repository.getEmployees(perPage = 100) },
            onSuccess = { resp ->
                updateState { s ->
                    s.copy(
                        isLoading       = false,
                        employeeRecords = resp.data.map { emp ->
                            AttendanceAdminUiState.EmployeeAttendanceRow(
                                id       = emp.id,
                                userId   = emp.userId,
                                userName = emp.name,
                                date     = emp.hireDate.orEmpty(),
                                checkIn  = null,
                                checkOut = null,
                                status   = if (emp.isActive == 1) "active" else "inactive",
                            )
                        },
                    )
                }
            },
            onError   = { e -> updateState { it.copy(isLoading = false, errorMessage = e.message) } },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onStudentRowClicked(studentUserId: Int) {
        sendNewNavigationEffect(AttendanceAdminEffect.NavigateToStudentAttendance(studentUserId))
    }

    override fun onCourseRowClicked(courseId: Int) {
        sendNewNavigationEffect(AttendanceAdminEffect.NavigateToCourseAttendance(courseId))
    }

    // ── Mark attendance ───────────────────────────────────────────────────────

    override fun onMarkAttendanceClicked() {
        updateState { it.copy(showMarkSheet = true, markForm = AttendanceAdminUiState.MarkForm(), markError = null) }
    }

    override fun onMarkFormStudentIdChanged(id: String) {
        updateState { it.copy(markForm = it.markForm.copy(studentUserId = id.toIntOrNull())) }
    }

    override fun onMarkFormCourseSelected(courseId: Int?) {
        updateState { it.copy(markForm = it.markForm.copy(courseId = courseId)) }
    }

    override fun onMarkFormDateChanged(date: String) {
        updateState { it.copy(markForm = it.markForm.copy(date = date)) }
    }

    override fun onMarkFormStatusSelected(status: String) {
        updateState { it.copy(markForm = it.markForm.copy(status = status)) }
    }

    override fun onMarkFormNotesChanged(notes: String) {
        updateState { it.copy(markForm = it.markForm.copy(notes = notes)) }
    }

    override fun onMarkFormSubmit() {
        val form = state.value.markForm
        if (form.studentUserId == null) { updateState { it.copy(markError = "يرجى إدخال رقم الطالب") }; return }
        if (form.courseId == null)      { updateState { it.copy(markError = "يرجى اختيار المقرر") }; return }
        if (form.date.isBlank())        { updateState { it.copy(markError = "يرجى إدخال التاريخ") }; return }

        updateState { it.copy(isSaving = true, markError = null) }
        tryToExecute(
            action    = {
                repository.markStudentAttendance(
                    MarkAttendanceRequest(
                        studentUserId = form.studentUserId,
                        courseId      = form.courseId,
                        date          = form.date,
                        status        = form.status,
                        notes         = form.notes.ifBlank { null },
                    )
                )
            },
            onSuccess = {
                updateState { it.copy(isSaving = false, showMarkSheet = false) }
                sendNewEffect(AttendanceAdminEffect.ShowSuccess("تم تسجيل الحضور بنجاح"))
            },
            onError   = { e -> updateState { it.copy(isSaving = false, markError = e.message) } },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onMarkFormDismissed() {
        updateState { it.copy(showMarkSheet = false, markError = null) }
    }

    override fun onEmployeeCheckIn() {
        tryToExecute(
            action    = { repository.employeeCheckIn() },
            onSuccess = { sendNewEffect(AttendanceAdminEffect.ShowSuccess("تم تسجيل الحضور")) },
            onError   = { e -> sendNewEffect(AttendanceAdminEffect.ShowError(e.message ?: "فشل تسجيل الحضور")) },
            dispatcher = dispatchers.IO,
        )
    }
}
