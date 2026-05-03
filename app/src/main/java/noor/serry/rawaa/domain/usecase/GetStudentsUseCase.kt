package noor.serry.rawaa.domain.usecase

import noor.serry.rawaa.domain.entity.StudentEntity
import noor.serry.rawaa.domain.repository.StudentRepository

class GetStudentsUseCase(private val repository: StudentRepository) {
    suspend operator fun invoke(): List<StudentEntity> = repository.getStudents()
}