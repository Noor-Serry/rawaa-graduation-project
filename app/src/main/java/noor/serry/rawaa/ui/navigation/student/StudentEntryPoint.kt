package noor.serry.rawaa.ui.navigation.student

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import noor.serry.designsystem.components.snackbar.SnackBarHost
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.ui.navigation.student.HomeNavTab
import noor.serry.rawaa.ui.navigation.student.HomeStudentBottomNav
import noor.serry.rawaa.ui.screens.studentScreens.menu.MenuScreen
import noor.serry.rawaa.ui.screens.studentScreens.menu.MenuViewModel
import noor.serry.rawaa.ui.screens.studentScreens.menu.components.StudentHeader
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun StudentEntryPoint() {
    val backStack = rememberNavBackStack(StudentRouteKeys.Home)

    // Derive the selected tab from the last entry in the backstack —
    // same pattern as TeacherEntryPoint. Falls back to HOME for any
    // route that has no corresponding tab (e.g. a detail screen).
    val selectedTab = backStack.lastOrNull()
        ?.let { it.toStudentNavTab() }
        ?: HomeNavTab.HOME

    // ONE ViewModel instance — shared by the header, the scrim, and the drawer panel.
    val menuViewModel: MenuViewModel = koinViewModel()
    val menuState by menuViewModel.uiState.collectAsState()

    CompositionLocalProvider(
        StudentBackStackProvider provides backStack
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top bar — always visible ───────────────────────────────────
            StudentHeader(
                state               = menuState,
                interactionListener = menuViewModel,
                onNotificationClick = {
                    // Avoid duplicate entries if already on Notifications
                    if (backStack.last() != StudentRouteKeys.Notifications) {
                        backStack.add(StudentRouteKeys.Notifications)
                    }
                },
            )

            // ── Content area ───────────────────────────────────────────────
            Box(modifier = Modifier.weight(1f)) {

                Scaffold(
                    bottomBar = {
                        HomeStudentBottomNav(
                            selectedTab   = selectedTab,
                            onTabSelected = { item ->
                                if (backStack.last() != item.route) {
                                    backStack.add(item.route)
                                }
                            },
                        )
                    },
                    containerColor = AppTheme.color.bg,
                ) { innerPadding ->
                    NavDisplay(
                        modifier      = Modifier
                            .padding(bottom = innerPadding.calculateBottomPadding()),
                        backStack     = backStack,
                        onBack        = { backStack.removeLastOrNull() },
                        entryProvider = studentEntryProvider,
                    )
                }

                // Drawer overlay — sits on top of Scaffold, same Box
                MenuScreen(
                    state               = menuState,
                    interactionListener = menuViewModel,
                )

                SnackBarHost(
                    Modifier
                        .zIndex(100f)
                        .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                )
            }
        }
    }
}

val StudentBackStackProvider =
    staticCompositionLocalOf<NavBackStack<NavKey>> { error("student back stack not provided") }