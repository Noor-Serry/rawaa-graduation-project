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
                val coursesActive   = repository.getCourses(isActive = 1)
                val coursesInactive = repository.getCourses(isActive = 0)
                val departments     = repository.getDepartments()
                Triple(coursesActive, coursesInactive, departments)
            },
            onSuccess = { (activeResp, inactiveResp, depsResp) ->
                updateState { current ->
                    current.copy(
                        isLoading       = false,
                        activeCourses   = activeResp.data.map { it.toCourseAdminItem() },
                        inactiveCourses = inactiveResp.data.map { it.toCourseAdminItem() },
                        departments     = depsResp.data?.map { it.toDeptFilterItem() } ?: emptyList(),
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
    }

    override fun onDepartmentFilterSelected(departmentId: Int?) {
        updateState { it.copy(selectedDepartmentId = departmentId) }
        loadFiltered(departmentId)
    }

    override fun onCourseClicked(courseId: Int) {
        sendNewNavigationEffect(CoursesAdminEffect.NavigateToCourseDetail(courseId))
    }

    private fun loadFiltered(departmentId: Int?) {
        tryToExecute(
            action = {
                val active   = repository.getCourses(isActive = 1, departmentId = departmentId)
                val inactive = repository.getCourses(isActive = 0, departmentId = departmentId)
                active to inactive
            },
            onSuccess = { (active, inactive) ->
                updateState { current ->
                    current.copy(
                        activeCourses   = active.data.map { it.toCourseAdminItem() },
                        inactiveCourses = inactive.data.map { it.toCourseAdminItem() },
                    )
                }
            },
            dispatcher = dispatchers.IO,
        )
    }
}

// ── Mappers ──────────────────────────────────────────────────────────────────

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
