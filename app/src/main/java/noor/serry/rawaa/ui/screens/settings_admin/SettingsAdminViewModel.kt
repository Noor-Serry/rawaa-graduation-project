package noor.serry.rawaa.ui.screens.settings_admin

import noor.serry.rawaa.data.dto.UserDto
import noor.serry.rawaa.data.local.TokenDataStore
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class SettingsAdminViewModel(
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
    private val tokenDataStore : TokenDataStore
) : BaseViewModel<SettingsAdminUiState, SettingsAdminEffect>(
    initialState = SettingsAdminUiState(isLoading = true),
    dispatcherProvider = dispatchers,
), SettingsAdminInteractionListener {

    init { load() }

    fun load() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            action = {
                val meResp        = repository.getMe()
                val dashboardResp = repository.getAdminDashboard()
                meResp to dashboardResp
            },
            onSuccess = { (meResp, dashboardResp) ->
                val user      = meResp.data
                val dashboard = dashboardResp.data
                updateState { current ->
                    current.copy(
                        isLoading      = false,
                        adminName      = user?.name ?: "",
                        adminEmail     = user?.email ?: "",
                        roleTitle      = user?.profile?.roleTitle ?: "",
                        phone          = user?.profile?.phone,
                        universityName = dashboard?.university?.name ?: "",
                        universityPlan = dashboard?.university?.plan ?: "",
                    )
                }
            },
            onError = { e ->
                updateState { it.copy(isLoading = false, errorMessage = e.message) }
            },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onLogoutClick() {
        tryToExecute(
            action = { repository.logout()
                tokenDataStore.clearToken()
                     },
            onSuccess = {

                sendNewNavigationEffect(SettingsAdminEffect.LoggedOut) },
            onError   = { sendNewNavigationEffect(SettingsAdminEffect.LoggedOut) },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onChangePasswordConfirm(current: String, new: String, confirm: String) {
        if (new != confirm) {
            updateState { it.copy(changePasswordError = "كلمة المرور الجديدة غير متطابقة") }
            return
        }
        updateState { it.copy(isChangingPassword = true, changePasswordError = null) }
        tryToExecute(
            action = { repository.changePassword(current, new, confirm) },
            onSuccess = {
                updateState {
                    it.copy(
                        isChangingPassword   = false,
                        changePasswordSuccess = true,
                        changePasswordError  = null,
                    )
                }
            },
            onError = { e ->
                updateState {
                    it.copy(isChangingPassword = false, changePasswordError = e.message)
                }
            },
            dispatcher = dispatchers.IO,
        )
    }

    override fun onDismissChangePassword() {
        updateState {
            it.copy(
                isChangingPassword   = false,
                changePasswordSuccess = false,
                changePasswordError  = null,
            )
        }
    }
}
