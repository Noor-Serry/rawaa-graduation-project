package noor.serry.rawaa.domain.usecase

import noor.serry.rawaa.domain.entity.StudentProfileEntity
import noor.serry.rawaa.domain.entity.TeacherProfileEntity
import noor.serry.rawaa.domain.repository.ProfileRepository

class GetStudentProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(): StudentProfileEntity = repository.getStudentProfile()
}

class GetTeacherProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(): TeacherProfileEntity = repository.getTeacherProfile()
}