package noor.serry.rawaa.ui.screens.schedule

import noor.serry.rawaa.domain.entity.ScheduleSessionEntity
import noor.serry.rawaa.domain.entity.SessionType as DomainSessionType
import noor.serry.rawaa.domain.usecase.GetScheduleSummaryUseCase
import noor.serry.rawaa.domain.usecase.GetWeeklyScheduleUseCase
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class ScheduleViewModel(
    private val getWeeklySchedule: GetWeeklyScheduleUseCase,
    private val getScheduleSummary: GetScheduleSummaryUseCase,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<ScheduleUiState, ScheduleEffect>(
    initialState = ScheduleUiState(isLoading = true),
    dispatcherProvider = dispatchers,
) ,ScheduleInteractionListener{

    init { load() }

    fun load() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            action = { getWeeklySchedule() to getScheduleSummary() },
            onSuccess = { (sessions, summary) ->
                val byDay = DayOfWeek.entries.associateWith { day ->
                    sessions.filter { it.dayIndex == day.ordinal }
                              .map { it.toSessionItem() }
                }
                updateState {
                    it.copy(
                        isLoading = false,
                        scheduleByDay = byDay,
                        totalDays = summary.days,
                        totalCourses = summary.courses,
                        totalLecturesPerWeek = summary.weeklyLectures,
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

    override fun onViewFullWeekSchedule() =
        sendNewEffect(ScheduleEffect.NavigateToFullWeekSchedule)
}

private fun ScheduleSessionEntity.toSessionItem() = SessionItem(
    courseCode = courseCode,
    courseName = courseName,
    professorName = instructorName,
    location = location,
    timeRange = "$startTime - $endTime",
    type = when (type) {
        DomainSessionType.LECTURE  -> SessionType.LECTURE
        DomainSessionType.LAB      -> SessionType.LAB
    },
)
