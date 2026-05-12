package noor.serry.rawaa.ui.screens.home_super_admin

interface HomeSuperAdminInteractionListener {

    /** Quick-action: إضافة جامعة — opens create-university flow */
    fun onCreateUniversityClick()

    /**
     * Quick-action: عرض الكل (universities) — navigates to the full
     * UniversitiesScreen where all CRUD actions are available.
     */
    fun onViewAllUniversitiesClick()

    /**
     * Tap on a university row — loads its admins list via
     * GET /api/super/universities/{id}/admins and expands the admins panel.
     */
    fun onUniversityClick(universityId: Int, universityName: String)

    /** Close / collapse the admins panel */
    fun onDismissAdminsPanel()

    /** Quick-action: الإعدادات — navigates to settings */
    fun onSettingsClick()

    /** Retry after an error */
    fun onRetry()
}
