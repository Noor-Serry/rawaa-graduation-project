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
        updateState { it.copy(isLoading = true, errorMessage = null) }
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
            onError = { e ->
                updateState { it.copy(isLoading = false, errorMessage = e.message) }
            },
            dispatcher = dispatchers.IO,
        )
    }

    // ── Edit mode ─────────────────────────────────────────────────────────────

    override fun onEditProfileClick() = updateState {
        it.copy(
            isEditMode  = true,
            editName    = it.fullName,
            editPhone   = it.phone,
            editEmail   = it.email,
            saveSuccess = false,
        )
    }

    override fun onCancelEditClick() = updateState { it.copy(isEditMode = false) }

    override fun onNameChanged(value: String)  = updateState { it.copy(editName  = value) }
    override fun onPhoneChanged(value: String) = updateState { it.copy(editPhone = value) }
    override fun onEmailChanged(value: String) = updateState { it.copy(editEmail = value) }

    override fun onSaveProfileClick() {
        val s = state.value
        updateState { it.copy(isSaving = true) }
        tryToExecute(
            action = {
                // 1. Update name via /api/auth/profile
                val nameChanged = s.editName.isNotBlank() && s.editName != s.fullName
                if (nameChanged) {
                    repository.updateProfile(name = s.editName)
                }
                // 2. Update phone / email via /api/students/{id}
                val phoneChanged = s.editPhone != s.phone
                val emailChanged = s.editEmail.isNotBlank() && s.editEmail != s.email
                if (phoneChanged || emailChanged) {
                    repository.updateStudent(
                        id    = s.userId,
                        phone = s.editPhone.takeIf { phoneChanged },
                        email = s.editEmail.takeIf { emailChanged },
                    )
                }
            },
            onSuccess = {
                updateState { it.copy(
                    isSaving    = false,
                    isEditMode  = false,
                    saveSuccess = true,
                    fullName    = if (s.editName.isNotBlank()) s.editName else it.fullName,
                    phone       = s.editPhone,
                    email       = if (s.editEmail.isNotBlank()) s.editEmail else it.email,
                ) }
                sendNewEffect(ProfileEffect.ShowSaveSuccess)
            },
            onError = { e ->
                updateState { it.copy(isSaving = false) }
                sendNewEffect(ProfileEffect.ShowError(e.message ?: "حدث خطأ أثناء الحفظ"))
            },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onChangeAvatarClick() = sendNewEffect(ProfileEffect.OpenImagePicker)
}

// ── Mapper ─────────────────────────────────────────────────────────────────────

fun UserDto.toProfileUiState(dashboard: StudentDashboardDto?) = ProfileUiState(
    isLoading        = false,
    userId           = id,
    fullName         = name,
    studentId        = profile?.nationalId ?: id.toString(),
    avatarUrl        = avatar,
    studyYears       = profile?.enrollmentYear?.let {
        java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) - it
    } ?: 0,
    completedCourses = dashboard?.courses?.count { it.status == "completed" } ?: 0,
    creditHours      = dashboard?.creditHours ?: 0,
    attendanceRate   = dashboard?.attendance?.attendanceRate ?: 0f,
    gpa              = profile?.gpa ?: dashboard?.gpa ?: "0.00",
    faculty          = profile?.departmentName ?: "",
    level            = profile?.level?.toString() ?: "",
    enrollmentDate   = profile?.enrollmentYear?.toString() ?: "",
    email            = email,
    phone            = profile?.phone ?: "",
)
