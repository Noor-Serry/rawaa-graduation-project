package noor.serry.rawaa.ui.screens.universities_super_admin

interface UniversitiesInteractionListener {

    // ── Navigation ────────────────────────────────────────────────────────────

    fun onBackClick()

    // ── Filters & search ──────────────────────────────────────────────────────

    fun onSearchQueryChange(query: String)
    fun onPlanFilterChange(plan: String?)          // null = clear filter
    fun onActiveFilterChange(isActive: Int?)       // null = all, 1 = active, 0 = inactive
    fun onClearFilters()

    // ── Pagination ────────────────────────────────────────────────────────────

    fun onLoadMore()
    fun onRetry()

    // ── University row interactions ───────────────────────────────────────────

    /** Tap a row → load detail + admins and open the detail panel */
    fun onUniversityClick(universityId: Int)

    /** Close the detail / admins panel */
    fun onDismissDetailPanel()

    // ── Create ────────────────────────────────────────────────────────────────

    /** Open the "add university" bottom sheet */
    fun onCreateUniversityClick()

    fun onCreateFormNameChange(value: String)
    fun onCreateFormNameEnChange(value: String)
    fun onCreateFormSlugChange(value: String)
    fun onCreateFormEmailChange(value: String)
    fun onCreateFormPhoneChange(value: String)
    fun onCreateFormAddressChange(value: String)
    fun onCreateFormCountryChange(value: String)
    fun onCreateFormPlanChange(value: String)
    fun onCreateFormPlanExpiresAtChange(value: String)
    fun onCreateFormMaxStudentsChange(value: String)
    fun onCreateFormMaxStaffChange(value: String)
    fun onCreateFormAdminNameChange(value: String)
    fun onCreateFormAdminEmailChange(value: String)
    fun onCreateFormAdminPasswordChange(value: String)

    /** Confirm create — calls POST /api/super/universities */
    fun onConfirmCreate()

    /** Close create sheet without saving */
    fun onDismissCreateSheet()

    // ── Edit ──────────────────────────────────────────────────────────────────

    /** Open the "edit university" bottom sheet for the given item */
    fun onEditUniversityClick(universityId: Int)

    fun onEditFormNameChange(value: String)
    fun onEditFormNameEnChange(value: String)
    fun onEditFormSlugChange(value: String)
    fun onEditFormEmailChange(value: String)
    fun onEditFormPhoneChange(value: String)
    fun onEditFormAddressChange(value: String)
    fun onEditFormCountryChange(value: String)
    fun onEditFormPlanChange(value: String)
    fun onEditFormPlanExpiresAtChange(value: String)
    fun onEditFormMaxStudentsChange(value: String)
    fun onEditFormMaxStaffChange(value: String)

    /** Confirm edit — calls PUT /api/super/universities/{id} */
    fun onConfirmEdit()

    /** Close edit sheet without saving */
    fun onDismissEditSheet()

    // ── Activate / Deactivate ─────────────────────────────────────────────────

    /** PUT /api/super/universities/{id}/activate */
    fun onActivateUniversity(universityId: Int)

    /** PUT /api/super/universities/{id}/deactivate */
    fun onDeactivateUniversity(universityId: Int)

    // ── Change plan ───────────────────────────────────────────────────────────

    /** Open the change-plan dialog for the given university */
    fun onChangePlanClick(universityId: Int)

    fun onPlanDialogPlanChange(value: String)
    fun onPlanDialogExpiresAtChange(value: String)
    fun onPlanDialogMaxStudentsChange(value: String)
    fun onPlanDialogMaxStaffChange(value: String)

    /** Confirm plan change — calls PUT /api/super/universities/{id}/plan */
    fun onConfirmChangePlan()

    fun onDismissPlanDialog()
}
