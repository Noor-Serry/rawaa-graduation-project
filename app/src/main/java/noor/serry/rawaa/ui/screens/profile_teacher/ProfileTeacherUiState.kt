package noor.serry.rawaa.ui.screens.profile_teacher

import noor.serry.rawaa.data.dto.CourseDto
import noor.serry.rawaa.data.dto.EmployeeDto
import noor.serry.rawaa.data.dto.UserDto

data class ProfileTeacherUiState(
    val isLoading: Boolean = true,
    val user: UserDto? = null,
    val employee: EmployeeDto? = null,
    val activeCourses: List<CourseDto> = emptyList(),
    val totalStudents: Int = 0,
    val yearsOfExperience: Int = 0,
    val error: String? = null,
)

sealed interface ProfileTeacherEffect {
    data object NavigateToEditProfile : ProfileTeacherEffect
    data class ShowError(val message: String) : ProfileTeacherEffect
}
