package noor.serry.rawaa.ui.screens.users_admin

sealed interface UsersAdminEffect {
    /** عرض الملف on a student row */
    data class NavigateToStudentDetail(val studentId: Int) : UsersAdminEffect

    /** عرض الملف on a doctor/employee row */
    data class NavigateToEmployeeDetail(val employeeId: Int) : UsersAdminEffect

    /** Navigate to the Add New User screen */
    data object NavigateToAddUser : UsersAdminEffect

    /** Deletion succeeded – used to show a snackbar / toast */
    data class ShowDeleteSuccess(val message: String) : UsersAdminEffect

    /** Any error that should be surfaced as a one-time toast */
    data class ShowError(val message: String) : UsersAdminEffect
}
