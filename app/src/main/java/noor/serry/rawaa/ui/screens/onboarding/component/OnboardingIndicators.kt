package noor.serry.rawaa.ui.screens.onboarding.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import noor.serry.designsystem.design.AppTheme

@Composable
fun OnboardingIndicators(
    modifier: Modifier = Modifier,
    currentIndicatorNumber: Int,
    numberOfIndicators: Int = 3,
    activeColor : Color = AppTheme.color.primary,
    inactiveColor : Color = AppTheme.color.border,
    dotSize : Dp = 9.dp,
    activeIndicatorWidth : Dp = 20.dp,
    spacing : Dp = 8.dp
) {

    Row(modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
            (0 until numberOfIndicators).forEach { index ->
                val width by animateDpAsState(
                    if (index == currentIndicatorNumber)
                        activeIndicatorWidth else dotSize,
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
                val color by animateColorAsState(if (index == currentIndicatorNumber)
                    activeColor else inactiveColor,
                    animationSpec = tween(300, easing = FastOutSlowInEasing))
                Box(
                    modifier = Modifier
                        .width(width)
                        .height(9.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color)
                )
            }
        }

    }
}