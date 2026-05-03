package noor.serry.rawaa.data.repository

import noor.serry.rawaa.data.mapper.toEntity
import noor.serry.rawaa.data.remote.ApiClient
import noor.serry.rawaa.domain.entity.StudentEntity
import noor.serry.rawaa.domain.repository.StudentRepository

class StudentRepositoryImpl(
    private val api: ApiClient,
) : StudentRepository {

    /**
     * GET /api/students
     * The response is a paginated wrapper:
     *   { success, data: [...], pagination: { total, per_page, current_page, last_page } }
     *
     * We fetch page 1 with a large page size; extend to support cursor-based paging if needed.
     */
    override suspend fun getStudents(): List<StudentEntity> {
        val response = api.getStudents(page = 1, perPage = 100)
        return response.data.map { it.toEntity() }
    }
}
