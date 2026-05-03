package noor.serry.rawaa.ui.screens.studentScreens.menu

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import noor.serry.rawaa.ui.navigation.student.StudentRouteKeys
import noor.serry.rawaa.ui.navigation.student.studentEntryProvider
import noor.serry.rawaa.ui.screens.home_student.components.HomeNavTab
import noor.serry.rawaa.ui.screens.home_student.components.HomeStudentBottomNav

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun StudentEntryPoint() {
    val backStack = rememberNavBackStack(StudentRouteKeys.Home)
    var selectedTab by remember { mutableStateOf(HomeNavTab.HOME) }

    CompositionLocalProvider(
        StudentBackStackProvider provides backStack
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            MenuScreen()
            // Layer 1: Scaffold with nav + bottom bar
            Scaffold(
                bottomBar = {
                    HomeStudentBottomNav(
                        selectedTab = selectedTab,
                        onTabSelected = { tab ->
                            if (backStack.last() != tab.route) {
                                selectedTab = tab.tab
                                backStack.add(tab.route)
                            }
                        },
                    )
                },
                containerColor = AppTheme.color.bg
            ) {


                NavDisplay(
                    modifier = Modifier
                        .padding(top = 64.dp, bottom = it.calculateBottomPadding())
                        .statusBarsPadding(),
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = studentEntryProvider
                )
            }

        }
    }
}


val StudentBackStackProvider =
    staticCompositionLocalOf<NavBackStack<NavKey>> { error("student back stack dose not provided") }