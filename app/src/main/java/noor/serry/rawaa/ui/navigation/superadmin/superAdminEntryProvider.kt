package noor.serry.rawaa.ui.navigation.superadmin

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider

val superAdminEntryProvider: (NavKey) -> NavEntry<NavKey> = entryProvider {

    entry<SuperAdminRouteKeys.Home> {
        //HomeSuperAdminScreen()
    }
    entry<SuperAdminRouteKeys.Universities> {
      //  UniversitiesScreen()
    }
    entry<SuperAdminRouteKeys.Profile> {
      //  ProfileSuperAdminScreen()
    }
}
