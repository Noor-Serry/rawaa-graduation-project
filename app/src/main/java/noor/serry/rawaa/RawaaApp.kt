package noor.serry.rawaa

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import noor.serry.rawaa.ui.navigation.base.AppRoute
import noor.serry.rawaa.ui.navigation.base.appEntryProvider


@Composable
fun RawaaApp(
) {
    val backStack = rememberNavBackStack(AppRoute.Onboarding)
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