package noor.serry.rawaa.ui.screens.profile_student

import noor.serry.rawaa.data.dto.StudentDashboardDto
import noor.serry.rawaa.data.dto.UserDto
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class ProfileViewModel(
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<ProfileUiState, ProfileEffect>(
    initialState = ProfileUiState(isLoading = true),
    dispatcherProvider = dispatchers,
), ProfileInteractionListener {

    init { load() }

    fun load() {
        updateState { it.copy(isLoading = true) }
        tryToExecute(
            action = {
                val me        = repository.getMe().data
                val dashboard = repository.getStudentDashboard().data
                me to dashboard
            },
            onSuccess = { (user, dashboard) ->
                if (user != null) {
                    updateState { user.toProfileUiState(dashboard) }
                } else {
                    updateState { it.copy(isLoading = false) }
                }
            },
            onError = { updateState { it.copy(isLoading = false) } },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onEditProfileClick()                   = sendNewEffect(ProfileEffect.NavigateToEditProfile)
    override fun onChangeAvatarClick()                  = sendNewEffect(ProfileEffect.OpenImagePicker)
    override fun onEditFieldClick(field: ProfileField)  = sendNewEffect(ProfileEffect.NavigateToEditField(field))
}

// ── Mapper ────────────────────────────────────────────────────────────────────

fun UserDto.toProfileUiState(dashboard: StudentDashboardDto?) = ProfileUiState(
    isLoading       = false,
    fullName        = name,
    studentId       = profile?.nationalId ?: id.toString(),
    avatarUrl       = avatar,
    studyYears      = profile?.enrollmentYear?.let {
        java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) - it
    } ?: 0,
    completedCourses = dashboard?.courses?.count { it.status == "completed" } ?: 0,
    gpa             = profile?.gpa ?: dashboard?.student?.gpa ?: "0.00",
    faculty         = profile?.departmentName ?: "",
    major           = profile?.departmentName ?: "",
    level           = profile?.level?.toString() ?: "",
    enrollmentDate  = profile?.enrollmentYear?.toString() ?: "",
    email           = email,
    phone           = profile?.phone ?: "",
    // birthDate removed — not in UserDto or UserProfileDto
    // address removed   — not in UserDto or UserProfileDto
    // achievementsCount removed — no server endpoint
    // certificatesCount removed — no server endpoint
)
