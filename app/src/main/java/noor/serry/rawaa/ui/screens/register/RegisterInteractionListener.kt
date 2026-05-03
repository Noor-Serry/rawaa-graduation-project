package noor.serry.rawaa.ui.screens.register

import noor.serry.rawaa.data.dto.DepartmentDto

interface RegisterInteractionListener {
    fun onFullNameChange(value: String)
    fun onEmailChange(value: String)
    fun onPhoneChange(value: String)
    fun onUniversityChange(value: String)
    fun onRoleSelected(role: UserRole)
    fun onPasswordChange(value: String)
    fun onConfirmPasswordChange(value: String)
    fun onNextPage()
    fun onPreviousPage()
    fun onRegister()
    fun onGoogleSignUp()
    fun onNavigateToLogin()
    fun onDepartmentSelected(department: DepartmentDto)
    fun onRoleTitleChange(v: String)
}
