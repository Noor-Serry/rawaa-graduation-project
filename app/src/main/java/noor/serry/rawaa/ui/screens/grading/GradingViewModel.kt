package noor.serry.rawaa.ui.screens.grading

import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class GradingViewModel(
    private val repository: UniversityRepository,
    dispatcherProvider: DispatcherProvider,
) : BaseViewModel<GradingUiState, GradingEffect>(
    initialState = GradingUiState(),
    dispatcherProvider = dispatcherProvider,
) {

    init {
        loadGradingData()
    }

    fun loadGradingData() {
        updateState { it.copy(isLoading = true, error = null) }
        tryToExecute(
            action = { repository.getDoctorDashboard() },
            onSuccess = { response ->
                val courses = response.data?.courses ?: emptyList()

                // Build grading items from courses (pending = items with enrolledCount > 0)
                val pendingItems = courses.map { course ->
                    GradingItem(
                        courseId = course.id,
                        assignmentTitle = "مشروع ${course.name} النهائي",
                        courseName = course.name,
                        deadlineDaysAgo = "انتهى منذ يوم",
                        totalPoints = 100,
                        submittedCount = ((course.enrolledCount ?: 0) * 0.38f).toInt(),
                        totalStudents = course.enrolledCount ?: 0,
                        submittedPercent = 38,
                        avgGradingMinutes = 25,
                        isGraded = false,
                    )
                }

                val gradedItems = courses.map { course ->
                    GradingItem(
                        courseId = course.id,
                        assignmentTitle = "واجب ${course.name}",
                        courseName = course.name,
                        deadlineDaysAgo = "تم التصحيح منذ أسبوع",
                        totalPoints = 50,
                        submittedCount = course.enrolledCount ?: 0,
                        totalStudents = course.enrolledCount ?: 0,
                        submittedPercent = 100,
                        avgGradingMinutes = 10,
                        isGraded = true,
                        avgGrade = 85,
                    )
                }

                val pending = pendingItems.sumOf { it.submittedCount }
                val graded = gradedItems.sumOf { it.submittedCount }

                updateState { state ->
                    state.copy(
                        isLoading = false,
                        pendingAssignments = pendingItems,
                        gradedAssignments = gradedItems,
                        pendingCount = pending,
                        gradedCount = graded,
                    )
                }
            },
            onError = { e ->
                updateState { it.copy(isLoading = false, error = e.message) }
                sendNewEffect(GradingEffect.ShowError(e.message ?: "حدث خطأ"))
            },
        )
    }

    fun onSearchQueryChanged(query: String) {
        updateState { it.copy(searchQuery = query) }
    }

    fun selectTab(tab: GradingTab) {
        updateState { it.copy(selectedTab = tab) }
    }

    fun onStartGrading(courseId: Int) {
        sendNewNavigationEffect(GradingEffect.StartGrading(courseId))
    }
}
