package noor.serry.rawaa.ui.screens.users_admin

interface EditUserInteractionListener {

    // ── Common field changes ──────────────────────────────────────────────────
    fun onNameChanged(value: String)
    fun onEmailChanged(value: String)
    fun onPhoneChanged(value: String)
    fun onActiveToggled(value: Boolean)

    // ── Department picker ─────────────────────────────────────────────────────
    fun onDepartmentPickerOpened()
    fun onDepartmentSelected(department: UsersAdminUiState.DepartmentFilterItem)
    fun onDepartmentPickerDismissed()

    // ── Student-only ──────────────────────────────────────────────────────────
    fun onLevelChanged(value: String)
    fun onEnrollmentYearChanged(value: String)

    // ── Employee / Doctor-only ────────────────────────────────────────────────
    fun onRoleTitleChanged(value: String)

    // ── Sheet / screen lifecycle ──────────────────────────────────────────────
    fun onSaveClicked()
    fun onBackClicked()
    fun onRetryClicked()
}
