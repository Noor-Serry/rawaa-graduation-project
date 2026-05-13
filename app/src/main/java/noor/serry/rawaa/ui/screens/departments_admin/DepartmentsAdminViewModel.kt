package noor.serry.rawaa.ui.screens.departments_admin

import noor.serry.rawaa.data.dto.DepartmentDto
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class DepartmentsAdminViewModel(
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<DepartmentsAdminUiState, DepartmentsAdminEffect>(
    initialState       = DepartmentsAdminUiState(isLoading = true),
    dispatcherProvider = dispatchers,
), DepartmentsAdminInteractionListener {

    init { load() }

    fun load() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            action    = { repository.getDepartments() },
            onSuccess = { resp ->
                updateState { current ->
                    current.copy(
                        isLoading   = false,
                        departments = resp.data?.map { it.toDepartmentItem() } ?: emptyList(),
                    )
                }
            },
            onError   = { e ->
                updateState { it.copy(isLoading = false, errorMessage = e.message) }
            },
            dispatcher = dispatchers.IO,
        )
    }

    // ── List interactions ─────────────────────────────────────────────────────

    override fun onAddDepartmentClicked() {
        updateState { it.copy(showFormSheet = true, editingDepartment = null, formName = "", formError = null) }
    }

    override fun onEditDepartmentClicked(department: DepartmentsAdminUiState.DepartmentItem) {
        updateState {
            it.copy(
                showFormSheet      = true,
                editingDepartment  = department,
                formName           = department.name,
                formError          = null,
            )
        }
    }

    override fun onDeleteDepartmentClicked(departmentId: Int) {
        updateState { it.copy(pendingDeleteId = departmentId) }
    }

    override fun onDeleteConfirmed() {
        val id = state.value.pendingDeleteId ?: return
        updateState { it.copy(pendingDeleteId = null) }
        tryToExecute(
            action    = { repository.deleteDepartment(id) },
            onSuccess = {
                updateState { s ->
                    s.copy(departments = s.departments.filter { it.id != id })
                }
                sendNewEffect(DepartmentsAdminEffect.ShowSuccess("تم حذف القسم بنجاح"))
            },
            onError   = { e ->
                sendNewEffect(DepartmentsAdminEffect.ShowError(e.message ?: "فشل الحذف"))
            },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onDeleteDismissed() {
        updateState { it.copy(pendingDeleteId = null) }
    }

    // ── Form interactions ─────────────────────────────────────────────────────

    override fun onFormNameChanged(name: String) {
        updateState { it.copy(formName = name, formError = null) }
    }

    override fun onFormSubmit() {
        val name = state.value.formName.trim()
        if (name.isBlank()) {
            updateState { it.copy(formError = "اسم القسم مطلوب") }
            return
        }

        val editing = state.value.editingDepartment
        updateState { it.copy(isSaving = true, formError = null) }

        if (editing == null) {
            // Create
            tryToExecute(
                action    = { repository.createDepartment(name) },
                onSuccess = { resp ->
                    val newItem = resp.data!!.toDepartmentItem()
                    updateState { s ->
                        s.copy(
                            isSaving      = false,
                            showFormSheet = false,
                            departments   = s.departments + newItem,
                        )
                    }
                    sendNewEffect(DepartmentsAdminEffect.ShowSuccess("تم إضافة القسم بنجاح"))
                },
                onError   = { e ->
                    updateState { it.copy(isSaving = false, formError = e.message) }
                },
                dispatcher = dispatchers.IO,
            )
        } else {
            // Update
            tryToExecute(
                action    = { repository.updateDepartment(editing.id, name) },
                onSuccess = { resp ->
                    val updated = resp.data!!.toDepartmentItem()
                    updateState { s ->
                        s.copy(
                            isSaving      = false,
                            showFormSheet = false,
                            departments   = s.departments.map { if (it.id == updated.id) updated else it },
                        )
                    }
                    sendNewEffect(DepartmentsAdminEffect.ShowSuccess("تم تحديث القسم بنجاح"))
                },
                onError   = { e ->
                    updateState { it.copy(isSaving = false, formError = e.message) }
                },
                dispatcher = dispatchers.IO,
            )
        }
    }

    override fun onFormDismissed() {
        updateState { it.copy(showFormSheet = false, formError = null) }
    }
}

// Mapper
fun DepartmentDto.toDepartmentItem() = DepartmentsAdminUiState.DepartmentItem(
    id   = id,
    name = name,
)
