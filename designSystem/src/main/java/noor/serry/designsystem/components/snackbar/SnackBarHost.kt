package noor.serry.designsystem.components.snackbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInQuart
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest


@Composable
fun BoxScope. SnackBarHost(
    modifier: Modifier = Modifier
) {
    var currentSnackBar by remember { mutableStateOf<SnackBarUiMessage?>(null) }
    var showSnackBar by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        SnackBarManager.snackBarFlow.collectLatest { snackBarData ->
            currentSnackBar = snackBarData
            showSnackBar = true

            delay(2500)
            showSnackBar = false

            delay(1500)
            currentSnackBar = null
        }
    }

    AnimatedVisibility(
        visible = showSnackBar ,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(500, easing = EaseOutQuart)
        ),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(800, easing = EaseInQuart)
        ),
        modifier = modifier
    ) {
        currentSnackBar?.let { snackBarUiMessage ->
            SnackBar(snackBarUiMessage)
        }
    }
}