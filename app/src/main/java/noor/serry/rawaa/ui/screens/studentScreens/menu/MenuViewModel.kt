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
import noor.serry.rawaa.ui.base.DispatcherProvider

class MenuViewModel(
    private val repository: UniversityRepository,
    private val tokenDataStore: TokenDataStore,
    val dispatchers: DispatcherProvider,
) : ViewModel(), MenuInteractionListener {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

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

                _uiState.update {
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
                _uiState.update { it.copy(unreadNotificationCount = count) }
            } catch (_: Exception) { /* keep current count */ }
        }
    }

    override fun onLogoutClick() {
        viewModelScope.launch(dispatchers.IO) {
            _uiState.update { it.copy(isLoggingOut = true) }
            try {
                repository.logout()
            } catch (_: Exception) { /* ignored — clear local regardless */ }
            tokenDataStore.clearAll()
            _uiState.update { it.copy(loggedOut = true, isLoggingOut = false) }
        }
    }

    override fun onMenuToggle()  = _uiState.update { it.copy(isOpen = !it.isOpen) }
    override fun onMenuDismiss() = _uiState.update { it.copy(isOpen = false) }

    override fun onSettingsClick()       = Unit  // TODO: navigate to settings
    override fun onHelpAndSupportClick() = Unit  // TODO: navigate to help
    override fun onPrivacyPolicyClick()  = Unit  // TODO: navigate to privacy policy
}
