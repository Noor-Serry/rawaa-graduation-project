package noor.serry.rawaa.ui.screens.home_teacher

import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class HomeTeacherViewModel(
    private val repository: UniversityRepository,
    dispatcherProvider: DispatcherProvider,
) : BaseViewModel<HomeTeacherUiState, HomeTeacherEffect>(
    initialState = HomeTeacherUiState(),
    dispatcherProvider = dispatcherProvider,
) {

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        updateState { it.copy(isLoading = true, error = null) }
        tryToExecute(
            action = {
                val dashboard = repository.getDoctorDashboard()
                val me = repository.getMe()
                Pair(dashboard, me)
            },
            onSuccess = { (dashboard, me) ->
                val data = dashboard.data
                val user = me.data
                updateState { state ->
                    state.copy(
                        isLoading = false,
                        doctorName = user?.name ?: "",
                        // DoctorDashboardDto.totalCourses / totalStudents are the only
                        // aggregate counts the backend returns for the doctor dashboard.
                        totalCourses = data?.totalCourses ?: 0,
                        totalStudents = data?.totalStudents ?: 0,
                        // pendingGrading is NOT mapped — DoctorDashboardDto has no such field.
                        courses = data?.courses ?: emptyList(),
                        todaySchedule = data?.schedule ?: emptyList(),
                        upcomingExams = data?.upcomingExams ?: emptyList(),
                    )
                }
            },
            onError = { e ->
                updateState { it.copy(isLoading = false, error = e.message) }
                sendNewEffect(HomeTeacherEffect.ShowError(e.message ?: "حدث خطأ"))
            },
        )
    }

    fun onManageCourseClicked(courseId: Int) {
        sendNewNavigationEffect(HomeTeacherEffect.NavigateToCourseDetail(courseId))
    }

    fun onViewAllCoursesClicked() {
        sendNewNavigationEffect(HomeTeacherEffect.NavigateToCourses)
    }

    fun onStartGradingClicked() {
        sendNewNavigationEffect(HomeTeacherEffect.NavigateToGrading)
    }
}