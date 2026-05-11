package noor.serry.rawaa.ui.screens.home_student

import noor.serry.rawaa.data.dto.ScheduleSessionDto
import noor.serry.rawaa.data.dto.StudentDashboardDto
import noor.serry.rawaa.data.dto.StudentCourseDto
import noor.serry.rawaa.data.dto.UpcomingExamDto
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider
import noor.serry.rawaa.ui.screens.home_student.components.CourseProgressItem

class HomeStudentViewModel(
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<HomeStudentUiState, HomeStudentEffect>(
    initialState = HomeStudentUiState(isLoading = true),
    dispatcherProvider = dispatchers,
), HomeStudentInteractionListener {

    init { load() }

    fun load() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            action = { repository.getStudentDashboard() },
            onSuccess = { response ->
                val dashboard = response.data
                if (dashboard != null) {
                    updateState { dashboard.toHomeStudentUiState() }
                } else {
                    updateState { it.copy(isLoading = false) }
                }
            },
            onError = { e -> updateState { it.copy(isLoading = false, errorMessage = e.message) } },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onViewAllSchedule() = sendNewEffect(HomeStudentEffect.NavigateToAllSchedule)
    override fun onViewAllCourses()  = sendNewEffect(HomeStudentEffect.NavigateToAllCourses)
    override fun onMyCoursesClick()  = sendNewEffect(HomeStudentEffect.NavigateToMyCourses)
    override fun onScheduleClick()   = sendNewEffect(HomeStudentEffect.NavigateToSchedule)
    // Removed: onGradesClick — no grades screen in StudentRouteKeys, no matching server action
}

// ── Mappers ──────────────────────────────────────────────────────────────────

fun StudentDashboardDto.toHomeStudentUiState() = HomeStudentUiState(
    isLoading           = false,
    studentName         = student.name,
    gpa                 = gpa ?: student.gpa ?: "0.00",
    activeCoursesCount  = activeCourses,
    upcomingExamsCount  = upcomingExams.size,
    scheduleItems       = schedule.map { it.toScheduleItem() },
    upcomingExams       = upcomingExams.map { it.toExamItem() },
    courseProgressItems = courses
        .filter { it.status == "active" }
        .map { it.toCourseProgressItem() },
)

fun ScheduleSessionDto.toScheduleItem() = HomeStudentUiState.ScheduleItem(
    courseName    = courseName ?: "",
    professorName = doctorName ?: "",
    time          = "$startTime - $endTime",
    location      = roomName ?: "",
    type          = when (type.lowercase()) {
        "lab" -> HomeStudentUiState.SessionType.LAB
        else  -> HomeStudentUiState.SessionType.LECTURE
    },
)

fun UpcomingExamDto.toExamItem() = HomeStudentUiState.ExamItem(
    title      = title,
    courseName = courseName ?: "",
    courseCode = code ?: "",
    startAt    = startAt ?: "",
    totalMarks = totalMarks,
    type       = type,
)

fun StudentCourseDto.toCourseProgressItem() = CourseProgressItem(
    courseName      = courseName,
    progressPercent = when (status) {
        "completed" -> 100
        "active"    -> 50
        else        -> 0
    },
)
