package noor.serry.rawaa.ui.screens.login.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme

@Composable
fun RowScope.UserRoleCard(
    isSelected: Boolean,
    label: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val roundShape = RoundedCornerShape(16.dp)
    val color by animateColorAsState(if (isSelected) AppTheme.color.bgDisabled else AppTheme.color.bg)
    val borderColor by animateColorAsState(if (isSelected) AppTheme.color.borderFocus else AppTheme.color.border)
    val textColor by animateColorAsState(if (isSelected) AppTheme.color.primaryDark else AppTheme.color.textSecondary)
    Box(
        modifier
            .weight(1f)
            .height(84.dp)
            .then(
                if (isSelected) Modifier
                    .dropShadow(
                        shape = RoundedCornerShape(24.dp),
                        shadow = Shadow(
                            radius = 6.dp,
                            spread = -4.dp,
                            color = AppTheme.color.text.copy(alpha = .1f),
                            offset = DpOffset(x = 0.dp, y = 4.dp)
                        )
                    )
                    .dropShadow(
                        shape = RoundedCornerShape(24.dp),
                        shadow = Shadow(
                            radius = 15.dp,
                            spread = -3.dp,
                            color = AppTheme.color.text.copy(alpha = .1f),
                            offset = DpOffset(x = 0.dp, y = 10.dp)
                        )
                    )
                else Modifier
            )
            .background(
                color = color,
                shape = roundShape
            )
            .border(1.17.dp, borderColor, roundShape)
            .then(
                if (onClick != null) Modifier.clickAnimation { onClick() }
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label, color = textColor, style = AppTheme.textStyle.body.small.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}
