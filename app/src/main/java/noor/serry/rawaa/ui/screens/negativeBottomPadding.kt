package noor.serry.rawaa.ui.screens

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Applies a negative bottom padding by pulling the composable upward,
 * effectively overlapping the content below it.
 *
 * @param overlap The amount to overlap. Defaults to 24.dp.
 */
fun Modifier.negativeBottomPadding(overlap: Dp = 24.dp): Modifier =
    this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val overlapPx = overlap.roundToPx()
        layout(placeable.width, placeable.height - overlapPx) {
            placeable.placeRelative(0, -overlapPx)
        }
    }