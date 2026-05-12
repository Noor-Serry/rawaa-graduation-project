package noor.serry.rawaa.ui.navigation.super_admin

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AdminRouteKeys : NavKey {

    @Serializable
    data object Home : AdminRouteKeys

    @Serializable
    data object Universities : AdminRouteKeys

    @Serializable
    data object AddUniversity : AdminRouteKeys
}
