package noor.serry.rawaa.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import noor.serry.rawaa.data.local.TokenDataStore

/**
 * Single source of truth for app-level settings that must survive process death:
 *  - Whether the user has already seen the onboarding flow
 *  - Whether a valid auth token exists (i.e. the user is logged in)
 *
 * All persistence is delegated to the shared [DataStore<Preferences>] that is
 * also used by [TokenDataStore], so there is exactly one file on disk.
 */
class AppSettingsRepository(
    private val dataStore: DataStore<Preferences>,
    private val tokenDataStore: TokenDataStore,
) {

    // ── Onboarding ────────────────────────────────────────────────────────────

    /**
     * Emits `true` once the user has completed or skipped onboarding.
     * Backed by DataStore so the value persists across app restarts.
     */
    val isOnboardingSeen: Flow<Boolean> =
        dataStore.data.map { prefs -> prefs[KEY_ONBOARDING_SEEN] ?: false }

    /** Call this when the user taps Skip or reaches the last onboarding page. */
    suspend fun markOnboardingAsSeen() {
        dataStore.edit { it[KEY_ONBOARDING_SEEN] = true }
    }

    // ── Auth state ────────────────────────────────────────────────────────────

    /**
     * Returns `true` if an auth token is currently stored.
     * Delegates to [TokenDataStore] so there is a single token source of truth.
     */
    suspend fun isLoggedIn(): Boolean = tokenDataStore.getToken() != null

    /**
     * Returns the persisted user role, or `null` when not logged in.
     */
    suspend fun getSavedRole(): String? = tokenDataStore.getRole()

    // ── Convenience reset (e.g. for testing / logout) ─────────────────────────

    /** Clears only the onboarding flag – useful for debug builds or QA. */
    suspend fun resetOnboarding() {
        dataStore.edit { it.remove(KEY_ONBOARDING_SEEN) }
    }

    companion object {
        private val KEY_ONBOARDING_SEEN = booleanPreferencesKey("onboarding_seen")
    }
}