package noor.serry.rawaa.domain.usecase

import noor.serry.rawaa.domain.entity.TeacherDashboardEntity
import noor.serry.rawaa.domain.repository.DashboardRepository

class GetTeacherDashboardUseCase(private val repository: DashboardRepository) {
    suspend operator fun invoke(): TeacherDashboardEntity = repository.getTeacherDashboard()
}