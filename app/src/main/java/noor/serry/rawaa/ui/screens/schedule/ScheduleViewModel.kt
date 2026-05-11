package noor.serry.rawaa.ui.screens.schedule

import noor.serry.rawaa.data.dto.ScheduleSessionDto
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class ScheduleViewModel(
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<ScheduleUiState, ScheduleEffect>(
    initialState = ScheduleUiState(isLoading = true),
    dispatcherProvider = dispatchers,
), ScheduleInteractionListener {

    init { load() }

    fun load() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            // GET /api/schedules/my  → List<ScheduleSessionDto>
            action = { repository.getMySchedule().data ?: emptyList() },
            onSuccess = { sessions ->
                val byDay = DayOfWeek.entries.associateWith { day ->
                    sessions.filter { it.dayMatches(day) }.map { it.toSessionItem() }
                }
                val uniqueCourses = sessions.mapNotNull { it.courseId }.toSet().size
                val uniqueDays    = sessions.mapNotNull { it.day }.toSet().size
                updateState {
                    it.copy(
                        isLoading            = false,
                        scheduleByDay        = byDay,
                        totalDays            = uniqueDays,
                        totalCourses         = uniqueCourses,
                        totalLecturesPerWeek = sessions.size,
                    )
                }
            },
            onError = { e -> updateState { it.copy(isLoading = false, errorMessage = e.message) } },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onDaySelected(day: DayOfWeek) = updateState { it.copy(selectedDay = day) }

    override fun onViewSessionDetails(courseCode: String) =
        sendNewEffect(ScheduleEffect.NavigateToSessionDetails(courseCode))

    // Removed: onViewFullWeekSchedule — ScheduleScreen already shows the full week
}

// ── Helpers & Mappers ─────────────────────────────────────────────────────────

private fun ScheduleSessionDto.dayMatches(day: DayOfWeek): Boolean {
    val dayStr = this.day?.lowercase() ?: return false
    return when (day) {
        DayOfWeek.SUNDAY    -> dayStr == "sunday"
        DayOfWeek.MONDAY    -> dayStr == "monday"
        DayOfWeek.TUESDAY   -> dayStr == "tuesday"
        DayOfWeek.WEDNESDAY -> dayStr == "wednesday"
        DayOfWeek.THURSDAY  -> dayStr == "thursday"
        DayOfWeek.FRIDAY    -> dayStr == "friday"
        DayOfWeek.SATURDAY  -> dayStr == "saturday"
    }
}

private fun ScheduleSessionDto.toSessionItem() = SessionItem(
    courseCode    = code ?: "",
    courseName    = courseName ?: "",
    professorName = doctorName ?: "",
    location      = roomName ?: "",
    timeRange     = "$startTime - $endTime",
    type = when (type.lowercase()) {
        "lab" -> SessionType.LAB
        else  -> SessionType.LECTURE
    },
)
