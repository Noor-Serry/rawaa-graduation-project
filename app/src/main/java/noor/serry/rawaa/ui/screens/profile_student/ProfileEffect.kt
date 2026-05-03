package noor.serry.rawaa.ui.screens.profile_student

sealed interface ProfileEffect {
    data object NavigateToEditProfile : ProfileEffect
    data object NavigateToAchievements : ProfileEffect
    data object NavigateToCertificates : ProfileEffect
    data object OpenImagePicker : ProfileEffect
    data class NavigateToEditField(val field: ProfileField) : ProfileEffect
}

enum class ProfileField { EMAIL, PHONE, BIRTH_DATE, ADDRESS }
