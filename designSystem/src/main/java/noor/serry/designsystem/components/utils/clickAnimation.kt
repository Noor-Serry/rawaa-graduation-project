package noor.serry.designsystem.components.utils

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale

fun Modifier.clickAnimation(onClick: () -> Unit): Modifier = composed {
    var animateTrigger by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (animateTrigger) 1.1f else 1f,
        animationSpec = tween(120),
        finishedListener = {
            animateTrigger = false
        }
    )

    val alpha by animateFloatAsState(
        targetValue = if (animateTrigger) .8f else 1f,
        animationSpec = tween(120),
        finishedListener = {
            animateTrigger = false
        }
    )
    this
        .scale(scale)
        .alpha(alpha)
        .clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            animateTrigger = true
            onClick()
        }
}
