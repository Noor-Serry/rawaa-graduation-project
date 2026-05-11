package noor.serry.rawaa.ui.navigation.admin

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider


val adminEntryProvider: (NavKey) -> NavEntry<NavKey> = entryProvider {

    entry<AdminRouteKeys.Home> {
      //  HomeAdminScreen()

    }
    entry<AdminRouteKeys.Departments> {
     //   DepartmentsScreen()
    }
    entry<AdminRouteKeys.Employees> {
    //    EmployeesScreen()
    }
    entry<AdminRouteKeys.Reports> {
      //  ReportsScreen()
    }
    entry<AdminRouteKeys.Profile> {
      //  ProfileAdminScreen()
    }
}
