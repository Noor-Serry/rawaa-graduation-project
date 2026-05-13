package noor.serry.rawaa.ui.screens.departments_admin

sealed interface DepartmentsAdminEffect {
    data class ShowSuccess(val message: String) : DepartmentsAdminEffect
    data class ShowError(val message: String)   : DepartmentsAdminEffect
}
