package noor.serry.rawaa.ui.screens.onboarding.component

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.zIndex
import noor.serry.designsystem.design.AppTheme
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation

private const val ANIMATION_DURATION = 1000
private const val OFFSET_DISTANCE = 1000

@Composable
fun BoxScope.AnimatedSkipText(
    isVisible: Boolean,
    @StringRes textId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.TopStart
) {

    val layoutDirection = LocalLayoutDirection.current
    val directionMultiplier = if (alignment == Alignment.TopStart) 1 else -1

    val offsetProvider: (Int) -> Int = { fullWidth ->
        val baseOffset = if (layoutDirection == LayoutDirection.Rtl) {
            fullWidth + OFFSET_DISTANCE
        } else {
            fullWidth - OFFSET_DISTANCE
        }
        directionMultiplier * baseOffset
    }

    AnimatedVisibility(
        visible = isVisible,
        modifier = modifier
            .zIndex(10f)
            .align(alignment)
            .clickAnimation(onClick),
        enter = slideInHorizontally(
            animationSpec = tween(ANIMATION_DURATION),
            initialOffsetX = offsetProvider
        ),
        exit = slideOutHorizontally(
            animationSpec = tween(ANIMATION_DURATION),
            targetOffsetX = offsetProvider
        )
    ) {
        Text(
            text = stringResource(textId),
            style = AppTheme.textStyle.body.medium,
            color = AppTheme.color.textSecondary
        )
    }
}