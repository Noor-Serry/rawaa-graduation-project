package noor.serry.rawaa.di

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.preferencesDataStore
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import noor.serry.rawaa.data.local.TokenDataStore
import noor.serry.rawaa.data.remote.ApiClient
import noor.serry.rawaa.data.repository.*
import noor.serry.rawaa.domain.repository.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/** Base URL — change to your deployed backend host. */
private const val BASE_URL = "https://abdallah-elrefai.com/university-api-v4"
private val Context.dataStore by preferencesDataStore(name = "rawaa_prefs")

val dataModule = module {

    // ── Network ───────────────────────────────────────────────────────────────

    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("Ktor", message)
                    }
                }
                level = LogLevel.BODY
            }
        }
    }

    // ── Local storage ─────────────────────────────────────────────────────────

    single { TokenDataStore(get()) }

    single {
        androidContext().dataStore   // androidx.datastore.preferences.preferencesDataStore
    }

    // ── API client ────────────────────────────────────────────────────────────

    single { ApiClient(client = get(), tokenStore = get(), baseUrl = BASE_URL) }

    // ── Auth (not a domain interface — used directly by Auth ViewModel) ───────

    single { AuthRepositoryImpl(api = get(), tokenStore = get()) }

    // ── Domain repositories ───────────────────────────────────────────────────

    singleOf(::DashboardRepositoryImpl)    { bind<DashboardRepository>() }
    singleOf(::CourseRepositoryImpl)       { bind<CourseRepository>() }
    singleOf(::AssignmentRepositoryImpl)   { bind<AssignmentRepository>() }
    singleOf(::NotificationRepositoryImpl) { bind<NotificationRepository>() }
    singleOf(::ScheduleRepositoryImpl)     { bind<ScheduleRepository>() }
    singleOf(::StudentRepositoryImpl)      { bind<StudentRepository>() }
    singleOf(::ProfileRepositoryImpl)      { bind<ProfileRepository>() }
    singleOf(::DepartmentRepository)
   // singleOf(::GoogleAuthDataSourceImpl) bind GoogleAuthDataSource
}
