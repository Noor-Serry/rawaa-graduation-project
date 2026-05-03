package noor.serry.rawaa.domain.repository

interface GoogleAuthDataSource {
    /**
     * Launches the Google sign-in bottom-sheet and returns the raw ID token,
     * or null if the user dismissed the sheet.
     *
     * Must be called from a coroutine bound to an Activity context.
     */
    suspend fun getIdToken(): String?
}
