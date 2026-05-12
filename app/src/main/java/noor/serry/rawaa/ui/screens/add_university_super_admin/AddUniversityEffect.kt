package noor.serry.rawaa.ui.screens.add_university_super_admin

sealed interface AddUniversityEffect {

    /** Pop this screen and land back on the previous destination */
    data object NavigateBack : AddUniversityEffect

    /** University was created — go back and let the list refresh */
    data object NavigateBackAfterCreate : AddUniversityEffect

    /** Show a short snackbar / toast */
    data class ShowSnackbar(val message: String) : AddUniversityEffect
}
