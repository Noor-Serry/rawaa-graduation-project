package noor.serry.rawaa.ui.screens.departments_admin

interface DepartmentsAdminInteractionListener {
    // List
    fun onAddDepartmentClicked()
    fun onEditDepartmentClicked(department: DepartmentsAdminUiState.DepartmentItem)
    fun onDeleteDepartmentClicked(departmentId: Int)
    fun onDeleteConfirmed()
    fun onDeleteDismissed()

    // Create / Edit sheet
    fun onFormNameChanged(name: String)
    fun onFormSubmit()
    fun onFormDismissed()
}
