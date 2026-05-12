package noor.serry.rawaa.ui.screens.home_admin

interface HomeAdminInteractionListener {
    /** Quick-action: إضافة مقرر  — navigates to Courses tab */
    fun onAddCourseClick()

    /** Quick-action: إضافة مستخدم — navigates to Users tab */
    fun onAddUserClick()

    /** Quick-action: التقارير — navigates to Reports tab */
    fun onReportsClick()

    /** Quick-action: الإعدادات — navigates to Settings tab */
    fun onSettingsClick()

    /** "عرض الكل" next to النشاطات الأخيرة — navigates to Users tab */
    fun onViewAllActivitiesClick()
}
