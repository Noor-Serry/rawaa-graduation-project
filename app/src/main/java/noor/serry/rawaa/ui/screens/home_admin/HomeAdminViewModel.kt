package noor.serry.rawaa.ui.screens.home_admin

import noor.serry.rawaa.data.dto.AdminDashboardDto
import noor.serry.rawaa.data.dto.RecentRegistrationDto
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class HomeAdminViewModel(
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<HomeAdminUiState, HomeAdminEffect>(
    initialState = HomeAdminUiState(isLoading = true),
    dispatcherProvider = dispatchers,
), HomeAdminInteractionListener {

    init { load() }

    fun load() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            action = {
                // Both endpoints are always available; fetch in parallel
                val dashboardResp = repository.getAdminDashboard()
                val meResp        = repository.getMe()
                dashboardResp to meResp
            },
            onSuccess = { (dashboardResp, meResp) ->
                val dashboard = dashboardResp.data
                val adminName = meResp.data?.name ?: ""
                if (dashboard != null) {
                    updateState { dashboard.toHomeAdminUiState(adminName = adminName) }
                } else {
                    updateState { it.copy(isLoading = false, adminName = adminName) }
                }
            },
            onError = { e ->
                updateState { it.copy(isLoading = false, errorMessage = e.message) }
            },
            dispatcher = dispatchers.IO,
        )
    }

    // ── InteractionListener ───────────────────────────────────────────────────

    override fun onAddCourseClick() =
        sendNewNavigationEffect(HomeAdminEffect.NavigateToCourses)

    override fun onAddUserClick() =
        sendNewNavigationEffect(HomeAdminEffect.NavigateToUsers)

    override fun onReportsClick() =
        sendNewNavigationEffect(HomeAdminEffect.NavigateToReports)

    override fun onSettingsClick() =
        sendNewNavigationEffect(HomeAdminEffect.NavigateToSettings)

    override fun onViewAllActivitiesClick() =
        sendNewNavigationEffect(HomeAdminEffect.NavigateToUsers2)
}

// ── Mappers ───────────────────────────────────────────────────────────────────

/**
 * Maps AdminDashboardDto → HomeAdminUiState.
 *
 * Only fields that actually exist in AdminDashboardDto / AdminStatsDto are used.
 * No growth percentages, no system-status data — those do not exist in the DTO.
 *
 * totalUsers is computed from totalStudents + totalDoctors + totalEmployees,
 * all of which are real fields on AdminStatsDto.
 */
fun AdminDashboardDto.toHomeAdminUiState(adminName: String) = HomeAdminUiState(
    isLoading           = false,
    adminName           = adminName,
    universityName      = university.name,
    totalStudents       = stats.totalStudents,
    totalDoctors        = stats.totalDoctors,
    totalEmployees      = stats.totalEmployees,
    totalDepartments    = stats.totalDepartments,
    activeCourses       = stats.activeCourses,
    activeRegistrations = stats.activeRegistrations,
    upcomingExams       = stats.upcomingExams,
    totalUsers          = stats.totalStudents + stats.totalDoctors + stats.totalEmployees,
    recentRegistrations = recentRegistrations.map { it.toRecentRegistrationItem() },
)

fun RecentRegistrationDto.toRecentRegistrationItem() = HomeAdminUiState.RecentRegistrationItem(
    studentName  = studentName,
    courseName   = courseName,
    courseCode   = code,
    registeredAt = registeredAt,
)
