package noor.serry.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import noor.serry.designsystem.design.AppTheme


@Composable
fun Icon(painter: Painter,
         modifier: Modifier = Modifier,
         contentDescription : String? = null,
         tint : Color = AppTheme.color.primary
) {
    Image(painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        colorFilter = ColorFilter.tint(tint)
    )
}