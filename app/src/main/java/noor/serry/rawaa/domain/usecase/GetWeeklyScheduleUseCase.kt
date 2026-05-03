package noor.serry.rawaa.domain.usecase

import noor.serry.rawaa.domain.entity.ScheduleSessionEntity
import noor.serry.rawaa.domain.entity.ScheduleSummaryEntity
import noor.serry.rawaa.domain.repository.ScheduleRepository

class GetWeeklyScheduleUseCase(private val repository: ScheduleRepository) {
    suspend operator fun invoke(): List<ScheduleSessionEntity> = repository.getWeeklySchedule()
}

class GetScheduleSummaryUseCase(private val repository: ScheduleRepository) {
    suspend operator fun invoke(): ScheduleSummaryEntity = repository.getSummary()
}