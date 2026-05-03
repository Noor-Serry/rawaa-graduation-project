package noor.serry.rawaa.ui.screens.login.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme

@Composable
fun ActionBanner(
    text: String,
    actionText: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Normal),
    actionTextStyle: TextStyle =  AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold),
    textColor: Color = AppTheme.color.textSecondary,
    actionTextColor: Color = AppTheme.color.primary,
    spaceBetween : Dp = 2.dp
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spaceBetween)
    ) {

        Text(
            text = text,
            style = textStyle,
            color = textColor
        )

        Text(
            text = actionText,
            style = actionTextStyle,
            color = actionTextColor,
            modifier = Modifier.clickAnimation { onActionClick() }
        )
    }
}