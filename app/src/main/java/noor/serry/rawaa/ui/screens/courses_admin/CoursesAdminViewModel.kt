package noor.serry.rawaa.ui.screens.courses_admin

import noor.serry.rawaa.data.dto.CourseDto
import noor.serry.rawaa.data.dto.DepartmentDto
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class CoursesAdminViewModel(
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<CoursesAdminUiState, CoursesAdminEffect>(
    initialState = CoursesAdminUiState(isLoading = true),
    dispatcherProvider = dispatchers,
), CoursesAdminInteractionListener {

    init { load() }

    fun load() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            action = {
                // Only fetch active courses + departments on first load.
                // Inactive courses are fetched lazily when the user taps that tab.
                val coursesActive = repository.getCourses(isActive = 1)
                val departments   = repository.getDepartments()
                coursesActive to departments
            },
            onSuccess = { (activeResp, depsResp) ->
                updateState { current ->
                    current.copy(
                        isLoading     = false,
                        activeCourses = activeResp.data.map { it.toCourseAdminItem() },
                        departments   = depsResp.data?.map { it.toDeptFilterItem() } ?: emptyList(),
                    )
                }
            },
            onError = { e ->
                updateState { it.copy(isLoading = false, errorMessage = e.message) }
            },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onTabSelected(tab: CoursesAdminUiState.CourseAdminTab) {
        updateState { it.copy(selectedTab = tab) }
        // Fire the inactive request only on first visit to that tab.
        if (tab == CoursesAdminUiState.CourseAdminTab.INACTIVE &&
            state.value.inactiveCourses.isEmpty()
        ) {
            loadInactive(state.value.selectedDepartmentId)
        }
    }

    override fun onDepartmentFilterSelected(departmentId: Int?) {
        updateState { it.copy(selectedDepartmentId = departmentId) }
        // Reload whichever tab is currently visible.
        if (state.value.selectedTab == CoursesAdminUiState.CourseAdminTab.ACTIVE) {
            loadActive(departmentId)
        } else {
            loadInactive(departmentId)
        }
    }

    override fun onCourseClicked(courseId: Int) {
        sendNewNavigationEffect(CoursesAdminEffect.NavigateToCourseDetail(courseId))
    }

    // ── Private loaders ───────────────────────────────────────────────────────

    private fun loadActive(departmentId: Int?) {
        tryToExecute(
            action = { repository.getCourses(isActive = 1, departmentId = departmentId) },
            onSuccess = { resp ->
                updateState { current ->
                    current.copy(activeCourses = resp.data.map { it.toCourseAdminItem() })
                }
            },
            dispatcher = dispatchers.IO,
        )
    }

    private fun loadInactive(departmentId: Int?) {
        updateState { it.copy(isLoadingInactive = true) }
        tryToExecute(
            action = { repository.getCourses(isActive = 0, departmentId = departmentId) },
            onSuccess = { resp ->
                updateState { current ->
                    current.copy(
                        isLoadingInactive = false,
                        inactiveCourses   = resp.data.map { it.toCourseAdminItem() },
                    )
                }
            },
            onError = { e ->
                updateState { it.copy(isLoadingInactive = false, errorMessage = e.message) }
            },
            dispatcher = dispatchers.IO,
        )
    }
}

// ── Mappers ───────────────────────────────────────────────────────────────────

fun CourseDto.toCourseAdminItem() = CoursesAdminUiState.CourseAdminItem(
    id             = id,
    name           = name,
    code           = code,
    creditHours    = creditHours,
    departmentName = departmentName,
    doctorName     = doctorName,
    semester       = semester,
    academicYear   = academicYear,
    maxStudents    = maxStudents,
    enrolledCount  = enrolledCount ?: 0,
    isActive       = isActive == 1,
)

fun DepartmentDto.toDeptFilterItem() = CoursesAdminUiState.DeptFilterItem(
    id   = id,
    name = name,
)