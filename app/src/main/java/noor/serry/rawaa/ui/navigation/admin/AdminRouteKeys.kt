package noor.serry.rawaa.ui.navigation.admin

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AdminRouteKeys : NavKey {

    @Serializable
    data object Home : AdminRouteKeys

    @Serializable
    data object Users : AdminRouteKeys

    @Serializable
    data object Courses : AdminRouteKeys

    @Serializable
    data object Reports : AdminRouteKeys

    @Serializable
    data class AddUser () : AdminRouteKeys
    @Serializable
    data object Settings : AdminRouteKeys

    // Legacy alias kept for compatibility with existing AdminNavTab enum
    // AdminNavTab.DEPARTMENTS → navigates to Users tab (user management)
    // AdminNavTab.EMPLOYEES   → navigates to Users tab (user management)
    // AdminNavTab.PROFILE     → navigates to Settings tab
    @Deprecated("Use Users", replaceWith = ReplaceWith("Users"))
    @Serializable
    data object Departments : AdminRouteKeys

    @Deprecated("Use Users", replaceWith = ReplaceWith("Users"))
    @Serializable
    data object Employees : AdminRouteKeys

    @Deprecated("Use Settings", replaceWith = ReplaceWith("Settings"))
    @Serializable
    data object Profile : AdminRouteKeys
}
