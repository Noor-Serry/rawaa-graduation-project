package noor.serry.rawaa.data.repository

import noor.serry.rawaa.data.mapper.toAssignmentEntity
import noor.serry.rawaa.data.remote.ApiClient
import noor.serry.rawaa.domain.entity.AssignmentEntity
import noor.serry.rawaa.domain.repository.AssignmentRepository

/**
 * The API v3 does not have a dedicated /api/assignments endpoint.
 * The closest equivalent is the exams system:
 *   GET /api/exams?is_published=1  — published (upcoming) exams visible to all roles
 *
 * "Pending" assignments → published exams not yet past their end_at.
 * "Graded"  assignments → exams past their end_at (results already available).
 *
 * If a proper assignments endpoint is added later, replace the call below.
 */
class AssignmentRepositoryImpl(
    private val api: ApiClient,
) : AssignmentRepository {

    override suspend fun getPendingAssignments(): List<AssignmentEntity> {
        val response = api.getExams(isPublished = 1)
        return response.data.map { it.toAssignmentEntity() }
    }

    override suspend fun getGradedAssignments(): List<AssignmentEntity> {
        // Re-use the same list — the UI can filter by deadline in the ViewModel if needed.
        val response = api.getExams(isPublished = 1)
        return response.data.map { it.toAssignmentEntity() }
    }
}
