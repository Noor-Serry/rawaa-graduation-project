package noor.serry.designsystem.components

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import noor.serry.designsystem.design.AppTheme
import noor.serry.designsystem.components.utils.clickAnimation


@SuppressLint("LocalContextConfigurationRead")
@Composable
fun BaseButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    roundedCornerSize: Dp = 100.dp,
    backgroundColor: Color = AppTheme.color.primary,
    textColor: Color = AppTheme.color.bg,
    isEnable : Boolean = true,
    isMirror : Boolean = true
) {
    val backgroundColor by animateColorAsState(
        if(isEnable) backgroundColor else backgroundColor.copy(alpha = 0.2f)
    )
    val context = LocalContext.current
    val language = context.resources.configuration.locales.get(0).language
    val rotate = 0f
    Row(
        modifier = modifier
            .then(
                if (isEnable)
                    Modifier.clickAnimation(onClick = onClick)
                else Modifier
            ).background(backgroundColor, RoundedCornerShape(roundedCornerSize))
            .clip(RoundedCornerShape(roundedCornerSize)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text, style = AppTheme.textStyle.body.large, color = textColor)
        icon?.let {
            Icon(
                painter = icon,
                tint = textColor,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp)
                    .rotate(if (language == "en" || !isMirror) rotate else rotate + 180)
            )
        }
    }
}