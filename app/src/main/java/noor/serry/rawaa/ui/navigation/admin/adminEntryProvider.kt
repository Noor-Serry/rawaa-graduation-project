package noor.serry.rawaa.ui.navigation.admin

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import noor.serry.rawaa.ui.screens.courses_admin.CoursesAdminScreen
import noor.serry.rawaa.ui.screens.reports_admin.ReportsAdminScreen
import noor.serry.rawaa.ui.screens.settings_admin.SettingsAdminScreen
import noor.serry.rawaa.ui.screens.users_admin.UsersAdminScreen
import noor.serry.rawaa.ui.MainViewModel
import noor.serry.rawaa.ui.screens.home_super_admin.HomeSuperAdminScreen
import noor.serry.rawaa.ui.screens.universities_super_admin.UniversitiesScreen
import noor.serry.rawaa.ui.screens.users_admin.AddUserScreen

val adminEntryProvider: (MainViewModel) -> (NavKey) -> NavEntry<NavKey> =
    { mainViewModel ->
        entryProvider {

            entry<AdminRouteKeys.Home> {
                HomeSuperAdminScreen()
            }

            entry<AdminRouteKeys.Users> {
                UsersAdminScreen()
            }
            entry<AdminRouteKeys.University> {
                UniversitiesScreen()
            }


            entry<AdminRouteKeys.Courses> {
                CoursesAdminScreen()
            }

            entry<AdminRouteKeys.Reports> {
                ReportsAdminScreen()
            }

            entry<AdminRouteKeys.Settings> {
                SettingsAdminScreen(mainViewModel = mainViewModel)
            }
            entry<AdminRouteKeys.AddUser> {
                AddUserScreen()
            }

            // Legacy routes — redirect to their new equivalents
            entry<AdminRouteKeys.Departments> {
                UsersAdminScreen()
            }

            entry<AdminRouteKeys.Employees> {
                UsersAdminScreen()
            }

            entry<AdminRouteKeys.Profile> {
                SettingsAdminScreen(mainViewModel = mainViewModel)
            }
        }
    }
