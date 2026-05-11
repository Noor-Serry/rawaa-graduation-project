package noor.serry.rawaa.ui.screens.student_profile_teacher

sealed interface StudentProfileEffect {
    data object NavigateBack : StudentProfileEffect
    data class ShowError(val message: String) : StudentProfileEffect
}
