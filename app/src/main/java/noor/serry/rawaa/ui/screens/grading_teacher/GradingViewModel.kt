package noor.serry.rawaa.ui.screens.grading_teacher

import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

/**
 * ViewModel for the teacher grading screen.
 *
 * Mirrors the structure of ProfileTeacherViewModel exactly:
 *   • Extends BaseViewModel<GradingUiState, GradingEffect>
 *   • Uses tryToExecute / updateState / sendNewEffect
 *   • Injected via constructor (Koin)
 *
 * Data source: GET /api/doctor/dashboard  →  DoctorDashboardDto
 *   • DoctorDashboardDto.totalStudents   → summary card
 *   • DoctorDashboardDto.totalCourses    → summary card
 *   • DoctorDashboardDto.courses         → mapped to CourseGradingUiModel
 *
 * Tab split (client-side):
 *   ACTIVE   → courses where isActive == 1
 *   INACTIVE → courses where isActive == 0
 */
class GradingViewModel(
    private val repository: UniversityRepository,
    dispatcherProvider: DispatcherProvider,
) : BaseViewModel<GradingUiState, GradingEffect>(
    initialState       = GradingUiState(),
    dispatcherProvider = dispatcherProvider,
), GradingInteractionListener {

    init {
        loadDashboard()
    }

    // ── GradingInteractionListener ────────────────────────────────────────────

    override fun onTabSelected(tab: GradingTab) {
        updateState { state ->
            state.copy(
                selectedTab     = tab,
                filteredCourses = applyFilters(state.allCourses, tab, state.searchQuery),
            )
        }
    }

    override fun onSearchChange(query: String) {
        updateState { state ->
            state.copy(
                searchQuery     = query,
                filteredCourses = applyFilters(state.allCourses, state.selectedTab, query),
            )
        }
    }

    override fun onRetry() = loadDashboard()

    // ── Data loading ──────────────────────────────────────────────────────────

    fun loadDashboard() {
        updateState { it.copy(isLoading = true, error = null) }
        tryToExecute(
            action    = { repository.getDoctorDashboard() },
            onSuccess = { response ->
                val dashboard = response.data
                val courses   = dashboard?.courses
                    ?.map { it.toCourseGradingUiModel() }
                    ?: emptyList()

                updateState { state ->
                    state.copy(
                        isLoading     = false,
                        totalStudents = dashboard?.totalStudents ?: 0,
                        totalCourses  = dashboard?.totalCourses  ?: 0,
                        allCourses    = courses,
                        filteredCourses = applyFilters(courses, state.selectedTab, state.searchQuery),
                    )
                }
            },
            onError = { e ->
                updateState { it.copy(isLoading = false, error = e.message) }
                sendNewEffect(GradingEffect.ShowError(e.message ?: "حدث خطأ"))
            },
        )
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun applyFilters(
        all: List<CourseGradingUiModel>,
        tab: GradingTab,
        query: String,
    ): List<CourseGradingUiModel> {
        val tabFiltered = when (tab) {
            GradingTab.ACTIVE   -> all.filter {  it.isActive }
            GradingTab.INACTIVE -> all.filter { !it.isActive }
        }
        return if (query.isBlank()) tabFiltered
        else tabFiltered.filter { course ->
            course.name.contains(query, ignoreCase = true) ||
            course.code.contains(query, ignoreCase = true) ||
            course.departmentName?.contains(query, ignoreCase = true) == true
        }
    }
}
