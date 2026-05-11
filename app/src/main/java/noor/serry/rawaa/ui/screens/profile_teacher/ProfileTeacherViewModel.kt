package noor.serry.rawaa.ui.screens.profile_teacher

import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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
                val hireDate = user?.profile?.hireDate
                val years = hireDate?.let { computeYearsOfExperience(it) } ?: 0

                updateState { state ->
                    state.copy(
                        isLoading = false,
                        user = user,
                        activeCourses = courses,
                        totalStudents = totalStudents,
                        yearsOfExperience = years,
                    )
                }
            },
            onError = { e ->
                updateState { it.copy(isLoading = false, error = e.message) }
                sendNewEffect(ProfileTeacherEffect.ShowError(e.message ?: "حدث خطأ"))
            },
        )
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private fun computeYearsOfExperience(hireDateStr: String): Int {
        val formats = listOf(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        )
        val parsed = formats.firstNotNullOfOrNull { fmt ->
            try {
                LocalDate.parse(hireDateStr.take(10), fmt)
            } catch (e: DateTimeParseException) {
                null
            }
        } ?: return 0
        return maxOf(0, LocalDate.now().year - parsed.year)
    }
}
