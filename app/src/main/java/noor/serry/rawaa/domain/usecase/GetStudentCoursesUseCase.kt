package noor.serry.rawaa.domain.usecase

import noor.serry.rawaa.domain.entity.CourseEntity
import noor.serry.rawaa.domain.repository.CourseRepository

class GetStudentCoursesUseCase(private val repository: CourseRepository) {
    suspend operator fun invoke(): List<CourseEntity> = repository.getStudentCourses()
}

class GetAvailableCoursesUseCase(private val repository: CourseRepository) {
    suspend operator fun invoke(): List<CourseEntity> = repository.getAvailableCourses()
}

class GetTeacherCoursesUseCase(private val repository: CourseRepository) {
    suspend operator fun invoke(): List<CourseEntity> = repository.getTeacherCourses()
}