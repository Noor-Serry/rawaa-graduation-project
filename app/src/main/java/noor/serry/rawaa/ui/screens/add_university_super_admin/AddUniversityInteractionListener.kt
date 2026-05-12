package noor.serry.rawaa.ui.screens.add_university_super_admin

interface AddUniversityInteractionListener {

    // ── Navigation ────────────────────────────────────────────────────────────

    fun onBackClick()

    // ── University info fields ─────────────────────────────────────────────────

    fun onNameChange(value: String)
    fun onNameEnChange(value: String)
    fun onSlugChange(value: String)
    fun onEmailChange(value: String)
    fun onPhoneChange(value: String)
    fun onAddressChange(value: String)
    fun onCountryChange(value: String)

    // ── Plan fields ────────────────────────────────────────────────────────────

    fun onPlanChange(value: String)
    fun onPlanExpiresAtChange(value: String)
    fun onMaxStudentsChange(value: String)
    fun onMaxStaffChange(value: String)

    // ── First admin credentials ────────────────────────────────────────────────

    fun onAdminNameChange(value: String)
    fun onAdminEmailChange(value: String)
    fun onAdminPasswordChange(value: String)
    fun onTogglePasswordVisibility()

    // ── Submit ─────────────────────────────────────────────────────────────────

    /** Validate and call POST /api/super/universities */
    fun onSubmit()
}
