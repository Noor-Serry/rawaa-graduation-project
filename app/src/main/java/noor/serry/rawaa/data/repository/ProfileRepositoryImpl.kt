package noor.serry.rawaa.data.repository

import noor.serry.rawaa.data.mapper.toStudentProfileEntity
import noor.serry.rawaa.data.mapper.toTeacherProfileEntity
import noor.serry.rawaa.data.remote.ApiClient
import noor.serry.rawaa.domain.entity.StudentProfileEntity
import noor.serry.rawaa.domain.entity.TeacherProfileEntity
import noor.serry.rawaa.domain.repository.ProfileRepository

/**
 * Both student and teacher profiles are fetched from the same endpoint:
 *   GET /api/auth/me  →  { success, data: { id, name, email, role, avatar, is_active, profile: { ... } } }
 *
 * The "profile" object contains role-specific fields:
 *   Student : phone, national_id, department_id, department_name, level, gpa, enrollment_year
 *   Doctor  : phone, national_id, department_id, department_name, role_title, salary, hire_date
 *
 * We use extension functions in DtoMappers.kt to convert UserDto to the appropriate entity.
 */
class ProfileRepositoryImpl(
    private val api: ApiClient,
) : ProfileRepository {

    override suspend fun getStudentProfile(): StudentProfileEntity {
        val response = api.getMe()
        if (!response.success) error("getMe failed: ${response.message}")
        return response.data?.toStudentProfileEntity()
            ?: error("getMe returned null data")
    }

    override suspend fun getTeacherProfile(): TeacherProfileEntity {
        val response = api.getMe()
        if (!response.success) error("getMe failed: ${response.message}")
        return response.data?.toTeacherProfileEntity()
            ?: error("getMe returned null data")
    }
}
