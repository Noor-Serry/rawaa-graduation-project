package noor.serry.rawaa.ui.navigation.student

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import noor.serry.rawaa.ui.navigation.base.AppRoute

@Serializable
sealed interface StudentRouteKeys : NavKey {

    @Serializable
    data object Home : StudentRouteKeys
    @Serializable
    data object Schedule : StudentRouteKeys
    @Serializable
    data object Courses : StudentRouteKeys
    @Serializable
    data object Notifications : StudentRouteKeys
    @Serializable
    data object Profile : StudentRouteKeys
    @Serializable
    data object Menu : StudentRouteKeys
    @Serializable
    data object PrivacyPolice : StudentRouteKeys
}