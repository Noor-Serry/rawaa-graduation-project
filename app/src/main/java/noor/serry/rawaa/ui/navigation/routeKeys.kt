package noor.serry.rawaa.ui.navigation

import kotlinx.serialization.Serializable
import androidx.navigation3.runtime.NavKey

@Serializable
sealed interface AppRoute : NavKey {
    @Serializable
    data object Onboarding : AppRoute

    @Serializable
    data object Register : AppRoute
    @Serializable
    data object Login : AppRoute
}