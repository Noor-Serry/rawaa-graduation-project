package noor.serry.rawaa.ui.screens.home_admin

sealed interface HomeAdminEffect {
    /** Quick-action "إضافة مقرر" → courses tab (POST /api/courses exists) */
    data object NavigateToCourses : HomeAdminEffect

    /** Quick-action "إضافة مستخدم" → users tab (POST /api/students + /api/employees exist) */
    data object NavigateToUsers : HomeAdminEffect

    /** Quick-action "التقارير" → reports tab (GET /api/admin/reports/... exists) */
    data object NavigateToReports : HomeAdminEffect

    /** Quick-action "الإعدادات" → settings tab */
    data object NavigateToSettings : HomeAdminEffect

    /** "عرض الكل" in recent-activities section → same as reports/users list */
    data object NavigateToUsers2 : HomeAdminEffect
}
