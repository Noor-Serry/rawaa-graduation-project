package noor.serry.rawaa.ui.screens.departments_admin

data class DepartmentsAdminUiState(
    val departments: List<DepartmentItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // Create / Edit sheet
    val showFormSheet: Boolean = false,
    val editingDepartment: DepartmentItem? = null,   // null = create, non-null = edit
    val formName: String = "",
    val formError: String? = null,
    val isSaving: Boolean = false,
    // Delete confirmation
    val pendingDeleteId: Int? = null,
) {
    data class DepartmentItem(
        val id: Int,
        val name: String,
        val coursesCount: Int? = null,
        val studentsCount: Int? = null,
    )

    /** True when the sheet is open in "edit" mode */
    val isEditing: Boolean get() = editingDepartment != null
}
