package noor.serry.rawaa.ui.navigation.admin

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import noor.serry.designsystem.design.AppTheme
import org.koin.compose.viewmodel.koinViewModel
import noor.serry.rawaa.ui.MainViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AdminEntryPoint(
    mainViewModel: MainViewModel = koinViewModel(),
) {
    val backStack    = rememberNavBackStack(AdminRouteKeys.Home)
    var selectedTab  by remember { mutableStateOf(AdminNavTab.HOME) }

    CompositionLocalProvider(
        AdminBackStackProvider provides backStack
    ) {
        Scaffold(
            bottomBar = {
                HomeAdminBottomNav(
                    selectedTab = selectedTab,
                    onTabSelected = { item ->
                        // Map route to tab enum
                        val tab = when (item.route) {
                            AdminRouteKeys.Home     -> AdminNavTab.HOME
                            AdminRouteKeys.Users    -> AdminNavTab.USERS
                            AdminRouteKeys.Courses  -> AdminNavTab.COURSES
                            AdminRouteKeys.Reports  -> AdminNavTab.REPORTS
                            AdminRouteKeys.Settings -> AdminNavTab.SETTINGS
                            else                    -> item.tab
                        }
                        if (backStack.last() != item.route) {
                            selectedTab = tab
                            backStack.add(item.route)
                        }
                    },
                )
            },
            containerColor = AppTheme.color.bg,
        ) {
            NavDisplay(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = it.calculateBottomPadding())
                    .statusBarsPadding(),
                backStack     = backStack,
                onBack        = { backStack.removeLastOrNull() },
                entryProvider = adminEntryProvider(mainViewModel),
            )
        }
    }
}

val AdminBackStackProvider =
    staticCompositionLocalOf<NavBackStack<NavKey>> { error("admin back stack not provided") }
