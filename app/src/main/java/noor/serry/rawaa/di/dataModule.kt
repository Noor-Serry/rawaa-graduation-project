package noor.serry.rawaa.di

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.preferencesDataStore
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import noor.serry.rawaa.data.local.TokenDataStore
import noor.serry.rawaa.data.repository.UniversityRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/** Base URL — change to your deployed backend host. */
private const val BASE_URL = "http://192.168.1.50/university-api-v3"
private val Context.dataStore by preferencesDataStore(name = "rawaa_prefs")

val dataModule = module {

    // ── Network ───────────────────────────────────────────────────────────────

    single {
        val tokenManager = get<TokenDataStore>()

        HttpClient {
            defaultRequest {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true
                    encodeDefaults = true
                })
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("Ktor", message)
                    }
                }
                level = LogLevel.BODY
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        val token = tokenManager.getToken()
                        Log.d("Ktor", token.toString())

                        if (token != null) {
                            BearerTokens(token, "")
                        } else {
                            null
                        }
                    }
                    sendWithoutRequest { request ->
                        request.url.encodedPath.startsWith("/university-api-v3/api/")
                    }
                }
            }
        }

    }

    // ── Local storage ─────────────────────────────────────────────────────────

    single { androidContext().dataStore }

    single { TokenDataStore(get()) }


    // ── Repository ────────────────────────────────────────────────────────────

    single { UniversityRepository(client = get(), tokenStore = get(), baseUrl = BASE_URL) }
}
