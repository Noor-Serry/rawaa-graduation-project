package noor.serry.rawaa.ui.screens.courses_student

import noor.serry.rawaa.R
import noor.serry.rawaa.data.dto.CourseDto
import noor.serry.rawaa.data.dto.StudentCourseDto
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class CoursesViewModel(
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<CoursesStudentUiState, CoursesEffect>(
    initialState = CoursesStudentUiState(isLoading = true),
    dispatcherProvider = dispatchers,
), CoursesInteractionListener {

    init { load() }

    fun load() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            action = {
                // GET /api/student/dashboard  → enrolled courses
                val dashboard = repository.getStudentDashboard().data
                // GET /api/courses            → all available courses for the university
                val available = repository.getCourses().data ?: emptyList()
                val enrolled = dashboard?.courses ?: emptyList()
                enrolled to available
            },
            onSuccess = { (enrolled, available) ->
                updateState {
                    it.copy(
                        isLoading = false,
                        myCourses = enrolled.map { c -> c.toEnrolledCourseItem() },
                        availableCourses = available.map { c -> c.toAvailableCourseItem() },
                    )
                }
            },
            onError = { e -> updateState { it.copy(isLoading = false, errorMessage = e.message) } },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onTabSelected(tab: CoursesTab) = updateState { it.copy(selectedTab = tab) }

    override fun onEnrollClick(courseId : Int) {
        tryToExecute(
            action = {
                repository.registerStudentToCourse(
                    courseId = courseId,
                    studentId =  repository.getMe().data?.profile?.id ?: 0
                )
            },
            onSuccess = {
                if (it.success){
                load()
                sendNewEffect(CoursesEffect.ShowMessage(R.string.show_message_enrolled_successfully))
                }
                else
                sendNewEffect(CoursesEffect.ShowErrorMessage(it.message ?: "فشل التسجيل في الماده"))

            },
            onError = { e -> updateState { it.copy(errorMessage = e.message) }
                sendNewEffect(CoursesEffect.ShowError(R.string.show_message_enrolled_error))
            },
            dispatcher = dispatchers.IO,
        )
    }


}

// ── Mappers ──────────────────────────────────────────────────────────────────

private fun StudentCourseDto.toEnrolledCourseItem() = EnrolledCourseItem(
    courseCode    = code,
    courseName    = courseName,
    professorName = doctorName ?: "",
    creditHours   = creditHours,
    semester      = semester ?: "",
    progressPercent = when (status) {
        "completed" -> 100
        "active"    -> 50
        else        -> 0
    },
    // nextSessionTime removed — not in StudentCourseDto
    // studentCount    removed — not in StudentCourseDto
)

private fun CourseDto.toAvailableCourseItem() = AvailableCourseItem(
    courseCode = code,
    courseName = name,

    professorName = doctorName ?: "",
    level = when (creditHours) {
        in 4..Int.MAX_VALUE -> CourseLevel.ADVANCED
        3 -> CourseLevel.INTERMEDIATE
        else -> CourseLevel.BEGINNER
    },
    studentCount = enrolledCount ?: 0,   // CourseDto.enrolledCount — nullable, defaults to 0
    creditHours = creditHours,
    semester = semester,
    courseId = this.id,
)
