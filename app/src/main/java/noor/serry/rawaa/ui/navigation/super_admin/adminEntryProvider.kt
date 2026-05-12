package noor.serry.rawaa.ui.navigation.super_admin

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import noor.serry.rawaa.ui.MainViewModel
import noor.serry.rawaa.ui.screens.add_university_super_admin.AddUniversityScreen
import noor.serry.rawaa.ui.screens.home_super_admin.HomeSuperAdminScreen
import noor.serry.rawaa.ui.screens.universities_super_admin.UniversitiesScreen

val adminEntryProvider: (MainViewModel) -> (NavKey) -> NavEntry<NavKey> =
    { _ ->
        entryProvider {

            entry<AdminRouteKeys.Home> {
                HomeSuperAdminScreen()
            }

            entry<AdminRouteKeys.Universities> {
                UniversitiesScreen()
            }

            entry<AdminRouteKeys.AddUniversity> {
                AddUniversityScreen()
            }
        }
    }
