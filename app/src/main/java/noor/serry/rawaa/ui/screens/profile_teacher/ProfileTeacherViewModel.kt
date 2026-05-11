package noor.serry.rawaa.ui.screens.profile_teacher

import noor.serry.rawaa.data.dto.CourseDto
import noor.serry.rawaa.data.dto.DoctorDashboardDto
import noor.serry.rawaa.data.dto.EmployeeDto
import noor.serry.rawaa.data.dto.UserDto
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class ProfileTeacherViewModel(
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<ProfileTeacherUiState, ProfileTeacherEffect>(
    initialState = ProfileTeacherUiState(),
    dispatcherProvider = dispatchers,
), ProfileTeacherInteractionListener {

    init { load() }

    override fun load() {
        updateState { it.copy(isLoading = true) }
        tryToExecute(
            action = {
                val me = repository.getMe().data
                val dashboard = repository.getDoctorDashboard().data
                me to dashboard
            },
            onSuccess = { (user, dashboard) ->
                if (user != null) {
                    updateState { user.toProfileTeacherUiState(dashboard) }
                } else {
                    updateState { it.copy(isLoading = false) }
                }
            },
            onError = { updateState { it.copy(isLoading = false) } },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onEditProfileClick() =
        sendNewEffect(ProfileTeacherEffect.NavigateToEditProfile)
}

// ── Mapper ────────────────────────────────────────────────────────────────────

private fun UserDto.toProfileTeacherUiState(dashboard: DoctorDashboardDto?) = ProfileTeacherUiState(
    isLoading = false,
    employeeId = id.toString(),
    name = name,
    department = profile?.departmentName ?: "",
    specialization = profile?.roleTitle ?: "",
    degree = profile?.roleTitle ?: "",
    experienceYears = profile?.hireDate?.let {
        try {
            java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) - it.take(4).toInt()
        } catch (e: Exception) { 0 }
    } ?: 0,
    enrollmentDate = profile?.hireDate ?: "",
    email = email,
    phone = profile?.phone ?: "",
    office = "",
    officeHours = "",
    totalStudents = dashboard?.totalStudents ?: 0,
    activeCourses = dashboard?.totalCourses ?: 0,
    currentCourses = dashboard?.courses?.map { it.toCourseRefUiModel() } ?: emptyList(),
    achievements = emptyList(),
)

private fun CourseDto.toCourseRefUiModel() = CourseRefUiModel(
    name = name,
    code = code,
    studentsCount = enrolledCount ?: 0,
)
