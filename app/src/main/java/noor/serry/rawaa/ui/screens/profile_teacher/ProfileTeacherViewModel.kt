package noor.serry.rawaa.ui.screens.profile_teacher

import noor.serry.rawaa.data.remote.ApiClient
import noor.serry.rawaa.domain.usecase.GetTeacherProfileUseCase
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class ProfileTeacherViewModel(
    private val getTeacherProfile: GetTeacherProfileUseCase,
    private val dispatchers: DispatcherProvider,
    private val apiClient : ApiClient
) : BaseViewModel<ProfileTeacherUiState, ProfileTeacherEffect>(
    initialState = ProfileTeacherUiState(),
    dispatcherProvider = dispatchers,
) ,ProfileTeacherInteractionListener{

    init { load()
        tryToExecute(
            action = {apiClient.getDoctorDashboard().data},
            onSuccess = {response -> updateState { it.copy(totalStudents =response?.totalStudents ?:0,
                activeCourses = response?.totalCourses ?:0
                ) }}
        )

    }

    override fun load() {
        updateState { it.copy(isLoading = true) }
        tryToExecute(
            action = { getTeacherProfile() },
            onSuccess = { profile -> updateState { profile.toProfileTeacherUiState() } },
            onError = { updateState { it.copy(isLoading = false) } },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onEditProfileClick() {
        sendNewEffect(ProfileTeacherEffect.NavigateToEditProfile)
    }
}
