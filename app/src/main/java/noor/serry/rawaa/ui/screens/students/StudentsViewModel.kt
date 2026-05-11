package noor.serry.rawaa.ui.screens.students

import noor.serry.rawaa.data.dto.StudentDto
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class StudentsViewModel(
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<StudentsUiState, StudentsEffect>(
    initialState = StudentsUiState(isLoading = true),
    dispatcherProvider = dispatchers,
), StudentsInteractionListener {

    init { load() }

    fun load() {
        updateState { it.copy(isLoading = true) }
        tryToExecute(
            action = { repository.getStudents().data ?: emptyList() },
            onSuccess = { list ->
                val uiModels = list.map { it.toStudentUiModel() }
                updateState {
                    it.copy(
                        isLoading = false,
                        students = uiModels,
                        totalCount = uiModels.size,
                        needsFollowUpCount = uiModels.count { s -> s.statusType == StudentStatusType.NEEDS_FOLLOW_UP },
                        failingCount = uiModels.count { s -> s.grade < 50 },
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

// ── Mapper ────────────────────────────────────────────────────────────────────

private fun StudentDto.toStudentUiModel(): StudentUiModel {
    val gpaFloat = gpa?.toFloatOrNull() ?: 0f
    val statusType = when {
        gpaFloat >= 3.5f -> StudentStatusType.EXCELLENT
        gpaFloat >= 2.0f -> StudentStatusType.GOOD
        else             -> StudentStatusType.NEEDS_FOLLOW_UP
    }
    val statusLabel = when (statusType) {
        StudentStatusType.EXCELLENT      -> "ممتاز"
        StudentStatusType.GOOD           -> "جيد"
        StudentStatusType.NEEDS_FOLLOW_UP -> "يحتاج متابعة"
    }
    val gradeInt = (gpaFloat / 4f * 100).toInt()
    return StudentUiModel(
        id = id.toString(),
        name = name,
        email = email,
        statusLabel = statusLabel,
        statusType = statusType,
        attendance = 0,
        grade = gradeInt,
        assignmentsSubmitted = 0,
        totalAssignments = 0,
        assignmentProgress = 0f,
        assignmentProgressPercent = 0,
        isTrendingUp = statusType != StudentStatusType.NEEDS_FOLLOW_UP,
    )
}
