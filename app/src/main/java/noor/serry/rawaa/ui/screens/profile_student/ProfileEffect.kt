package noor.serry.rawaa.ui.screens.profile_student

sealed interface ProfileEffect {
    data object NavigateToEditProfile : ProfileEffect
    data object OpenImagePicker       : ProfileEffect
    data class  NavigateToEditField(val field: ProfileField) : ProfileEffect
    // Removed: NavigateToAchievements — no achievements endpoint on server
    // Removed: NavigateToCertificates — no certificates endpoint on server
}

enum class ProfileField { EMAIL, PHONE }
// Removed: BIRTH_DATE, ADDRESS — not present in UserDto or UserProfileDto
