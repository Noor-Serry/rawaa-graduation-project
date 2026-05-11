package noor.serry.rawaa.ui.screens.profile_teacher

import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class ProfileTeacherViewModel(
    private val repository: UniversityRepository,
    dispatcherProvider: DispatcherProvider,
) : BaseViewModel<ProfileTeacherUiState, ProfileTeacherEffect>(
    initialState = ProfileTeacherUiState(),
    dispatcherProvider = dispatcherProvider,
) {

    init {
        loadProfile()
    }

    fun loadProfile() {
        updateState { it.copy(isLoading = true, error = null) }
        tryToExecute(
            action = {
                val me = repository.getMe()
                val dashboard = repository.getDoctorDashboard()
                Pair(me, dashboard)
            },
            onSuccess = { (meResponse, dashboardResponse) ->
                val user = meResponse.data
                val dashboard = dashboardResponse.data
                val courses = dashboard?.courses ?: emptyList()
                val totalStudents = courses.sumOf { it.enrolledCount ?: 0 }

                updateState { state ->
                    state.copy(
                        isLoading = false,
                        user = user,
                        activeCourses = courses,
                        totalStudents = totalStudents,
                    )
                }
            },
            onError = { e ->
                updateState { it.copy(isLoading = false, error = e.message) }
                sendNewEffect(ProfileTeacherEffect.ShowError(e.message ?: "حدث خطأ"))
            },
        )
    }

    fun onEditProfileClicked() {
        sendNewNavigationEffect(ProfileTeacherEffect.NavigateToEditProfile)
    }
}
