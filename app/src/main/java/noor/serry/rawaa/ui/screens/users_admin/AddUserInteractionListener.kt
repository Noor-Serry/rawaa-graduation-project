package noor.serry.rawaa.ui.screens.users_admin

/**
 * Contract for all user interactions that originate inside the Add-User
 * bottom sheet.
 *
 * Implemented by [AddUserViewModel]; consumed by [AddUserSheet].
 */
interface AddUserInteractionListener {

    // ── Role selection ────────────────────────────────────────────────────────

    /** The user tapped a role chip (طالب / دكتور / موظف). */
    fun onRoleChanged(role: NewUserRole)

    // ── Common field changes ──────────────────────────────────────────────────

    fun onNameChanged(value: String)
    fun onEmailChanged(value: String)
    fun onPasswordChanged(value: String)
    fun onPhoneChanged(value: String)

    // ── Department picker ─────────────────────────────────────────────────────

    /** User tapped the department trigger field → show picker dialog. */
    fun onDepartmentPickerOpened()

    /** User selected a department from the picker dialog. */
    fun onDepartmentSelected(department: UsersAdminUiState.DepartmentFilterItem)

    /** User dismissed the picker dialog without selecting anything. */
    fun onDepartmentPickerDismissed()

    // ── Student-only fields ───────────────────────────────────────────────────

    fun onLevelChanged(value: String)
    fun onEnrollmentYearChanged(value: String)

    // ── Employee-only fields ──────────────────────────────────────────────────

    fun onRoleTitleChanged(value: String)

    // ── Sheet lifecycle ───────────────────────────────────────────────────────

    /** User tapped the primary CTA (إضافة المستخدم). Triggers validation → API call. */
    fun onSubmitClicked()

    /** User tapped إلغاء or swiped the sheet down. */
    fun onDismissClicked()
}
