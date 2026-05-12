package noor.serry.rawaa.ui.screens.universities_super_admin

sealed interface UniversitiesEffect {

    /** Go back to the home screen */
    data object NavigateBack : UniversitiesEffect

    /** Navigate to create-university standalone flow (if one exists) */
    data object NavigateToCreateUniversity : UniversitiesEffect

    /** Show a short snackbar / toast with the given message */
    data class ShowSnackbar(val message: String) : UniversitiesEffect

    /** Scroll the list to the top (e.g. after a search or filter change) */
    data object ScrollToTop : UniversitiesEffect
}
