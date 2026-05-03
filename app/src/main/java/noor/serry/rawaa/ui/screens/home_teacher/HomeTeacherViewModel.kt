package noor.serry.rawaa.ui.screens.home_teacher

import noor.serry.rawaa.domain.usecase.GetTeacherDashboardUseCase
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class HomeTeacherViewModel(
    private val getTeacherDashboard: GetTeacherDashboardUseCase,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<HomeTeacherUiState, HomeTeacherEffect>(
    initialState = HomeTeacherUiState(),
    dispatcherProvider = dispatchers,
) , HomeTeacherInteractionListener{

    init { load() }

    fun load() {
        updateState { it.copy(isLoading = true) }
        tryToExecute(
            action = { getTeacherDashboard() },
            onSuccess = { dashboard -> updateState { dashboard.toHomeTeacherUiState() } },
            onError = { updateState { it.copy(isLoading = false) } },
            dispatcher = dispatchers.IO,
        )
    }


    override fun onAddCourseClick() {
        sendNewNavigationEffect(HomeTeacherEffect.NavigateToAddCourse)

    }

    override fun onNewAssignmentClick() {
        sendNewNavigationEffect(HomeTeacherEffect.NavigateToAddCourse)
    }

    override fun onGradingClick() {
        sendNewNavigationEffect(HomeTeacherEffect.NavigateToGrading)
    }

    override fun onReportsClick() {
        sendNewNavigationEffect(HomeTeacherEffect.NavigateToSchedule)
    }

    override fun onViewAllScheduleClick() {
        sendNewNavigationEffect(HomeTeacherEffect.NavigateToStudents)    }

    override fun onViewAllCoursesClick() {
        sendNewNavigationEffect(HomeTeacherEffect.NavigateToCourses)
    }

    override fun onManageCourseClick(courseId: String) {
        sendNewNavigationEffect(HomeTeacherEffect.NavigateToManageCourse(courseId))
    }
}
