package noor.serry.rawaa.ui.screens.courses_teacher

import noor.serry.rawaa.domain.usecase.GetTeacherCoursesUseCase
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class CoursesTeacherViewModel(
    private val getTeacherCourses: GetTeacherCoursesUseCase,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<CoursesTeacherUiState, CoursesTeacherEffect>(
    initialState = CoursesTeacherUiState(),
    dispatcherProvider = dispatchers,
) , CoursesTeacherInteractionListener{

    init { load() }

    override fun load() {
        updateState { it.copy(isLoading = true) }
        tryToExecute(
            action = { getTeacherCourses() },
            onSuccess = { list ->
                val uiModels = list.map { it.toTeacherCourseUiModel() }
                updateState {
                    it.copy(
                        isLoading = false,
                        activeCourses = uiModels.filter { c -> c.averageGrade >= 0 },
                        archivedCourses = emptyList(),
                        totalPendingGrades = uiModels.sumOf { c -> c.pendingGrades },
                        totalStudents = uiModels.sumOf { c -> c.totalStudents },
                        activeCourseCount = uiModels.size,
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
