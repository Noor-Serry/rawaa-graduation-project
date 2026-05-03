package noor.serry.rawaa.domain.usecase

import noor.serry.rawaa.domain.entity.StudentDashboardEntity
import noor.serry.rawaa.domain.repository.DashboardRepository

class GetStudentDashboardUseCase(private val repository: DashboardRepository) {
    suspend operator fun invoke(): StudentDashboardEntity = repository.getStudentDashboard()
}