package noor.serry.rawaa.ui.screens.home_student.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.design.AppTheme

data class QuickAction(
    val title: String,
    val subtitle: String,
    val iconRes: Int,
    val isYellowIcon: Boolean = false,
    val onClick: () -> Unit = {}
)

@Composable
fun QuickActionCard(
    action: QuickAction,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg)
            .border(1.17.dp, AppTheme.color.border, RoundedCornerShape(16.dp))
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (action.isYellowIcon) Color(0x33FACC15)
                    else AppTheme.color.primary.copy(alpha = .1f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(action.iconRes),
                contentDescription = null,
                tint = if (action.isYellowIcon) Color(0xFFF59E0B) else AppTheme.color.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        Column(modifier = Modifier.align(Alignment.CenterHorizontally),horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = action.title,
                color = AppTheme.color.primaryDark,
                style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = action.subtitle,
                color = AppTheme.color.textSecondary,
                style = AppTheme.textStyle.label.medium,
            )
        }
    }
}

@Composable
fun QuickActionsSection(
    actions: List<QuickAction>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "إجراءات سريعة",
            color = AppTheme.color.primaryDark,
            style = AppTheme.textStyle.body.large.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.fillMaxWidth()
        )
        // 2-column grid
        val rows = actions.chunked(2)
        Column(
            modifier = Modifier.padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            rows.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { action ->
                        QuickActionCard(
                            action = action,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Fill empty cell if odd number
                    if (rowItems.size == 1) {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
