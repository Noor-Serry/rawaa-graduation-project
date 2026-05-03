package noor.serry.rawaa.domain.repository

import noor.serry.rawaa.domain.entity.StudentEntity

interface StudentRepository {
    suspend fun getStudents(): List<StudentEntity>
}