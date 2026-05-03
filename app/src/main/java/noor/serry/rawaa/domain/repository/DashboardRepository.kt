package noor.serry.rawaa.domain.repository

import noor.serry.rawaa.domain.entity.StudentDashboardEntity
import noor.serry.rawaa.domain.entity.TeacherDashboardEntity

interface DashboardRepository {
    suspend fun getStudentDashboard(): StudentDashboardEntity
    suspend fun getTeacherDashboard(): TeacherDashboardEntity
}