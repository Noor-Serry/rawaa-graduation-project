package noor.serry.rawaa.data.repository

import android.content.Context
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import java.security.MessageDigest
import java.util.UUID

/**
 * Credential-Manager-backed implementation of [GoogleAuthDataSource].
 *
 * Lives in the **data** layer; the domain layer only sees the interface.
 *
 * The [context] lambda is called at sign-in time so callers can supply
 * a fresh Activity context without storing it as a field (avoids leaks).
 */
class GoogleAuthDataSourceImpl(
    private val context: Context,
)  {

     suspend fun getIdToken(): String? {
        val credentialManager = CredentialManager.create(context)

        return try {
            val option = GetSignInWithGoogleOption.Builder(WEB_CLIENT_ID)
                .setNonce(generateNonce())
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(option)
                .build()

            val response = credentialManager.getCredential(
                context = context,
                request = request,
            )

            extractIdToken(response.credential)

        } catch (e: GetCredentialCancellationException) {
            Log.d(TAG, "User cancelled Google Sign-In")
            null // caller treats null as cancellation
        }
        // Any other exception propagates up — the ViewModel / use-case catches it
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private fun extractIdToken(credential: Credential): String {
        require(
            credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) { "Unexpected credential type: ${credential.type}" }

        return GoogleIdTokenCredential.createFrom(credential.data).idToken
    }

    private fun generateNonce(): String {
        val raw = UUID.randomUUID().toString()
        return MessageDigest.getInstance("SHA-256")
            .digest(raw.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val TAG = "GoogleAuthDataSource"
        const val WEB_CLIENT_ID =
            "167073760840-8n2a2epthh6ogmu1ri7vbuud422rsshc.apps.googleusercontent.com"
    }
}
