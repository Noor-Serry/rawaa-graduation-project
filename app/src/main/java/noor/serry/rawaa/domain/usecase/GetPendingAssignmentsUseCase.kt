package noor.serry.rawaa.domain.usecase

import noor.serry.rawaa.domain.entity.AssignmentEntity
import noor.serry.rawaa.domain.repository.AssignmentRepository

class GetPendingAssignmentsUseCase(private val repository: AssignmentRepository) {
    suspend operator fun invoke(): List<AssignmentEntity> = repository.getPendingAssignments()
}

class GetGradedAssignmentsUseCase(private val repository: AssignmentRepository) {
    suspend operator fun invoke(): List<AssignmentEntity> = repository.getGradedAssignments()
}