package noor.serry.rawaa

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import noor.serry.rawaa.ui.MainUiState
import noor.serry.rawaa.ui.MainViewModel
import noor.serry.rawaa.ui.navigation.base.AppRoute
import noor.serry.rawaa.ui.navigation.base.appEntryProvider
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RawaaApp(
    mainViewModel: MainViewModel = koinViewModel()
) {
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    if (uiState == MainUiState.Loading) return

    val startDestination = when (uiState) {
        MainUiState.Loading,
        MainUiState.ShowAuth   -> AppRoute.Login
        MainUiState.ShowOnboarding -> AppRoute.Onboarding
        is MainUiState.ShowMain -> {
            val role = (uiState as MainUiState.ShowMain).role
            Log.e("RawaaApp", "Resuming session as role=$role")
            roleToRoute(role)
        }
    }

    Log.e("RawaaApp", "startDestination=$startDestination")
    val backStack = rememberNavBackStack(startDestination)

    CompositionLocalProvider(BackStackProvider provides backStack) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = appEntryProvider,
        )
    }
}

/**
 * Maps the persisted role string to the correct root [AppRoute].
 *
 * | Role string  | Entry point           |
 * |--------------|-----------------------|
 * | "student"    | StudentEntry          |
 * | "doctor"     | TeacherEntry          |
 * | "employee"   | TeacherEntry          |
 * | "admin"      | AdminEntry            |
 * | "super"      | SuperAdminEntry       |
 * | anything else| Login (safe fallback) |
 */
private fun roleToRoute(role: String): AppRoute = when (role) {
    "student"  -> AppRoute.StudentEntry
    "doctor"   -> AppRoute.TeacherEntry
    "employee" -> AppRoute.TeacherEntry
    "admin"    -> AppRoute.AdminEntry
    "super"    -> AppRoute.SuperAdminEntry
    else       -> AppRoute.Login
}

@SuppressLint("CompositionLocalNaming")
val BackStackProvider =
    staticCompositionLocalOf<NavBackStack<NavKey>> { error("back stack not provided") }
