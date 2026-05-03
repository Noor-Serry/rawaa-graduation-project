package noor.serry.rawaa.domain.repository

import noor.serry.rawaa.domain.entity.ScheduleSessionEntity
import noor.serry.rawaa.domain.entity.ScheduleSummaryEntity

interface ScheduleRepository {
    suspend fun getWeeklySchedule(): List<ScheduleSessionEntity>
    suspend fun getSummary(): ScheduleSummaryEntity
}