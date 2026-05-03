package noor.serry.rawaa.ui.screens.profile_teacher

sealed interface ProfileTeacherEffect {
    data object NavigateToEditProfile : ProfileTeacherEffect
}