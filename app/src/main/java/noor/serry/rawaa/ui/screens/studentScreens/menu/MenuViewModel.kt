package noor.serry.rawaa.ui.screens.studentScreens.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import noor.serry.rawaa.data.repository.AuthRepositoryImpl
import noor.serry.rawaa.ui.base.DispatcherProvider

class MenuViewModel(
    private val authRepository: AuthRepositoryImpl,
    val dispatchers: DispatcherProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    fun logout() {
        viewModelScope.launch(dispatchers.IO) {
            _uiState.update { it.copy(isLoggingOut = true) }
            try {
                authRepository.logout()
                _uiState.update { it.copy(loggedOut = true, isLoggingOut = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message, isLoggingOut = false) }
            }
        }
    }

    fun onMenuToggle() {
        _uiState.update { it.copy(isOpen = !it.isOpen) }
    }
}