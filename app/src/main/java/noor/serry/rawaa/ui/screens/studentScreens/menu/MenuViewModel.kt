package noor.serry.rawaa.ui.screens.studentScreens.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                val user = repository.getMe().data ?: return@launch
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
                        userName    = user.name,
                        userRole    = if (departmentName.isNotBlank()) "$roleLabel · $departmentName" else roleLabel,
                        userInitial = user.name.trim().firstOrNull()?.toString() ?: "",
                    )
                }
            } catch (_: Exception) { /* keep defaults */ }
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

    override fun onMenuToggle()   = _uiState.update { it.copy(isOpen = !it.isOpen) }
    override fun onMenuDismiss()  = _uiState.update { it.copy(isOpen = false) }

    override fun onSettingsClick()       = Unit  // TODO: navigate to settings
    override fun onHelpAndSupportClick() = Unit  // TODO: navigate to help
    override fun onPrivacyPolicyClick()  = Unit  // TODO: navigate to privacy policy
}
