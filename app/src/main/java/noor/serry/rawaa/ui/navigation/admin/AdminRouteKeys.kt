package noor.serry.rawaa.ui.navigation.admin

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AdminRouteKeys : NavKey {

    @Serializable
    data object Home : AdminRouteKeys
    @Serializable
    data object Departments : AdminRouteKeys
    @Serializable
    data object Employees : AdminRouteKeys
    @Serializable
    data object Reports : AdminRouteKeys
    @Serializable
    data object Profile : AdminRouteKeys
}
