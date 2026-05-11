package noor.serry.rawaa.ui.screens.courses_teacher

import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class CoursesTeacherViewModel(
    private val repository: UniversityRepository,
    dispatcherProvider: DispatcherProvider,
) : BaseViewModel<CoursesTeacherUiState, CoursesTeacherEffect>(
    initialState = CoursesTeacherUiState(),
    dispatcherProvider = dispatcherProvider,
) {

    init {
        loadCourses()
    }

    fun loadCourses() {
        updateState { it.copy(isLoading = true, error = null) }
        tryToExecute(
            action = {
                val dashboard = repository.getDoctorDashboard()
                dashboard
            },
            onSuccess = { response ->
                val data = response.data
                val allCourses = data?.courses ?: emptyList()
                val active = allCourses.filter { it.isActive == 1 }
                val archived = allCourses.filter { it.isActive == 0 }
                val totalStudents = allCourses.sumOf { it.enrolledCount ?: 0 }

                updateState { state ->
                    state.copy(
                        isLoading = false,
                        activeCourses = active,
                        archivedCourses = archived,
                        // totalStudents is derived from DoctorDashboardDto.total_students
                        // when available, otherwise sum enrolledCount across courses.
                        totalStudents = data?.totalStudents
                            ?.takeIf { it > 0 }
                            ?: totalStudents,
                    )
                }
            },
            onError = { e ->
                updateState { it.copy(isLoading = false, error = e.message) }
                sendNewEffect(CoursesTeacherEffect.ShowError(e.message ?: "حدث خطأ"))
            },
        )
    }

    fun selectTab(tab: CourseTab) {
        updateState { it.copy(selectedTab = tab) }
    }

    fun onCourseClicked(courseId: Int) {
        sendNewEffect(CoursesTeacherEffect.NavigateToCourseDetail(courseId))
    }
}
