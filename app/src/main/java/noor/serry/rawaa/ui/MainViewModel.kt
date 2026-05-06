package noor.serry.rawaa.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import noor.serry.rawaa.data.local.TokenDataStore
import noor.serry.rawaa.data.repository.AppSettingsRepository

/**
 * App-level ViewModel that determines which root destination to show
 * immediately after the splash screen dismisses.
 *
 * Decision tree (evaluated once, on startup):
 *
 *   isOnboardingSeen == false  →  [MainUiState.ShowOnboarding]
 *   isOnboardingSeen == true
 *     && isLoggedIn == true    →  [MainUiState.ShowMain(role)]
 *     && isLoggedIn == false   →  [MainUiState.ShowAuth]
 *
 * Injected via Koin as a *single* instance so [MainActivity] and the root
 * composable share the exact same state object.
 */
class MainViewModel(
    private val appSettings: AppSettingsRepository,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        resolveStartDestination()
    }

    // ── Startup resolution ────────────────────────────────────────────────────

    private fun resolveStartDestination() {
        viewModelScope.launch {
            val onboardingSeen = appSettings.isOnboardingSeen.first()

            _uiState.value = when {
                !onboardingSeen -> MainUiState.ShowOnboarding

                appSettings.isLoggedIn() -> {
                    Log.e("MainViewModel.kt", ""+tokenDataStore.getToken())
                    val role = appSettings.getSavedRole() ?: "student"
                    MainUiState.ShowMain(role)
                }

                else -> MainUiState.ShowAuth
            }
        }
    }

    // ── Called by OnboardingViewModel when onboarding finishes ────────────────

    /**
     * Persists the "onboarding seen" flag and then re-evaluates the start
     * destination so the root composable transitions to [MainUiState.ShowAuth].
     *
     * This is the ONLY place [AppSettingsRepository.markOnboardingAsSeen] is
     * called so the flag is set exactly once.
     */
    fun onOnboardingCompleted() {
        viewModelScope.launch {
            appSettings.markOnboardingAsSeen()
            // After marking, the user is definitely not logged in yet.
            _uiState.value = MainUiState.ShowAuth
        }
    }
}