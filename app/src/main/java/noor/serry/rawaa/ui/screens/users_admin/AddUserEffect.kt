package noor.serry.rawaa.ui.screens.users_admin

/**
 * One-time side-effects emitted by [AddUserViewModel].
 *
 * Consumed via [BaseViewModel.effect] / [collectLatest] in the UI layer.
 */
sealed interface AddUserEffect {

    /**
     * Emitted when the user has been created successfully.
     * The UI should dismiss the sheet and optionally show a success toast.
     *
     * @param message  Localised success message, e.g. "تم إضافة المستخدم بنجاح".
     */
    data class UserCreatedSuccessfully(val message: String) : AddUserEffect

    /**
     * A recoverable error that should be surfaced as a toast / snackbar
     * without dismissing the sheet.
     *
     * @param message  Localised error message from the API or a generic fallback.
     */
    data class ShowError(val message: String) : AddUserEffect

    /**
     * Emitted when the user explicitly cancels / dismisses the sheet.
     * Allows the parent screen to react (e.g. clear focus, reset scroll).
     */
    data object Dismissed : AddUserEffect
}
