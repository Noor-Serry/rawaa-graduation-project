package noor.serry.rawaa.ui.screens.courses_teacher

import noor.serry.rawaa.data.dto.CourseDto
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class CoursesTeacherViewModel(
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<CoursesTeacherUiState, CoursesTeacherEffect>(
    initialState = CoursesTeacherUiState(),
    dispatcherProvider = dispatchers,
), CoursesTeacherInteractionListener {

    init { load() }

    override fun load() {
        updateState { it.copy(isLoading = true) }
        tryToExecute(
            action = { repository.getCourses().data ?: emptyList() },
            onSuccess = { list ->
                val uiModels = list.map { it.toTeacherCourseUiModel() }
                updateState {
                    it.copy(
                        isLoading = false,
                        activeCourses = uiModels.filter { c -> c.isActive },
                        archivedCourses = uiModels.filter { c -> !c.isActive },
                        totalPendingGrades = 0,
                        totalStudents = uiModels.sumOf { c -> c.totalStudents },
                        activeCourseCount = uiModels.count { c -> c.isActive },
                    )
                }
            },
            onError = { updateState { it.copy(isLoading = false) } },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onTabSelected(tab: CoursesTeacherTab) = updateState { it.copy(selectedTab = tab) }
    override fun onManageCourseClick(courseId: String) = sendNewEffect(CoursesTeacherEffect.NavigateToManageCourse(courseId))
    override fun onAddCourseClick() = sendNewEffect(CoursesTeacherEffect.NavigateToAddCourse)
}

// ── Mapper ───────────────────────────────────────────────────────────────────

private val TeacherCourseUiModel.isActive: Boolean get() = averageGrade >= 0

fun CourseDto.toTeacherCourseUiModel() = TeacherCourseUiModel(
    courseId = id.toString(),
    courseCode = code,
    courseName = name,
    semester = semester ?: "",
    totalStudents = enrolledCount ?: 0,
    totalAssignments = 0,
    pendingGrades = 0,
    averageGrade = 0,
    averageProgress = 0f,
)
