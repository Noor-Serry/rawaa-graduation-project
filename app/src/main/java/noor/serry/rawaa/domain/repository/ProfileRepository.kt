package noor.serry.rawaa.domain.repository

import noor.serry.rawaa.domain.entity.StudentProfileEntity
import noor.serry.rawaa.domain.entity.TeacherProfileEntity

interface ProfileRepository {
    suspend fun getStudentProfile(): StudentProfileEntity
    suspend fun getTeacherProfile(): TeacherProfileEntity
}