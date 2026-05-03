package noor.serry.rawaa.domain.repository

import noor.serry.rawaa.domain.entity.AssignmentEntity

interface AssignmentRepository {
    suspend fun getPendingAssignments(): List<AssignmentEntity>
    suspend fun getGradedAssignments(): List<AssignmentEntity>
}