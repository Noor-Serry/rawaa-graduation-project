package noor.serry.rawaa.ui.screens.home_teacher

import noor.serry.rawaa.data.dto.CourseDto
import noor.serry.rawaa.data.dto.DoctorDashboardDto
import noor.serry.rawaa.data.dto.ScheduleSessionDto
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class HomeTeacherViewModel(
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<HomeTeacherUiState, HomeTeacherEffect>(
    initialState = HomeTeacherUiState(),
    dispatcherProvider = dispatchers,
), HomeTeacherInteractionListener {

    init { load() }

    fun load() {
        updateState { it.copy(isLoading = true) }
        tryToExecute(
            action = { repository.getDoctorDashboard() },
            onSuccess = { response ->
                val dashboard = response.data
                if (dashboard != null) {
                    updateState { dashboard.toHomeTeacherUiState() }
                } else {
                    updateState { it.copy(isLoading = false) }
                }
            },
            onError = { updateState { it.copy(isLoading = false) } },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onAddCourseClick() =
        sendNewNavigationEffect(HomeTeacherEffect.NavigateToAddCourse)

    override fun onNewAssignmentClick() =
        sendNewNavigationEffect(HomeTeacherEffect.NavigateToAddCourse)

    override fun onGradingClick() =
        sendNewNavigationEffect(HomeTeacherEffect.NavigateToGrading)

    override fun onReportsClick() =
        sendNewNavigationEffect(HomeTeacherEffect.NavigateToSchedule)

    override fun onViewAllScheduleClick() =
        sendNewNavigationEffect(HomeTeacherEffect.NavigateToStudents)

    override fun onViewAllCoursesClick() =
        sendNewNavigationEffect(HomeTeacherEffect.NavigateToCourses)

    override fun onManageCourseClick(courseId: String) =
        sendNewNavigationEffect(HomeTeacherEffect.NavigateToManageCourse(courseId))
}

// ── Mappers ──────────────────────────────────────────────────────────────────

fun DoctorDashboardDto.toHomeTeacherUiState() = HomeTeacherUiState(
    isLoading = false,
    teacherName = "",
    pendingTasks = 0,
    totalStudents = totalStudents,
    activeCourses = totalCourses,
    todaySessions = schedule.map { it.toTeacherSessionUiModel() },
    pendingAssignments = emptyList(),
    courses = courses.map { it.toTeacherCourseSummaryUiModel() },
    weeklySubmissionRate = 0,
    weeklyAttendanceRate = 0,
)

fun ScheduleSessionDto.toTeacherSessionUiModel() = TeacherSessionUiModel(
    id = id.toString(),
    courseName = courseName ?: "",
    time = "$startTime - $endTime",
    location = roomName ?: "",
    studentsCount = enrolled ?: 0,
)

fun CourseDto.toTeacherCourseSummaryUiModel() = TeacherCourseSummaryUiModel(
    id = id.toString(),
    name = name,
    averageGrade = 0,
    totalAssignments = 0,
    totalStudents = enrolledCount ?: 0,
    averageProgress = 0f,
)
