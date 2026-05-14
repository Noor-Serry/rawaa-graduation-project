package noor.serry.rawaa.ui.screens.studentScreens.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import noor.serry.rawaa.data.local.TokenDataStore
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class MenuViewModel(
    private val repository: UniversityRepository,
    private val tokenDataStore: TokenDataStore,
    val dispatchers: DispatcherProvider,
) : BaseViewModel<MenuUiState,MenuEffect>(MenuUiState(),dispatchers), MenuInteractionListener {


    init { loadUser() }

    private fun loadUser() {
        viewModelScope.launch(dispatchers.IO) {
            try {
                // Run both calls in parallel
                val userDeferred          = async { repository.getMe().data }
                val notificationsDeferred = async {
                    try { repository.getNotifications(perPage = 1).unreadCount }
                    catch (_: Exception) { 0 }
                }

                val user          = userDeferred.await() ?: return@launch
                val unreadCount   = notificationsDeferred.await()

                val roleLabel = when (user.role) {
                    "student"  -> "طالب"
                    "doctor"   -> "دكتور"
                    "employee" -> "موظف"
                    "admin"    -> "مشرف"
                    else       -> user.role
                }
                val departmentName = user.profile?.departmentName ?: ""

                updateState {
                    it.copy(
                        userName                 = user.name,
                        userRole                 = if (departmentName.isNotBlank()) "$roleLabel · $departmentName" else roleLabel,
                        userInitial              = user.name.trim().firstOrNull()?.toString() ?: "",
                        unreadNotificationCount  = unreadCount,
                    )
                }
            } catch (_: Exception) { /* keep defaults */ }
        }
    }

    /** Call this after the user reads notifications to refresh the badge. */
    fun refreshNotificationCount() {
        viewModelScope.launch(dispatchers.IO) {
            try {
                val count = repository.getNotifications(perPage = 1).unreadCount
                updateState{ it.copy(unreadNotificationCount = count) }
            } catch (_: Exception) { /* keep current count */ }
        }
    }

    override fun onLogoutClick() {
        viewModelScope.launch(dispatchers.IO) {
            updateState { it.copy(isLoggingOut = true) }
            try {
                repository.logout()
            } catch (_: Exception) { /* ignored — clear local regardless */ }
            finally {
                tokenDataStore.clearAll()
                updateState {
                    it.copy(
                        loggedOut = true,
                        isLoggingOut = false
                    )
                }
                sendNewNavigationEffect(MenuEffect.NavigateToLogin)
            }
        }

    }

    override fun onMenuToggle()  = updateState { it.copy(isOpen = !it.isOpen) }
    override fun onMenuDismiss() = updateState {   it.copy(isOpen = false) }

    override fun onSettingsClick()       = Unit  // TODO: navigate to settings
    override fun onHelpAndSupportClick() = Unit  // TODO: navigate to help
    override fun onPrivacyPolicyClick() {
        updateState { it.copy(isOpen = false) }
        sendNewNavigationEffect(MenuEffect.NavigateToPrivacyPolicy)
    }
}
