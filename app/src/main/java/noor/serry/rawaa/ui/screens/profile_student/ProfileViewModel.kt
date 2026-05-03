package noor.serry.rawaa.ui.screens.profile_student

import noor.serry.rawaa.domain.entity.StudentProfileEntity
import noor.serry.rawaa.domain.usecase.GetStudentProfileUseCase
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class ProfileViewModel(
    private val getStudentProfile: GetStudentProfileUseCase,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<ProfileUiState, ProfileEffect>(
    initialState = ProfileUiState(isLoading = true),
    dispatcherProvider = dispatchers,
),ProfileInteractionListener {

    init { load() }

    fun load() {
        updateState { it.copy(isLoading = true) }
        tryToExecute(
            action = { getStudentProfile() },
            onSuccess = { profile -> updateState { profile.toProfileUiState() } },
            onError = { updateState { it.copy(isLoading = false) } },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onEditProfileClick() {
        sendNewEffect(ProfileEffect.NavigateToEditProfile)
    }

    override fun onChangeAvatarClick() {
        sendNewEffect(ProfileEffect.OpenImagePicker)
    }

    override fun onEditFieldClick(field: ProfileField) {
        sendNewEffect(ProfileEffect.NavigateToEditField(field))
    }

    override fun onAchievementsClick() {
        sendNewEffect(ProfileEffect.NavigateToAchievements)
    }

    override fun onCertificatesClick() {
        sendNewEffect(ProfileEffect.NavigateToCertificates)
    }
}

private fun StudentProfileEntity.toProfileUiState() = ProfileUiState(
    isLoading = false,
    fullName = name,
    studentId = universityId,
    studyYears = studyYears,
    completedCourses = completedCourses,
    gpa = "%.2f".format(cgpa),
    faculty = college,
    major = major,
    level = level,
    enrollmentDate = enrollmentDate,
    email = email,
    phone = phone,
    birthDate = birthDate,
    address = address,
    achievementsCount = achievementsCount,
    certificatesCount = certificatesCount,
)
