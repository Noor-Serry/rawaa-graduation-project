package noor.serry.rawaa.data.repository

import noor.serry.rawaa.data.mapper.toEntity
import noor.serry.rawaa.data.remote.ApiClient
import noor.serry.rawaa.domain.entity.ScheduleSessionEntity
import noor.serry.rawaa.domain.entity.ScheduleSummaryEntity
import noor.serry.rawaa.domain.entity.SessionType
import noor.serry.rawaa.domain.repository.ScheduleRepository

class ScheduleRepositoryImpl(
    private val api: ApiClient,
) : ScheduleRepository {

    /**
     * GET /api/schedules/my
     * Returns the weekly schedule for the authenticated student or doctor.
     * Each item has: id, course_name, code, day, start_time, end_time,
     *                room_name, building, type, doctor_name.
     */
    override suspend fun getWeeklySchedule(): List<ScheduleSessionEntity> {
        val response = api.getMySchedule()
        if (!response.success) return emptyList()
        return response.data
            ?.map { it.toEntity() }
            ?: emptyList()
    }

    /**
     * The API has no dedicated summary endpoint; derive values from the schedule list.
     */
    override suspend fun getSummary(): ScheduleSummaryEntity {
        val sessions = getWeeklySchedule()
        val uniqueDays     = sessions.map { it.dayIndex }.distinct().size
        val uniqueCourses  = sessions.map { it.courseCode }.distinct().size
        val lectureCount   = sessions.count { it.type == SessionType.LECTURE }
        return ScheduleSummaryEntity(
            days = uniqueDays,
            courses = uniqueCourses,
            weeklyLectures = lectureCount,
        )
    }
}
