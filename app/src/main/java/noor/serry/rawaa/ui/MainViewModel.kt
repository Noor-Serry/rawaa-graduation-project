package noor.serry.rawaa.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import noor.serry.rawaa.data.local.TokenDataStore

/**
 * App-level ViewModel that determines which root destination to show
 * immediately after the splash screen dismisses.
 *
 * Decision tree (evaluated once, on startup):
 *
 *   onboardingSeen == false  →  [MainUiState.ShowOnboarding]
 *   onboardingSeen == true
 *     && token != null       →  [MainUiState.ShowMain(role)]
 *     && token == null       →  [MainUiState.ShowAuth]
 */
class MainViewModel(
    private val tokenDataStore: TokenDataStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        resolveStartDestination()
    }

    private fun resolveStartDestination() {
        viewModelScope.launch {
            val onboardingSeen = tokenDataStore.isOnboardingSeen()
            val token = tokenDataStore.getToken()

            _uiState.value = when {
                !onboardingSeen -> MainUiState.ShowOnboarding
                token != null -> {
                    val role = tokenDataStore.getRole() ?: "student"
                    MainUiState.ShowMain(role)
                }
                else -> MainUiState.ShowAuth
            }
        }
    }

    fun onOnboardingCompleted() {
        viewModelScope.launch {
            tokenDataStore.markOnboardingSeen()
            _uiState.value = MainUiState.ShowAuth
        }
    }
}
