package noor.serry.rawaa.ui.screens.reports_admin

import noor.serry.rawaa.data.dto.AttendanceReportRowDto
import noor.serry.rawaa.data.dto.GradesReportRowDto
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class ReportsAdminViewModel(
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<ReportsAdminUiState, ReportsAdminEffect>(
    initialState = ReportsAdminUiState(isLoading = true),
    dispatcherProvider = dispatchers,
), ReportsAdminInteractionListener {

    init { load() }

    fun load() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            action = {
                val gradesResp     = repository.getGradesReport()
                val attendanceResp = repository.getAttendanceReport()
                gradesResp to attendanceResp
            },
            onSuccess = { (gradesResp, attendanceResp) ->
                updateState { current ->
                    current.copy(
                        isLoading      = false,
                        gradesRows     = gradesResp.data?.map { it.toGradesRowItem() } ?: emptyList(),
                        attendanceRows = attendanceResp.data?.map { it.toAttendanceRowItem() } ?: emptyList(),
                    )
                }
            },
            onError = { e ->
                updateState { it.copy(isLoading = false, errorMessage = e.message) }
            },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onTabSelected(tab: ReportsAdminUiState.ReportTab) {
        updateState { it.copy(selectedTab = tab) }
    }
}

// ── Mappers ──────────────────────────────────────────────────────────────────

fun GradesReportRowDto.toGradesRowItem() = ReportsAdminUiState.GradesRowItem(
    courseName = courseName,
    code       = code,
    enrolled   = enrolled,
    avgGrade   = avgGrade,
    maxGrade   = maxGrade,
    minGrade   = minGrade,
    passed     = passed,
    failed     = failed,
)

fun AttendanceReportRowDto.toAttendanceRowItem() = ReportsAdminUiState.AttendanceRowItem(
    studentName   = studentName,
    level         = level,
    dept          = dept,
    totalSessions = totalSessions,
    present       = present,
    absent        = absent,
    rate          = rate,
)
