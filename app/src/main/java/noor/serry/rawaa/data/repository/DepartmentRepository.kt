package noor.serry.rawaa.data.repository

import noor.serry.rawaa.data.dto.DepartmentDto
import noor.serry.rawaa.data.remote.ApiClient

class DepartmentRepository(private val api: ApiClient) {
    suspend fun getDepartments(): List<DepartmentDto> {
        val response = api.getDepartments()
        if (!response.success) throw Exception("Failed to load departments: ${response.message}")
        return response.data ?: emptyList()
    }
}