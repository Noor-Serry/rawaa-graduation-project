package noor.serry.rawaa.ui.screens.users_admin

interface UsersAdminInteractionListener {
    // ── Filters ───────────────────────────────────────────────────────────────
    fun onRoleFilterSelected(filter: UsersAdminUiState.RoleFilter)
    fun onSearchQueryChanged(query: String)
    fun onDepartmentFilterSelected(departmentId: Int?)

    // ── List actions ──────────────────────────────────────────────────────────
    /** عرض الملف – navigates to the read-only profile screen */
    fun onViewProfileClicked(userId: Int, userType: UsersAdminUiState.UserType)

    /** تعديل – navigates to the edit screen */
    fun onEditClicked(userId: Int, userType: UsersAdminUiState.UserType)

    /** حذف – shows confirmation dialog */
    fun onDeleteClicked(userId: Int, userType: UsersAdminUiState.UserType)

    /** Confirm deletion after dialog */
    fun onDeleteConfirmed()

    /** Dismiss deletion dialog */
    fun onDeleteDismissed()

    // ── Add-user sheet ────────────────────────────────────────────────────────
    fun onAddUserClicked()
    fun onAddUserDismissed()
}
