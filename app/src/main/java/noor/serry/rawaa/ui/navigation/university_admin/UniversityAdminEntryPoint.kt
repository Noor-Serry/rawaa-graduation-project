package noor.serry.rawaa.ui.navigation.university_admin

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
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
fun UniversityAdminEntryPoint(
    mainViewModel: MainViewModel = koinViewModel(),
) {
    val backStack = rememberNavBackStack(UniversityAdminRouteKeys.Dashboard)

    // Derive the selected tab from the top of the back stack so pressing Back
    // automatically de-selects a tab correctly.
    val selectedTab by remember {
        derivedStateOf {
            (backStack.lastOrNull() as? UniversityAdminRouteKeys)?.toNavTab()
                ?: UniversityAdminNavTab.DASHBOARD
        }
    }

    CompositionLocalProvider(
        UniversityAdminBackStackProvider provides backStack,
    ) {
        Scaffold(
            bottomBar = {
                UniversityAdminBottomNav(
                    selectedTab  = selectedTab,
                    onTabSelected = { item ->
                        val isSameTab = (backStack.lastOrNull() as? UniversityAdminRouteKeys)
                            ?.toNavTab() == item.tab
                        if (!isSameTab) {
                            // Pop everything above the root of that tab before pushing
                            backStack.add(item.route)
                        }
                    },
                )
            },
            containerColor = AppTheme.color.bg,
        ) { padding ->
            NavDisplay(
                modifier      = Modifier
                    .fillMaxSize()
                    .padding(bottom = padding.calculateBottomPadding())
                    .statusBarsPadding(),
                backStack     = backStack,
                onBack        = { backStack.removeLastOrNull() },
                entryProvider = universityAdminEntryProvider(mainViewModel),
            )
        }
    }
}

val UniversityAdminBackStackProvider =
    staticCompositionLocalOf<NavBackStack<NavKey>> { error("university admin back stack not provided") }
