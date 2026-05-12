package noor.serry.rawaa.ui.screens.home_super_admin

import noor.serry.rawaa.data.dto.UniversityAdminDto
import noor.serry.rawaa.data.dto.UniversityDto
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class HomeSuperAdminViewModel(
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<HomeSuperAdminUiState, HomeSuperAdminEffect>(
    initialState = HomeSuperAdminUiState(isLoading = true),
    dispatcherProvider = dispatchers,
), HomeSuperAdminInteractionListener {

    init { load() }

    // ── Initial load: stats + first 5 universities ────────────────────────────

    fun load() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            action = {
                // Fetch platform stats and the first 5 universities in parallel.
                // The home screen only ever shows a preview of 5; the full list
                // belongs to UniversitiesScreen / UniversitiesViewModel.
                val statsResp = repository.getSuperAdminStats()
                val unisResp  = repository.getSuperAdminUniversities(page = 1, perPage = 5)
                statsResp to unisResp
            },
            onSuccess = { (statsResp, unisResp) ->
                val stats = statsResp.data
                val unis  = unisResp.data
                updateState { current ->
                    current.copy(
                        isLoading = false,
                        // Stats
                        totalUniversities  = stats?.totalUniversities  ?: 0,
                        activeUniversities = stats?.activeUniversities ?: 0,
                        totalStudents      = stats?.totalStudents      ?: 0,
                        totalDoctors       = stats?.totalDoctors       ?: 0,
                        totalEmployees     = stats?.totalEmployees     ?: 0,
                        planTrial          = stats?.planTrial          ?: 0,
                        planBasic          = stats?.planBasic          ?: 0,
                        planPro            = stats?.planPro            ?: 0,
                        planEnterprise     = stats?.planEnterprise     ?: 0,
                        // Preview – hard-capped at 5 items
                        universities = unis.take(5).map { it.toUniversityItem() },
                    )
                }
            },
            onError = { e ->
                updateState { it.copy(isLoading = false, errorMessage = e.message) }
            },
            dispatcher = dispatchers.IO,
        )
    }

    // ── Load university admins ────────────────────────────────────────────────

    private fun loadUniversityAdmins(universityId: Int) {
        updateState { it.copy(adminsLoading = true) }
        tryToExecute(
            action    = { repository.getUniversityAdmins(universityId) },
            onSuccess = { resp ->
                updateState { current ->
                    current.copy(
                        adminsLoading    = false,
                        universityAdmins = resp.data?.map { it.toUniversityAdminItem() }
                            ?: emptyList(),
                    )
                }
            },
            onError = { e ->
                updateState { it.copy(adminsLoading = false, errorMessage = e.message) }
            },
            dispatcher = dispatchers.IO,
        )
    }

    // ── Interaction listener ──────────────────────────────────────────────────

    override fun onCreateUniversityClick() =
        sendNewNavigationEffect(HomeSuperAdminEffect.NavigateToCreateUniversity)

    override fun onViewAllUniversitiesClick() =
        sendNewNavigationEffect(HomeSuperAdminEffect.NavigateToUniversities)

    override fun onUniversityClick(universityId: Int, universityName: String) {
        // Toggle: tapping the same university collapses the panel
        val current = state.value
        if (current.selectedUniversityId == universityId) {
            updateState {
                it.copy(
                    selectedUniversityId   = null,
                    selectedUniversityName = "",
                    universityAdmins       = emptyList(),
                )
            }
        } else {
            updateState {
                it.copy(
                    selectedUniversityId   = universityId,
                    selectedUniversityName = universityName,
                    universityAdmins       = emptyList(),
                )
            }
            loadUniversityAdmins(universityId)
        }
    }

    override fun onDismissAdminsPanel() {
        updateState {
            it.copy(
                selectedUniversityId   = null,
                selectedUniversityName = "",
                universityAdmins       = emptyList(),
            )
        }
    }

    override fun onSettingsClick() =
        sendNewNavigationEffect(HomeSuperAdminEffect.NavigateToSettings)

    override fun onRetry() = load()
}

// ── Mappers ───────────────────────────────────────────────────────────────────

private fun UniversityDto.toUniversityItem() = HomeSuperAdminUiState.UniversityItem(
    id            = id,
    name          = name,
    slug          = slug,
    plan          = plan,
    isActive      = isActive,
    studentCount  = studentCount ?: stats?.totalStudents ?: 0,
    doctorCount   = doctorCount  ?: stats?.totalDoctors  ?: 0,
    planExpiresAt = planExpiresAt,
)

private fun UniversityAdminDto.toUniversityAdminItem() = HomeSuperAdminUiState.UniversityAdminItem(
    id          = id,
    name        = name,
    email       = email,
    isActive    = isActive,
    lastLoginAt = lastLoginAt,
    createdAt   = createdAt,
)
