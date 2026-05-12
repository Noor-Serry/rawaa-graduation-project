package noor.serry.rawaa.ui.screens.users_admin

interface UsersAdminInteractionListener {
    // ── Filters ───────────────────────────────────────────────────────────────
    fun onRoleFilterSelected(filter: UsersAdminUiState.RoleFilter)
    fun onSearchQueryChanged(query: String)
    fun onDepartmentFilterSelected(departmentId: Int?)

    // ── List actions (all backed by real endpoints) ────────────────────────────
    /** عرض الملف – navigates to the student / employee detail screen */
    fun onViewProfileClicked(userId: Int, userType: UsersAdminUiState.UserType)

    /** حذف – shows confirmation dialog */
    fun onDeleteClicked(userId: Int, userType: UsersAdminUiState.UserType)

    /** Confirm deletion after dialog */
    fun onDeleteConfirmed()

    /** Dismiss deletion dialog */
    fun onDeleteDismissed()

    // ── Add-user sheet ────────────────────────────────────────────────────────
    /** إضافة مستخدم جديد button */
    fun onAddUserClicked()
    fun onAddUserDismissed()
}
