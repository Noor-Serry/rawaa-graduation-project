package noor.serry.rawaa.ui.screens.users_admin

/**
 * One-time side-effects emitted by [EditUserViewModel].
 */
sealed interface EditUserEffect {
    /** Editing finished; navigate back (and optionally refresh the list). */
    data class UpdatedSuccessfully(val message: String) : EditUserEffect

    /** Navigate back without saving. */
    data object NavigateBack : EditUserEffect

    /** Recoverable error – show as toast, stay on screen. */
    data class ShowError(val message: String) : EditUserEffect
}
