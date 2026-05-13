package noor.serry.rawaa.ui.screens.users_admin

interface ViewUserInteractionListener {
    /** Back arrow / system back pressed. */
    fun onBackClicked()

    /** تعديل button in the profile header. */
    fun onEditClicked()

    /** Retry loading after an error. */
    fun onRetryClicked()
}
