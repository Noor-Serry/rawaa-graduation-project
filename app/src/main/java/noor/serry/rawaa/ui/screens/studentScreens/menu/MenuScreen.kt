package noor.serry.rawaa.ui.screens.studentScreens.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import noor.serry.rawaa.ui.screens.studentScreens.menu.components.StudentHeader
import noor.serry.rawaa.ui.screens.studentScreens.menu.components.StudentMenu
import org.koin.androidx.compose.koinViewModel

/**
 * Menu screen — uses the project's own design system (StudentHeader + StudentMenu)
 * instead of the previous Material3 Scaffold/ListItem mix.
 *
 * userName, userRole, and userInitial are now loaded from the server
 * via MenuViewModel.loadUser() (GET /api/auth/me → UserDto).
 */
@Composable
fun MenuScreen(
    onLoggedOut: () -> Unit = {},
    viewModel: MenuViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.loggedOut) {
        if (state.loggedOut) onLoggedOut()
    }

    Column(modifier = Modifier.fillMaxSize().zIndex(100f)) {
        // Top bar — shows app name and notification bell.
        // state.isOpen drives the hamburger-to-X rotation animation.
        StudentHeader(
            state               = state,
            interactionListener = viewModel,
        )

        // Body — shows:
        //   • Avatar circle with state.userInitial (first char of UserDto.name)
        //   • state.userName  (UserDto.name)
        //   • state.userRole  (UserDto.role translated to Arabic)
        //   • Static menu items: Settings, Help, Privacy
        //   • Logout button → calls MenuViewModel.onLogoutClick()
        StudentMenu(
            state               = state,
            interactionListener = viewModel,
        )
    }
}
