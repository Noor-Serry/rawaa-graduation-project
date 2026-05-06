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
    if (uiState ==MainUiState.Loading ) return
    val startDestination = when(uiState){
        MainUiState.Loading ,
        MainUiState.ShowAuth -> AppRoute.Login
        is MainUiState.ShowMain -> {
            Log.e("RawaaApp.kt",""+uiState.toString() )
            if ((uiState as MainUiState.ShowMain).role == "doctor")
                AppRoute.TeacherEntry else AppRoute.StudentEntry
        }
        MainUiState.ShowOnboarding -> AppRoute.Onboarding
    }
    Log.e("RawaaApp.kt", startDestination.toString())
    val backStack = rememberNavBackStack(startDestination)
    CompositionLocalProvider(
        BackStackProvider provides backStack
    ) {

        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = appEntryProvider
        )
}
}


@SuppressLint("CompositionLocalNaming")
val BackStackProvider =
    staticCompositionLocalOf<NavBackStack<NavKey>> { error("back stack dose not provided") }