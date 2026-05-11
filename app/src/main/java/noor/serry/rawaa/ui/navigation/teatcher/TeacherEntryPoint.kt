package noor.serry.rawaa.ui.navigation.teatcher

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import noor.serry.designsystem.design.AppTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TeacherEntryPoint() {
    val backStack = rememberNavBackStack(TeacherRouteKeys.Home)

    CompositionLocalProvider(
        TeacherBackStackProvider provides backStack
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                bottomBar = {
                    HomeTeacherBottomNav(
                        selectedTab =backStack.lastOrNull()?.let { it.toNavTab() } ?: TeacherNavTab.HOME,
                        onTabSelected = { item ->
                            if (backStack.last() != item.route) {
                                backStack.add(item.route)
                            }
                        },
                    )
                },
                containerColor = AppTheme.color.bg,
            ) {
                NavDisplay(
                    modifier = Modifier
                        .padding(bottom = it.calculateBottomPadding())
                        .statusBarsPadding(),
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = teacherEntryProvider,
                )
            }
        }
    }
}

val TeacherBackStackProvider =
    staticCompositionLocalOf<NavBackStack<NavKey>> { error("teacher back stack not provided") }