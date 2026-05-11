package noor.serry.rawaa.ui.screens.profile_student

sealed interface ProfileEffect {
    data object OpenImagePicker  : ProfileEffect
    data object ShowSaveSuccess  : ProfileEffect
    data class  ShowError(val message: String) : ProfileEffect
}
