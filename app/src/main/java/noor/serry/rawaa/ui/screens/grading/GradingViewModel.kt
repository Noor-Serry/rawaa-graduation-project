package noor.serry.rawaa.ui.screens.grading

import noor.serry.rawaa.domain.usecase.GetGradedAssignmentsUseCase
import noor.serry.rawaa.domain.usecase.GetPendingAssignmentsUseCase
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class GradingViewModel(
    private val getPendingAssignments: GetPendingAssignmentsUseCase,
    private val getGradedAssignments: GetGradedAssignmentsUseCase,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<GradingUiState, GradingEffect>(
    initialState = GradingUiState(isLoading = true),
    dispatcherProvider = dispatchers,
) , GradingInteractionListener{

    init { load() }

    fun load() {
        updateState { it.copy(isLoading = true) }
        tryToExecute(
            action = { getPendingAssignments() to getGradedAssignments() },
            onSuccess = { (pending, graded) ->
                updateState {
                    it.copy(
                        isLoading = false,
                        pendingAssignments = pending.map { a -> a.toPendingAssignmentUiModel() },
                        gradedAssignments  = graded.map  { a -> a.toGradedAssignmentUiModel() },
                        totalPendingCount  = pending.size,
                        totalGradedCount   = graded.size,
                    )
                }
            },
            onError = { updateState { it.copy(isLoading = false) } },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onTabSelected(tab: GradingTab)       = updateState { it.copy(selectedTab = tab) }
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
