package noor.serry.rawaa.data.repository

import noor.serry.rawaa.data.dto.DoctorRegisterRequest
import noor.serry.rawaa.data.dto.GoogleLoginRequest
import noor.serry.rawaa.data.dto.LoginRequest
import noor.serry.rawaa.data.dto.StudentRegisterRequest
import noor.serry.rawaa.data.local.TokenDataStore
import noor.serry.rawaa.data.remote.ApiClient

class AuthRepositoryImpl(
    private val api: ApiClient,
    private val tokenStore: TokenDataStore,
) {

    // ─────────────────────────────────────────────────────────────
    //  Login
    // ─────────────────────────────────────────────────────────────

    suspend fun loginWithEmail(
        email: String,
        password: String,
        universitySlug: String,
    ): String {
        val response = api.login(LoginRequest(email, password, universitySlug))
        if (!response.success) throw Exception("Login failed: ${response.message}")
        val authData = response.data ?: throw Exception("Login returned no data")
        tokenStore.saveToken(authData.token)
        tokenStore.saveUniversitySlug(universitySlug)
        tokenStore.saveRole(authData.user.role)
        return authData.user.role
    }

    suspend fun loginWithGoogle(
        idToken: String,
        universitySlug: String,
        role: String = "student",
        departmentId: Int? = null,
    ): String {
        val response = api.loginWithGoogle(
            GoogleLoginRequest(
                idToken        = idToken,
                universitySlug = universitySlug,
                role           = role,
                departmentId   = departmentId,
            )
        )
        if (!response.success) throw Exception("Google login failed: ${response.message}")
        val authData = response.data ?: throw Exception("Google login returned no data")
        tokenStore.saveToken(authData.token)
        tokenStore.saveUniversitySlug(universitySlug)
        tokenStore.saveRole(authData.user.role)
        return authData.user.role
    }

    // ─────────────────────────────────────────────────────────────
    //  Registration  →  POST /api/auth/register
    // ─────────────────────────────────────────────────────────────

    suspend fun registerStudent(
        universitySlug: String,
        name: String,
        email: String,
        password: String,
        phone: String,
        nationalId: String,
        departmentId: Int,
        level: Int,
        enrollmentYear: Int,
    ): String {
        val response = api.registerStudent(
            StudentRegisterRequest(
                universitySlug = universitySlug,
                name = name,
                email = email,
                password = password,
                phone = phone,
                nationalId = nationalId,
                departmentId = departmentId,
                level = level,
                enrollmentYear = enrollmentYear,
            )
        )
        if (!response.success) throw Exception("Registration failed: ${response.message}")
        val authData = response.data ?: throw Exception("Register returned no data")
        tokenStore.saveToken(authData.token)
        tokenStore.saveUniversitySlug(universitySlug)
        tokenStore.saveRole(authData.user.role)
        return authData.user.role
    }

    suspend fun registerDoctor(
        universitySlug: String,
        name: String,
        email: String,
        password: String,
        phone: String,
        roleTitle: String,
        salary: Double,
        departmentId: Int,
    ): String {
        val response = api.registerDoctor(
            DoctorRegisterRequest(
                universitySlug = universitySlug,
                name = name,
                email = email,
                password = password,
                phone = phone,
                roleTitle = roleTitle,
                salary = salary,
                departmentId = departmentId,
                role = "doctor"
            )
        )
        if (!response.success) throw Exception("Registration failed: ${response.message}")
        val authData = response.data ?: throw Exception("Register returned no data")
        tokenStore.saveToken(authData.token)
        tokenStore.saveUniversitySlug(universitySlug)
        tokenStore.saveRole(authData.user.role)
        return authData.user.role
    }

    // ─────────────────────────────────────────────────────────────
    //  Logout / session helpers
    // ─────────────────────────────────────────────────────────────

    suspend fun logout() {
        try { api.logout() } catch (_: Exception) { }  // best-effort server logout
        tokenStore.clearAll()
    }

    suspend fun isLoggedIn(): Boolean = tokenStore.getToken() != null

    suspend fun getSavedRole(): String? = tokenStore.getRole()
}