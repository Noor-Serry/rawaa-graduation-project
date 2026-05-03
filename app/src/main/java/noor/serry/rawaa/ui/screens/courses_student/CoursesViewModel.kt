package noor.serry.rawaa.ui.screens.courses_student

import noor.serry.rawaa.domain.entity.CourseEntity
import noor.serry.rawaa.domain.usecase.GetAvailableCoursesUseCase
import noor.serry.rawaa.domain.usecase.GetStudentCoursesUseCase
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class CoursesViewModel(
    private val getStudentCourses: GetStudentCoursesUseCase,
    private val getAvailableCourses: GetAvailableCoursesUseCase,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<CoursesStudentUiState, CoursesEffect>(
    initialState = CoursesStudentUiState(isLoading = true),
    dispatcherProvider = dispatchers,
) , CoursesInteractionListener{

    init { load() }

    fun load() {
        updateState { it.copy(isLoading = true) }
        tryToExecute(
            action = { getStudentCourses() to getAvailableCourses() },
            onSuccess = { (enrolled, available) ->
                updateState {
                    it.copy(
                        isLoading = false,
                        myCourses = enrolled.map { c -> c.toEnrolledCourseItem() },
                        availableCourses = available.map { c -> c.toAvailableCourseItem() },
                    )
                }
            },
            onError = { updateState { it.copy(isLoading = false) } },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onTabSelected(tab: CoursesTab) = updateState { it.copy(selectedTab = tab) }
    override fun onOpenCourse(code: String) = sendNewEffect(CoursesEffect.NavigateToCourseDetails(code))
    override fun onLecturesClick(code: String) = sendNewEffect(CoursesEffect.NavigateToLectures(code))
    override fun onHomeworkClick(code: String) = sendNewEffect(CoursesEffect.NavigateToHomework(code))
    override fun onMaterialsClick(code: String) = sendNewEffect(CoursesEffect.NavigateToMaterials(code))
    override fun onEnrollClick(code: String) = sendNewEffect(CoursesEffect.NavigateToEnroll(code))
}

private fun CourseEntity.toEnrolledCourseItem() = EnrolledCourseItem(
    courseCode = code,
    courseName = name,
    professorName = instructorName,
    progressPercent = (progress * 100).toInt(),
    nextSessionTime = nextSessionTime,
    studentCount = totalStudents,
)

private fun CourseEntity.toAvailableCourseItem() = AvailableCourseItem(
    courseCode = code,
    courseName = name,
    professorName = instructorName,
    level = when (level) {
        noor.serry.rawaa.domain.entity.CourseLevel.ADVANCED     -> CourseLevel.ADVANCED
        noor.serry.rawaa.domain.entity.CourseLevel.INTERMEDIATE -> CourseLevel.INTERMEDIATE
        noor.serry.rawaa.domain.entity.CourseLevel.BEGINNER     -> CourseLevel.BEGINNER
    },
    studentCount = totalStudents,
    durationWeeks = durationWeeks,
)
