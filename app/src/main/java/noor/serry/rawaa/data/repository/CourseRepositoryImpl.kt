package noor.serry.rawaa.data.repository

import noor.serry.rawaa.data.local.TokenDataStore
import noor.serry.rawaa.data.mapper.toEntity
import noor.serry.rawaa.data.remote.ApiClient
import noor.serry.rawaa.domain.entity.CourseEntity
import noor.serry.rawaa.domain.repository.CourseRepository

class CourseRepositoryImpl(
    private val api: ApiClient,
    private val tokenStore: TokenDataStore,
) : CourseRepository {

    /**
     * Returns the courses the authenticated student is currently enrolled in.
     * Uses GET /api/students/{id}/courses?status=active.
     *
     * The student's profile id is fetched from GET /api/auth/me (profile.id),
     * which is the row id in the `students` table used by all student-scoped endpoints.
     */
    override suspend fun getStudentCourses(): List<CourseEntity> {
        val meResponse = api.getMe()
        val studentProfileId = meResponse.data?.profile?.id
            ?: return emptyList()   // not a student or profile missing — return empty

        val response = api.getStudentCourses(studentId = studentProfileId, status = "active")
        return response.data
            ?.map { it.toEntity() }
            ?: emptyList()
    }

    /**
     * Returns courses available for registration — the full active course catalogue.
     * Uses GET /api/courses?is_active=1.
     */
    override suspend fun getAvailableCourses(): List<CourseEntity> {
        val response = api.getAllCourses(isActive = 1)
        return response.data.map { it.toEntity() }
    }

    /**
     * Returns courses assigned to the authenticated doctor.
     * The doctor dashboard already returns their courses; we reuse that endpoint
     * to avoid fetching the full catalogue and filtering client-side.
     */
    override suspend fun getTeacherCourses(): List<CourseEntity> {
        val response = api.getDoctorDashboard()
        return response.data?.courses
            ?.map { it.toEntity() }
            ?: emptyList()
    }
}
