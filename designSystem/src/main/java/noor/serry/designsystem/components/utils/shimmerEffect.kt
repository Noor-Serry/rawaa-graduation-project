package noor.serry.designsystem.components.utils

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

internal fun Modifier.shimmerEffect(colors : List<Color>): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }


    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val anim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )
    val brush = remember(size, anim) {
        val startX = -size.width.toFloat()
        val endX = size.width.toFloat()

        Brush.linearGradient(
            colors = colors,
            start = Offset(startX + anim * endX * 4, 0f),
            end = Offset(startX + anim * endX * 4 + size.width, size.height.toFloat())
        )
    }

    background(brush)
        .onGloballyPositioned { size = it.size }
}