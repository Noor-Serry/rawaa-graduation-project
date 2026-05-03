package noor.serry.rawaa.data.repository

import noor.serry.rawaa.data.mapper.toEntity
import noor.serry.rawaa.data.remote.ApiClient
import noor.serry.rawaa.domain.entity.StudentDashboardEntity
import noor.serry.rawaa.domain.entity.TeacherDashboardEntity
import noor.serry.rawaa.domain.repository.DashboardRepository

class DashboardRepositoryImpl(
    private val api: ApiClient,
) : DashboardRepository {

    /**
     * GET /api/student/dashboard
     * Returns student info, enrolled courses, attendance stats, schedule, and upcoming exams.
     */
    override suspend fun getStudentDashboard(): StudentDashboardEntity {
        val response = api.getStudentDashboard()
        if (!response.success) error("getStudentDashboard failed: ${response.message}")
        return response.data?.toEntity()
            ?: error("getStudentDashboard returned null data")
    }

    /**
     * GET /api/doctor/dashboard
     * Returns total_courses, total_students, courses list, schedule, and upcoming exams.
     */
    override suspend fun getTeacherDashboard(): TeacherDashboardEntity {
        val response = api.getDoctorDashboard()
        if (!response.success) error("getDoctorDashboard failed: ${response.message}")
        return response.data?.toEntity()
            ?: error("getDoctorDashboard returned null data")
    }
}
