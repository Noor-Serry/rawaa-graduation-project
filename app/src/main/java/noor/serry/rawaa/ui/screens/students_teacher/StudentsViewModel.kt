package noor.serry.rawaa.ui.screens.students_teacher

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
                        // Removed: failingCount — relied on a synthetic grade Int that was a GPA conversion,
                        //   not a real server-provided grade field. No actual grade data is available in StudentDto.
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

    // Removed: onSendMessageClick — no messaging endpoint exists in UniversityRepository
    // Removed: onSendBulkMessageClick — no bulk messaging endpoint exists in UniversityRepository
}

// ── Mapper ────────────────────────────────────────────────────────────────────

private fun StudentDto.toStudentUiModel(): StudentUiModel {
    val gpaFloat = gpa?.toFloatOrNull() ?: 0f
    // Status is derived from the real gpa field — acceptable UI-side classification
    val statusType = when {
        gpaFloat >= 3.5f -> StudentStatusType.EXCELLENT
        gpaFloat >= 2.0f -> StudentStatusType.GOOD
        else             -> StudentStatusType.NEEDS_FOLLOW_UP
    }
    val statusLabel = when (statusType) {
        StudentStatusType.EXCELLENT       -> "ممتاز"
        StudentStatusType.GOOD            -> "جيد"
        StudentStatusType.NEEDS_FOLLOW_UP -> "يحتاج متابعة"
    }
    return StudentUiModel(
        id = id.toString(),
        name = name,
        email = email,
        departmentName = departmentName,
        level = level,
        gpa = gpa,                          // pass raw String as returned by server
        statusLabel = statusLabel,
        statusType = statusType,
        enrollmentYear = enrollmentYear,
        isActive = isActive == 1,
        // Removed: attendance — not in StudentDto
        // Removed: grade (Int) — converting GPA to a % integer was misleading; gpa String is now passed directly
        // Removed: assignmentsSubmitted / totalAssignments — not in StudentDto
        // Removed: isTrendingUp — no backend signal
    )
}
