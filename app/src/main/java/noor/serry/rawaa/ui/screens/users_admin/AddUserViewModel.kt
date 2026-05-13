package noor.serry.rawaa.ui.screens.users_admin

import android.util.Log
import noor.serry.rawaa.data.dto.DoctorRegisterRequest
import noor.serry.rawaa.data.dto.StudentRegisterRequest
import noor.serry.rawaa.data.local.TokenDataStore
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

/**
 * ViewModel that owns the Add-User screen's state and business logic.
 *
 * Extends [BaseViewModel] with [AddUserUiState] / [AddUserEffect] and
 * implements [AddUserInteractionListener] so the composable can call
 * interactions directly on the ViewModel reference without extra lambdas.
 *
 * @param repository    Data source; injected via Koin / Hilt.
 * @param tokenStore    Provides the persisted auth token after login.
 * @param dispatchers   Thread dispatcher provider (IO / Main / Default).
 */
class AddUserViewModel(
    private val repository: UniversityRepository,
    private val tokenStore: TokenDataStore,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<AddUserUiState, AddUserEffect>(
    initialState       = AddUserUiState(),
    dispatcherProvider = dispatchers,
), AddUserInteractionListener {

    // ── Init ──────────────────────────────────────────────────────────────────

    init {
        loadInitialData()
    }

    /**
     * Fires two parallel API calls during screen initialisation:
     *
     *  1. GET /api/departments     → populates the department picker.
     *  2. GET /api/admin/dashboard → provides [AdminUniversityRefDto.slug]
     *     which is required by both POST /api/students and POST /api/employees.
     *
     * The slug is stored in [AddUserUiState.universitySlug] so [onSubmitClicked]
     * never has to make an extra network call at submit time.
     */
    private fun loadInitialData() {
        tryToExecute(
            action = {
                repository.getDepartments()
            },
            onSuccess = { deptsResponse  ->
                val items = deptsResponse.data
                    ?.map { it.toDepartmentFilterItem() }
                    ?: emptyList()

                val slug = ""

                updateState {
                    it.copy(
                        availableDepartments = items,
                        universitySlug       = slug,
                    )
                }
            },
            onError = { e ->
                updateState { it.copy(errorMessage = e.message ?: "فشل تحميل البيانات الأولية") }
            },
            dispatcher = dispatchers.IO,
        )
    }

    // ── Role ──────────────────────────────────────────────────────────────────

    override fun onRoleChanged(role: NewUserRole) {
        updateState { it.copy(selectedRole = role) }
    }

    // ── Common fields ─────────────────────────────────────────────────────────

    override fun onNameChanged(value: String) =
        updateState { it.copy(name = value, errorMessage = null) }

    override fun onEmailChanged(value: String) =
        updateState { it.copy(email = value, errorMessage = null) }

    override fun onPasswordChanged(value: String) =
        updateState { it.copy(password = value, errorMessage = null) }

    override fun onPhoneChanged(value: String) =
        updateState { it.copy(phone = value) }

    // ── Department picker ─────────────────────────────────────────────────────

    override fun onDepartmentPickerOpened() {
        updateState { it.copy(showDepartmentPicker = true) }
    }

    override fun onDepartmentSelected(department: UsersAdminUiState.DepartmentFilterItem) {
        updateState {
            it.copy(
                departmentId         = department.id,
                departmentName       = department.name,
                showDepartmentPicker = false,
                errorMessage         = null,
            )
        }
    }

    override fun onDepartmentPickerDismissed() {
        updateState { it.copy(showDepartmentPicker = false) }
    }

    // ── Student-only ──────────────────────────────────────────────────────────

    override fun onLevelChanged(value: String) =
        updateState { it.copy(level = value) }

    override fun onEnrollmentYearChanged(value: String) =
        updateState { it.copy(enrollmentYear = value) }

    // ── Employee-only ─────────────────────────────────────────────────────────

    override fun onRoleTitleChanged(value: String) =
        updateState { it.copy(roleTitle = value, errorMessage = null) }

    // ── Submit ────────────────────────────────────────────────────────────────

    override fun onSubmitClicked() {
        val current = state.value
        if (!current.isValid) return          // UI already shows inline errors

        updateState { it.copy(isSubmitting = true, errorMessage = null) }

        val form = current.toFormState()
        val slug = current.universitySlug     // resolved in init — guaranteed non-empty if dashboard loaded
        tryToExecute(
            action = {
                when (form.selectedRole) {
                            NewUserRole.STUDENT -> {repository.createStudent(
                        StudentRegisterRequest(
                            universitySlug = slug,
                            name           = form.name,
                            email          = form.email,
                            password       = form.password,
                            phone          = form.phone.ifBlank { null },
                            departmentId   = form.departmentId!!,
                            level          = form.level.toIntOrNull() ?: 1,
                            enrollmentYear = form.enrollmentYear.toIntOrNull() ?: 2025,
                        )
                    )
                            }
                    NewUserRole.DOCTOR,
                    NewUserRole.EMPLOYEE -> repository.createEmployee(
                        DoctorRegisterRequest(
                            universitySlug = slug,
                            name           = form.name,
                            email          = form.email,
                            password       = form.password,
                            role           = if (form.selectedRole == NewUserRole.DOCTOR) "doctor" else "employee",
                            phone          = form.phone.ifBlank { null },
                            roleTitle      = form.roleTitle,
                            departmentId   = form.departmentId,
                        )
                    )
                }
            },
            onSuccess = {
                updateState { it.copy(isSubmitting = false) }
                sendNewEffect(AddUserEffect.UserCreatedSuccessfully("تم إضافة المستخدم بنجاح"))
            },
            onError = { e ->
                updateState {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = e.message ?: "حدث خطأ أثناء الإضافة، حاول مجدداً",
                    )
                }
                sendNewEffect(AddUserEffect.ShowError(e.message ?: "حدث خطأ أثناء الإضافة، حاول مجدداً"))
            },
            dispatcher = dispatchers.IO,
        )
    }

    // ── Dismiss ───────────────────────────────────────────────────────────────

    override fun onDismissClicked() {
        sendNewEffect(AddUserEffect.Dismissed)
    }
}