package noor.serry.rawaa.ui.screens.register

import noor.serry.rawaa.data.dto.DepartmentDto

data class RegisterUiState(
    val currentPage: Int = 0,
    // Page 0 - Personal Info
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    // Page 1 - Academic Info
    val university: String = "",
    val selectedRole: UserRole = UserRole.STUDENT,
    val departments: List<DepartmentDto> = emptyList(),
    val isDepartmentsLoading: Boolean = false,
    val selectedDepartment: DepartmentDto? = null,
    val departmentError: String? = null,
    // Page 2 - Account Security
    val password: String = "",
    val confirmPassword: String = "",
    val passwordsMatch: Boolean = true,
    val roleTitle: String = "",          // doctor only
    // Loading / error
    val isLoading: Boolean = false,
    val generalError: String? = null,
    // Field errors
    val fullNameError: String? = null,
    val emailError: String? = null,
    val universityError: String? = null,
    val passwordError: String? = null,
    val roleTitleError: String? = null,
)
enum class UserRole(val label: String, val apiValue: String) {
    STUDENT("طالب", "student"),
    TEACHER("دكتور", "doctor"),
    STAKEHOLDER("موظف", "employee"),
    ADMIN("مسؤول", "admin"),
}
