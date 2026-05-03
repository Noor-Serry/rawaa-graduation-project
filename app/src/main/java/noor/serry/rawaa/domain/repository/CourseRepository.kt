package noor.serry.rawaa.domain.repository

import noor.serry.rawaa.domain.entity.CourseEntity

interface CourseRepository {
    suspend fun getStudentCourses(): List<CourseEntity>
    suspend fun getAvailableCourses(): List<CourseEntity>
    suspend fun getTeacherCourses(): List<CourseEntity>
}