package noor.serry.rawaa.ui.screens.users_admin

import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

/**
 * ViewModel for the read-only user-profile screen.
 *
 * @param userId        The id of the user to display.
 * @param userType      Determines which endpoint is called.
 * @param repository    Data source; injected via Koin / Hilt.
 * @param dispatchers   Thread dispatcher provider.
 */
class ViewUserViewModel(
    private val userId: Int,
    private val userType: UsersAdminUiState.UserType,
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<ViewUserUiState, ViewUserEffect>(
    initialState       = ViewUserUiState(isLoading = true),
    dispatcherProvider = dispatchers,
), ViewUserInteractionListener {

    init { load() }

    // ── Load ──────────────────────────────────────────────────────────────────

    fun load() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            action = {
                when (userType) {
                    UsersAdminUiState.UserType.STUDENT -> {
                        val resp = repository.getStudent(userId)
                        resp.data!!.toViewUiState()
                    }
                    UsersAdminUiState.UserType.DOCTOR,
                    UsersAdminUiState.UserType.ADMIN -> {
                        val resp = repository.getEmployee(userId)
                        resp.data!!.toViewUiState()
                    }
                }
            },
            onSuccess = { partial ->
                updateState { partial.copy(isLoading = false) }
            },
            onError = { e ->
                updateState { it.copy(isLoading = false, errorMessage = e.message) }
                sendNewEffect(ViewUserEffect.ShowError(e.message ?: "حدث خطأ أثناء تحميل البيانات"))
            },
            dispatcher = dispatchers.IO,
        )
    }

    // ── Interactions ──────────────────────────────────────────────────────────

    override fun onBackClicked() {
        sendNewNavigationEffect(ViewUserEffect.NavigateBack)
    }

    override fun onEditClicked() {
        sendNewNavigationEffect(ViewUserEffect.NavigateToEdit(userId, userType))
    }

    override fun onRetryClicked() = load()
}

// ── Mappers ───────────────────────────────────────────────────────────────────

private fun noor.serry.rawaa.data.dto.StudentDto.toViewUiState() = ViewUserUiState(
    userId         = id,
    userType       = UsersAdminUiState.UserType.STUDENT,
    name           = name,
    email          = email,
    phone          = phone ?: "",
    isActive       = isActive == 1,
    departmentName = departmentName ?: "",
    createdAt      = createdAt ?: "",
    level          = level,
    enrollmentYear = enrollmentYear,
    gpa            = gpa?.toDoubleOrNull(),
    role           = "student",
)

private fun noor.serry.rawaa.data.dto.EmployeeDto.toViewUiState() = ViewUserUiState(
    userId         = id,
    userType       = if (role == "doctor") UsersAdminUiState.UserType.DOCTOR
    else UsersAdminUiState.UserType.ADMIN,
    name           = name,
    email          = email,
    phone          = phone ?: "",
    isActive       = isActive == 1,
    departmentName = departmentName ?: "",
    createdAt      = "",
    roleTitle      = roleTitle,
    role           = role,
)