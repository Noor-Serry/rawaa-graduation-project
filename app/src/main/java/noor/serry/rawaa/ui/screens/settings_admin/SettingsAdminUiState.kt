package noor.serry.rawaa.ui.screens.settings_admin

data class SettingsAdminUiState(
    // From UserDto
    val adminName: String = "",
    val adminEmail: String = "",
    // From UserDto.profile (UserProfileDto)
    val roleTitle: String = "",
    val phone: String? = null,
    // From AdminDashboardDto.university (AdminUniversityRefDto)
    val universityName: String = "",
    val universityPlan: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // Change-password dialog state
    val isChangingPassword: Boolean = false,
    val changePasswordSuccess: Boolean = false,
    val changePasswordError: String? = null,
)
