package noor.serry.rawaa.ui.screens.home_super_admin

sealed interface HomeSuperAdminEffect {

    /** Navigate to the Create-University flow */
    data object NavigateToCreateUniversity : HomeSuperAdminEffect

    /** Navigate to the full Universities management screen */
    data object NavigateToUniversities : HomeSuperAdminEffect

    /** Navigate to platform-level settings */
    data object NavigateToSettings : HomeSuperAdminEffect
}
