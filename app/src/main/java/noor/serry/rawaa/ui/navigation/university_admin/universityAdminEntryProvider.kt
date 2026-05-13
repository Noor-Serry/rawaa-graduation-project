package noor.serry.rawaa.ui.navigation.university_admin

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import noor.serry.rawaa.ui.MainViewModel
import noor.serry.rawaa.ui.screens.courses_admin.CoursesAdminScreen
import noor.serry.rawaa.ui.screens.departments_admin.DepartmentsAdminScreen
import noor.serry.rawaa.ui.screens.reports_admin.ReportsAdminScreen
import noor.serry.rawaa.ui.screens.settings_admin.SettingsAdminScreen
import noor.serry.rawaa.ui.screens.users_admin.AddUserScreen
import noor.serry.rawaa.ui.screens.users_admin.EditUserScreen
import noor.serry.rawaa.ui.screens.users_admin.UsersAdminScreen
import noor.serry.rawaa.ui.screens.users_admin.UsersAdminUiState
import noor.serry.rawaa.ui.screens.users_admin.ViewUserScreen

val universityAdminEntryProvider: (MainViewModel) -> (NavKey) -> NavEntry<NavKey> =
    { mainViewModel ->
        entryProvider {

            // ── Bottom-nav roots ─────────────────────────────────────────────

            entry<UniversityAdminRouteKeys.Dashboard> {
                // Re-use the existing reports screen as the dashboard landing,
                // or swap for a dedicated DashboardAdminScreen if you create one.
                ReportsAdminScreen()
            }

            entry<UniversityAdminRouteKeys.Users> {
                UsersAdminScreen()
            }

            entry<UniversityAdminRouteKeys.Courses> {
                CoursesAdminScreen()
            }

            entry<UniversityAdminRouteKeys.Departments> {
                DepartmentsAdminScreen()
            }

            entry<UniversityAdminRouteKeys.Settings> {
                SettingsAdminScreen()
            }

            // ── Users sub-screens ────────────────────────────────────────────
            // StudentDetail and EmployeeDetail use the existing teacher-facing
            // profile screens or a dedicated admin version – wire as needed.

             entry<UniversityAdminRouteKeys.StudentDetail> { key ->
                 ViewUserScreen(
                     userId = key.studentId,
                     userType = UsersAdminUiState.UserType.STUDENT
                     )
             }
             entry<UniversityAdminRouteKeys.EmployeeDetail> { key ->
                 ViewUserScreen(
                     userId = key.employeeId,
                     userType = UsersAdminUiState.UserType.ADMIN
                 )
             }

            entry<UniversityAdminRouteKeys.EditUser> { key ->
                EditUserScreen(
                    userId = key.userId,
                    userType = key.userType
                )
            }

             entry<UniversityAdminRouteKeys.AddUser> {
                 AddUserScreen()
             }

        }
    }
