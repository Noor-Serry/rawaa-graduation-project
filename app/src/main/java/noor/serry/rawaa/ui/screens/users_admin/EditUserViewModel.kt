package noor.serry.rawaa.ui.screens.users_admin

import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

/**
 * ViewModel for the Edit-User screen.
 *
 * Loads the user's current data on init, then tracks form mutations and
 * submits PATCH requests on save.
 *
 * @param userId      The id of the user being edited.
 * @param userType    Determines which endpoint is called.
 * @param repository  Injected data source.
 * @param dispatchers Thread dispatcher provider.
 */
class EditUserViewModel(
    private val userId: Int,
    private val userType: UsersAdminUiState.UserType,
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<EditUserUiState, EditUserEffect>(
    initialState       = EditUserUiState(isLoading = true, userId = userId, userType = userType),
    dispatcherProvider = dispatchers,
), EditUserInteractionListener {

    init { load() }

    // ── Load current data ─────────────────────────────────────────────────────

    fun load() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            action = {
                val departments = repository.getDepartments().data
                    ?.map { it.toDepartmentFilterItem() }
                    ?: emptyList()

                val partial: EditUserUiState = when (userType) {
                    UsersAdminUiState.UserType.STUDENT -> {
                        val dto = repository.getStudent(userId).data!!
                        EditUserUiState(
                            userId             = userId,
                            userType           = UsersAdminUiState.UserType.STUDENT,
                            name               = dto.name,
                            email              = dto.email,
                            phone              = dto.phone ?: "",
                            isActive           = dto.isActive == 1,
                            departmentId       = dto.departmentId,
                            departmentName     = dto.departmentName ?: "",
                            level              = dto.level?.toString() ?: "",
                            enrollmentYear     = dto.enrollmentYear?.toString() ?: "",
                        )
                    }
                    UsersAdminUiState.UserType.DOCTOR,
                    UsersAdminUiState.UserType.ADMIN -> {
                        val dto = repository.getEmployee(userId).data!!
                        EditUserUiState(
                            userId         = userId,
                            userType       = userType,
                            name           = dto.name,
                            email          = dto.email,
                            phone          = dto.phone ?: "",
                            isActive       = dto.isActive == 1,
                            departmentId   = dto.departmentId,
                            departmentName = dto.departmentName ?: "",
                            roleTitle      = dto.roleTitle ?: "",
                        )
                    }
                }
                partial.copy(availableDepartments = departments, isLoading = false)
            },
            onSuccess = { loaded ->
                updateState { loaded }
            },
            onError = { e ->
                updateState { it.copy(isLoading = false, errorMessage = e.message) }
                sendNewEffect(EditUserEffect.ShowError(e.message ?: "فشل تحميل البيانات"))
            },
            dispatcher = dispatchers.IO,
        )
    }

    // ── Common field changes ──────────────────────────────────────────────────

    override fun onNameChanged(value: String) =
        updateState { it.copy(name = value, errorMessage = null) }

    override fun onEmailChanged(value: String) =
        updateState { it.copy(email = value, errorMessage = null) }

    override fun onPhoneChanged(value: String) =
        updateState { it.copy(phone = value) }

    override fun onActiveToggled(value: Boolean) =
        updateState { it.copy(isActive = value) }

    // ── Department picker ─────────────────────────────────────────────────────

    override fun onDepartmentPickerOpened() =
        updateState { it.copy(showDepartmentPicker = true) }

    override fun onDepartmentSelected(department: UsersAdminUiState.DepartmentFilterItem) =
        updateState {
            it.copy(
                departmentId         = department.id,
                departmentName       = department.name,
                showDepartmentPicker = false,
                errorMessage         = null,
            )
        }

    override fun onDepartmentPickerDismissed() =
        updateState { it.copy(showDepartmentPicker = false) }

    // ── Student-only ──────────────────────────────────────────────────────────

    override fun onLevelChanged(value: String) =
        updateState { it.copy(level = value) }

    override fun onEnrollmentYearChanged(value: String) =
        updateState { it.copy(enrollmentYear = value) }

    // ── Employee / Doctor-only ────────────────────────────────────────────────

    override fun onRoleTitleChanged(value: String) =
        updateState { it.copy(roleTitle = value, errorMessage = null) }

    // ── Save ──────────────────────────────────────────────────────────────────

    override fun onSaveClicked() {
        val current = state.value
        if (!current.isValid) return          // inline errors already visible

        updateState { it.copy(isSubmitting = true, errorMessage = null) }

        tryToExecute(
            action = {
                when (current.userType) {
                    UsersAdminUiState.UserType.STUDENT -> repository.updateStudent(
                        id           = current.userId,
                        name         = current.name,
                        email        = current.email,
                        phone        = current.phone.ifBlank { null },
                        departmentId = current.departmentId,
                        level        = current.level.toIntOrNull(),
                        isActive     = current.isActive,
                    )
                    UsersAdminUiState.UserType.DOCTOR,
                    UsersAdminUiState.UserType.ADMIN -> repository.updateEmployee(
                        id           = current.userId,
                        name         = current.name,
                        email        = current.email,
                        phone        = current.phone.ifBlank { null },
                        departmentId = current.departmentId,
                        roleTitle    = current.roleTitle.ifBlank { null },
                        isActive     = current.isActive,
                    )
                }
            },
            onSuccess = {
                updateState { it.copy(isSubmitting = false) }
                sendNewEffect(EditUserEffect.UpdatedSuccessfully("تم تحديث بيانات المستخدم بنجاح"))
            },
            onError = { e ->
                updateState { it.copy(isSubmitting = false, errorMessage = e.message) }
                sendNewEffect(EditUserEffect.ShowError(e.message ?: "حدث خطأ أثناء الحفظ، حاول مجدداً"))
            },
            dispatcher = dispatchers.IO,
        )
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    override fun onBackClicked() {
        sendNewNavigationEffect(EditUserEffect.NavigateBack)
    }

    override fun onRetryClicked() = load()
}