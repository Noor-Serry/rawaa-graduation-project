package noor.serry.rawaa.ui.navigation.super_admin

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
import noor.serry.rawaa.ui.MainViewModel
import org.koin.compose.viewmodel.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AdminEntryPoint(
    mainViewModel: MainViewModel = koinViewModel(),
) {
    val backStack   = rememberNavBackStack(AdminRouteKeys.Home)
    var selectedTab by remember { mutableStateOf(AdminNavTab.HOME) }

    CompositionLocalProvider(
        AdminBackStackProvider provides backStack,
    ) {
        Scaffold(
            bottomBar = {
                HomeAdminBottomNav(
                    selectedTab = selectedTab,
                    onTabSelected = { item ->
                        val tab = when (item.route) {
                            AdminRouteKeys.Home          -> AdminNavTab.HOME
                            AdminRouteKeys.Universities  -> AdminNavTab.UNIVERSITIES
                            AdminRouteKeys.AddUniversity -> AdminNavTab.ADD_UNIVERSITY
                        }
                        if (backStack.last() != item.route) {
                            selectedTab = tab
                            backStack.add(item.route)
                        }
                    },
                )
            },
            containerColor = AppTheme.color.bg,
        ) { padding ->
            NavDisplay(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = padding.calculateBottomPadding())
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
