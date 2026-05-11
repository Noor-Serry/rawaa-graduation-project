package noor.serry.rawaa.ui.screens.grading

import noor.serry.rawaa.data.dto.CourseStudentDto
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class GradingViewModel(
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<GradingUiState, GradingEffect>(
    initialState = GradingUiState(isLoading = true),
    dispatcherProvider = dispatchers,
), GradingInteractionListener {

    init { load() }

    fun load() {
        updateState { it.copy(isLoading = true) }
        tryToExecute(
            action = {
                // Fetch courses first to then get students (pending grades) per course
                val courses = repository.getCourses().data ?: emptyList()
                val pending = mutableListOf<PendingAssignmentUiModel>()
                val graded = mutableListOf<GradedAssignmentUiModel>()

                for (course in courses) {
                    val students = repository.getCourseStudents(course.id).data ?: emptyList()
                    val pendingStudents = students.filter { it.grade == null }
                    val gradedStudents = students.filter { it.grade != null }

                    if (pendingStudents.isNotEmpty()) {
                        pending.add(
                            PendingAssignmentUiModel(
                                id = course.id.toString(),
                                title = course.name,
                                courseName = course.name,
                                deadline = "",
                                submittedCount = gradedStudents.size,
                                totalStudents = students.size,
                                completionPercent = if (students.isNotEmpty())
                                    (gradedStudents.size * 100 / students.size) else 0,
                                completionProgress = if (students.isNotEmpty())
                                    gradedStudents.size.toFloat() / students.size else 0f,
                                averageGradingMinutes = 5,
                            )
                        )
                    }
                    if (gradedStudents.isNotEmpty()) {
                        val avg = gradedStudents.mapNotNull { it.grade }.average()
                        graded.add(
                            GradedAssignmentUiModel(
                                id = course.id.toString(),
                                title = course.name,
                                courseName = course.name,
                                gradedDate = "",
                                totalStudents = students.size,
                                averageGrade = avg.toInt(),
                            )
                        )
                    }
                }
                pending to graded
            },
            onSuccess = { (pending, graded) ->
                updateState {
                    it.copy(
                        isLoading = false,
                        pendingAssignments = pending,
                        gradedAssignments = graded,
                        totalPendingCount = pending.size,
                        totalGradedCount = graded.size,
                    )
                }
            },
            onError = { updateState { it.copy(isLoading = false) } },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onTabSelected(tab: GradingTab) = updateState { it.copy(selectedTab = tab) }

    override fun onSearchChange(query: String) {
        updateState { it.copy(searchQuery = query) }
    }

    override fun onStartGradingClick(assignmentId: String) {
        sendNewEffect(GradingEffect.NavigateToGradeAssignment(assignmentId))
    }

    override fun onViewDetailsClick(assignmentId: String) {
        sendNewEffect(GradingEffect.NavigateToAssignmentDetails(assignmentId))
    }

    override fun onViewStatsClick(assignmentId: String) {
        sendNewEffect(GradingEffect.NavigateToAssignmentStats(assignmentId))
    }
}
