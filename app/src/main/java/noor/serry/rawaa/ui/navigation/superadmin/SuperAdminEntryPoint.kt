package noor.serry.rawaa.ui.navigation.superadmin

import android.annotation.SuppressLint
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
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import noor.serry.designsystem.design.AppTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SuperAdminEntryPoint() {
    val backStack = rememberNavBackStack(SuperAdminRouteKeys.Home)
    var selectedTab by remember { mutableStateOf(SuperAdminNavTab.HOME) }

    CompositionLocalProvider(
        SuperAdminBackStackProvider provides backStack
    ) {
        Scaffold(
            bottomBar = {
                HomeSuperAdminBottomNav(
                    selectedTab = selectedTab,
                    onTabSelected = { item ->
                        if (backStack.last() != item.route) {
                            selectedTab = item.tab
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
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = superAdminEntryProvider,
            )
        }
    }
}

val SuperAdminBackStackProvider =
    staticCompositionLocalOf<NavBackStack<NavKey>> { error("super admin back stack not provided") }
