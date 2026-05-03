package noor.serry.rawaa.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "rawaa_prefs")

/**
 * Persists auth token, university slug, and user role across app sessions.
 *
 * The same [DataStore] instance injected here is the one bound in [dataModule],
 * so there is a single source of truth across the whole app.
 */
class TokenDataStore(private val dataStore: DataStore<Preferences>) {

    // ── Token ─────────────────────────────────────────────────────────────────

    suspend fun getToken(): String? =
        dataStore.data.map { it[KEY_TOKEN] }.firstOrNull()

    suspend fun saveToken(token: String) {
        dataStore.edit { it[KEY_TOKEN] = token }
    }

    // ── University slug ───────────────────────────────────────────────────────

    suspend fun saveUniversitySlug(slug: String) {
        dataStore.edit { it[KEY_SLUG] = slug }
    }

    suspend fun getUniversitySlug(): String? =
        dataStore.data.map { it[KEY_SLUG] }.firstOrNull()

    // ── Role (student | doctor | employee | admin) ────────────────────────────

    suspend fun saveRole(role: String) {
        dataStore.edit { it[KEY_ROLE] = role }
    }

    suspend fun getRole(): String? =
        dataStore.data.map { it[KEY_ROLE] }.firstOrNull()

    // ── Clear ─────────────────────────────────────────────────────────────────

    /** Remove only the token (keeps slug & role for re-login convenience). */
    suspend fun clearToken() {
        dataStore.edit { it.remove(KEY_TOKEN) }
    }

    /** Remove all stored preferences (full sign-out). */
    suspend fun clearAll() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_TOKEN)
            prefs.remove(KEY_SLUG)
            prefs.remove(KEY_ROLE)
        }
    }

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_SLUG  = stringPreferencesKey("university_slug")
        private val KEY_ROLE  = stringPreferencesKey("user_role")
    }
}
