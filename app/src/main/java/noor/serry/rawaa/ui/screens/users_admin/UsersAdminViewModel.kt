package noor.serry.rawaa.ui.screens.users_admin

import noor.serry.rawaa.data.dto.DepartmentDto
import noor.serry.rawaa.data.dto.EmployeeDto
import noor.serry.rawaa.data.dto.StudentDto
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class UsersAdminViewModel(
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<UsersAdminUiState, UsersAdminEffect>(
    initialState = UsersAdminUiState(isLoading = true),
    dispatcherProvider = dispatchers,
), UsersAdminInteractionListener {

    init { load() }

    // ── Load ──────────────────────────────────────────────────────────────────

    fun load() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            action = {
                val studentsResp    = repository.getStudents(perPage = 100)
                val employeesResp   = repository.getEmployees(perPage = 100)
                val departmentsResp = repository.getDepartments()
                Triple(studentsResp, employeesResp, departmentsResp)
            },
            onSuccess = { (studentsResp, employeesResp, depsResp) ->
                val students  = studentsResp.data.map { it.toUserItem() }
                val employees = employeesResp.data.map { it.toUserItem() }
                updateState { current ->
                    current.copy(
                        isLoading   = false,
                        users       = students + employees,
                        departments = depsResp.data?.map { it.toDepartmentFilterItem() } ?: emptyList(),
                    )
                }
            },
            onError = { e ->
                updateState { it.copy(isLoading = false, errorMessage = e.message) }
            },
            dispatcher = dispatchers.IO,
        )
    }

    // ── Filters ───────────────────────────────────────────────────────────────

    override fun onRoleFilterSelected(filter: UsersAdminUiState.RoleFilter) {
        updateState { it.copy(selectedRole = filter) }
    }

    override fun onSearchQueryChanged(query: String) {
        updateState { it.copy(searchQuery = query) }
    }

    override fun onDepartmentFilterSelected(departmentId: Int?) {
        updateState { it.copy(selectedDepartmentId = departmentId) }
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    override fun onViewProfileClicked(userId: Int, userType: UsersAdminUiState.UserType) {
        val effect = when (userType) {
            UsersAdminUiState.UserType.STUDENT -> UsersAdminEffect.NavigateToStudentDetail(userId)
            UsersAdminUiState.UserType.DOCTOR  -> UsersAdminEffect.NavigateToEmployeeDetail(userId)
            UsersAdminUiState.UserType.ADMIN   -> UsersAdminEffect.NavigateToEmployeeDetail(userId)
        }
        sendNewNavigationEffect(effect)
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    override fun onDeleteClicked(userId: Int, userType: UsersAdminUiState.UserType) {
        updateState { it.copy(pendingDeleteId = userId, pendingDeleteType = userType) }
    }

    override fun onDeleteConfirmed() {
        val id   = state.value.pendingDeleteId   ?: return
        val type = state.value.pendingDeleteType ?: return
        updateState { it.copy(pendingDeleteId = null, pendingDeleteType = null) }

        tryToExecute(
            action = {
                when (type) {
                    UsersAdminUiState.UserType.STUDENT -> repository.deleteStudent(id)
                    UsersAdminUiState.UserType.DOCTOR  -> repository.deleteEmployee(id)
                    UsersAdminUiState.UserType.ADMIN   -> repository.deleteEmployee(id)
                }
            },
            onSuccess = {
                updateState { s -> s.copy(users = s.users.filter { it.id != id }) }
                sendNewEffect(UsersAdminEffect.ShowDeleteSuccess("تم حذف المستخدم بنجاح"))
            },
            onError = { e ->
                sendNewEffect(UsersAdminEffect.ShowError(e.message ?: "حدث خطأ أثناء الحذف"))
            },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onDeleteDismissed() {
        updateState { it.copy(pendingDeleteId = null, pendingDeleteType = null) }
    }

    // ── Add-user navigation ───────────────────────────────────────────────────

    override fun onAddUserClicked() {
        sendNewNavigationEffect(UsersAdminEffect.NavigateToAddUser)
    }

    override fun onAddUserDismissed() {
        // No-op: sheet is now a separate screen; kept in interface for compatibility
    }
}

// ── Mappers ───────────────────────────────────────────────────────────────────

private fun StudentDto.toUserItem() = UsersAdminUiState.UserItem(
    id             = id,
    userType       = UsersAdminUiState.UserType.STUDENT,
    role           = "student",
    name           = name,
    email          = email,
    phone          = phone,
    departmentId   = departmentId,
    departmentName = departmentName,
    isActive       = isActive == 1,
    createdAt      = createdAt,
    level          = level,
    roleTitle      = null,
)

private fun EmployeeDto.toUserItem(): UsersAdminUiState.UserItem {
    val type = when (role) {
        "doctor"   -> UsersAdminUiState.UserType.DOCTOR
        "employee" -> UsersAdminUiState.UserType.ADMIN
        else       -> UsersAdminUiState.UserType.ADMIN
    }
    return UsersAdminUiState.UserItem(
        id             = id,
        userType       = type,
        role           = role,
        name           = name,
        email          = email,
        phone          = phone,
        departmentId   = departmentId,
        departmentName = departmentName,
        isActive       = isActive == 1,
        createdAt      = null,      // EmployeeDto has no createdAt in list response
        level          = null,
        roleTitle      = roleTitle,
    )
}

private fun DepartmentDto.toDepartmentFilterItem() =
    UsersAdminUiState.DepartmentFilterItem(id = id, name = name)
