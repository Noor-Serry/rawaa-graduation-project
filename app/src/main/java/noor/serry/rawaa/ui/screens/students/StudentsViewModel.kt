package noor.serry.rawaa.ui.screens.students

import noor.serry.rawaa.domain.usecase.GetStudentsUseCase
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class StudentsViewModel(
    private val getStudents: GetStudentsUseCase,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<StudentsUiState, StudentsEffect>(
    initialState = StudentsUiState(isLoading = true),
    dispatcherProvider = dispatchers,
) ,StudentsInteractionListener{

    init { load() }

    fun load() {
        updateState { it.copy(isLoading = true) }
        tryToExecute(
            action = { getStudents() },
            onSuccess = { list ->
                val uiModels = list.map { it.toStudentUiModel() }
                updateState {
                    it.copy(
                        isLoading = false,
                        students = uiModels,
                        totalCount = uiModels.size,
                        needsFollowUpCount = uiModels.count { s -> s.statusType == StudentStatusType.NEEDS_FOLLOW_UP },
                        failingCount = 0,
                    )
                }
            },
            onError = { updateState { it.copy(isLoading = false) } },
            dispatcher = dispatchers.IO,
        )
    }


    override fun onSearchChange(query: String) {
        updateState { it.copy(searchQuery = query) }
    }

    override fun onViewProfileClick(studentId: String) {
        sendNewEffect(StudentsEffect.NavigateToStudentProfile(studentId))
    }

    override fun onSendMessageClick(studentId: String) {
        sendNewEffect(StudentsEffect.NavigateToSendMessage(studentId))

    }

    override fun onSendBulkMessageClick() {
        sendNewEffect(StudentsEffect.NavigateToSendBulkMessage)


    }
}
