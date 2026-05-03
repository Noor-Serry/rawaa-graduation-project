package noor.serry.rawaa.ui.screens.home_student

import noor.serry.rawaa.domain.entity.AssignmentEntity
import noor.serry.rawaa.domain.entity.ScheduleSessionEntity
import noor.serry.rawaa.domain.entity.StudentDashboardEntity
import noor.serry.rawaa.domain.usecase.GetStudentDashboardUseCase
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class HomeStudentViewModel(
    private val getStudentDashboard: GetStudentDashboardUseCase,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<HomeStudentUiState, HomeStudentEffect>(
    initialState = HomeStudentUiState(isLoading = true),
    dispatcherProvider = dispatchers,
) , HomeStudentInteractionListener {

    init { load() }

    fun load() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            action = { getStudentDashboard() },
            onSuccess = { dashboard -> updateState { dashboard.toHomeStudentUiState() } },
            onError = { e -> updateState { it.copy(isLoading = false, errorMessage = e.message) } },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onViewAllSchedule() = sendNewEffect(HomeStudentEffect.NavigateToAllSchedule)
    override fun onViewAllHomework() = sendNewEffect(HomeStudentEffect.NavigateToAllHomework)
    override fun onViewAllCourses() = sendNewEffect(HomeStudentEffect.NavigateToAllCourses)
    override fun onHomeworkClick() = sendNewEffect(HomeStudentEffect.NavigateToHomework)
    override fun onMyCoursesClick() = sendNewEffect(HomeStudentEffect.NavigateToMyCourses)
    override fun onScheduleClick() = sendNewEffect(HomeStudentEffect.NavigateToSchedule)
    override fun onGradesClick() = sendNewEffect(HomeStudentEffect.NavigateToGrades)
}

// ── Mappers ──────────────────────────────────────────────────────────────────

fun StudentDashboardEntity.toHomeStudentUiState() = HomeStudentUiState(
    isLoading = false,
    studentName = studentName,
    gpa = "%.2f".format(cgpa),
    homeworkCount = pendingAssignments,
    activeCoursesCount = activeCourses,
    scheduleItems = todaySessions.map { it.toScheduleItem() },
    homeworkItems = upcomingAssignments.map { it.toHomeworkItem() },
)

fun ScheduleSessionEntity.toScheduleItem() = HomeStudentUiState.ScheduleItem(
    courseName = courseName,
    professorName = instructorName,
    time = "$startTime - $endTime",
    location = location,
    isOnline = false,
)

fun AssignmentEntity.toHomeworkItem() = HomeStudentUiState.HomeworkItem(
    title = title,
    courseName = courseName,
    deadline = deadline,
)
