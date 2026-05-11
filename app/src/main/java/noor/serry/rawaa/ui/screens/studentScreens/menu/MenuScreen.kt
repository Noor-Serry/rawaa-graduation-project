package noor.serry.rawaa.ui.screens.studentScreens.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import noor.serry.rawaa.ui.screens.studentScreens.menu.components.StudentMenu

/**
 * Receives state and listener from the parent (StudentEntryPoint) so the
 * exact same ViewModel instance that drives the header also drives the drawer.
 * No koinViewModel() here — that was causing two separate instances.
 */
@Composable
fun MenuScreen(
    state: MenuUiState,
    interactionListener: MenuInteractionListener,
    onLoggedOut: () -> Unit = {},
) {
    LaunchedEffect(state.loggedOut) {
        if (state.loggedOut) onLoggedOut()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Dim scrim ─────────────────────────────────────────────────────
        AnimatedVisibility(
            visible  = state.isOpen,
            enter    = fadeIn(tween(250)),
            exit     = fadeOut(tween(250)),
            modifier = Modifier
                .fillMaxSize()
                .zIndex(50f),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null,
                        onClick           = interactionListener::onMenuDismiss,
                    )
            )
        }

        // ── Side drawer panel — slides in from the right ──────────────────
        AnimatedVisibility(
            visible  = state.isOpen,
            enter    = slideInHorizontally(
                animationSpec  = tween(durationMillis = 340),
                initialOffsetX = { fullWidth -> -fullWidth },
            ),
            exit     = slideOutHorizontally(
                animationSpec = tween(durationMillis = 280),
                targetOffsetX = { fullWidth -> -fullWidth },
            ),
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = 0.80f)
                .align(Alignment.CenterEnd)
                .zIndex(51f),
        ) {
            StudentMenu(
                state               = state,
                interactionListener = interactionListener,
            )
        }
    }
}
