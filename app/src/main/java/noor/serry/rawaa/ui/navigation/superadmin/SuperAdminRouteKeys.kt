package noor.serry.rawaa.ui.navigation.superadmin

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface SuperAdminRouteKeys : NavKey {

    @Serializable
    data object Home : SuperAdminRouteKeys
    @Serializable
    data object Universities : SuperAdminRouteKeys
    @Serializable
    data object Profile : SuperAdminRouteKeys
}
