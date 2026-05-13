package noor.serry.rawaa.ui.navigation.university_admin

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import noor.serry.rawaa.ui.screens.users_admin.UsersAdminUiState

@Serializable
sealed interface UniversityAdminRouteKeys : NavKey {

    // ── Bottom-nav roots ──────────────────────────────────────────────────────

    @Serializable data object Dashboard   : UniversityAdminRouteKeys

    @Serializable data object Users       : UniversityAdminRouteKeys   // students + employees

    @Serializable data object Courses     : UniversityAdminRouteKeys

    @Serializable data object Departments : UniversityAdminRouteKeys

    // ── Sub-screens (pushed on top of bottom-nav) ─────────────────────────────

    // Users
    @Serializable data class StudentDetail(val studentId: Int)   : UniversityAdminRouteKeys
    @Serializable data class EmployeeDetail(val employeeId: Int) : UniversityAdminRouteKeys
    @Serializable
    data class EditUser(val userId: Int,val userType : UsersAdminUiState.UserType) : UniversityAdminRouteKeys
    @Serializable
    data object AddUser : UniversityAdminRouteKeys


    // Settings / Profile
    @Serializable data object Settings : UniversityAdminRouteKeys
}
