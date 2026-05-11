package noor.serry.rawaa.ui.navigation.base

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
    @Serializable
    data object StudentEntry : AppRoute

    @Serializable
    data object TeacherEntry : AppRoute

    @Serializable
    data object AdminEntry : AppRoute

    @Serializable
    data object SuperAdminEntry : AppRoute
}
