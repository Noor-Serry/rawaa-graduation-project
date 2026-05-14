package noor.serry.rawaa.ui.screens.notifications.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.screens.notifications.NotificationItem
import noor.serry.rawaa.ui.screens.notifications.NotificationType

// ── Color tokens per notification type ────────────────────────────────────────

private data class NotificationColors(
    val iconBg: Color,
    val iconTint: Color,
)

private fun notificationColors(type: NotificationType): NotificationColors = when (type) {
    NotificationType.GRADE -> NotificationColors(
        iconBg   = Color(0x2010B981),
        iconTint = Color(0xFF10B981),
    )
    NotificationType.ASSIGNMENT -> NotificationColors(
        iconBg   = Color(0x203B82F6),
        iconTint = Color(0xFF3B82F6),
    )
    NotificationType.EXAM -> NotificationColors(
        iconBg   = Color(0x20F59E0B),
        iconTint = Color(0xFFF59E0B),
    )
    NotificationType.ANNOUNCEMENT -> NotificationColors(
        iconBg   = Color(0x208B5CF6),
        iconTint = Color(0xFF8B5CF6),
    )
}

private fun notificationIcon(type: NotificationType): Int = when (type) {
    NotificationType.GRADE       -> R.drawable.ic_grades
    NotificationType.ASSIGNMENT    -> R.drawable.ic_book
    NotificationType.EXAM        -> R.drawable.ic_calendar
    NotificationType.ANNOUNCEMENT -> R.drawable.ic_info
}

// ── Card ──────────────────────────────────────────────────────────────────────

@Composable
fun NotificationCard(
    item: NotificationItem,
    onViewDetails: () -> Unit,
    onMarkAsRead: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = notificationColors(item.type)
    val borderColor by animateColorAsState(if (item.isRead) AppTheme.color.border
    else AppTheme.color.primary, tween(700)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AppTheme.color.bg,RoundedCornerShape(16.dp))
            .border(1.17.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        // ── Top row: unread dot + title + icon ────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Colored icon box
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(colors.iconBg,RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(notificationIcon(item.type)),
                        contentDescription = null,
                        tint = colors.iconTint,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        color = AppTheme.color.primaryDark,
                        style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold),
                    )
                    Text(
                        text = item.body,
                        color = AppTheme.color.textSecondary,
                        style = AppTheme.textStyle.body.small.copy(
                            fontWeight = FontWeight.Normal
                        ),
                        modifier = Modifier.padding(top = 2.dp),
                    )
                    // ── Time ago ──────────────────────────────────────────────
                    Text(
                        text = item.timeAgo,
                        color = AppTheme.color.textSecondary,
                        style = AppTheme.textStyle.label.medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            // Unread dot
            if (!item.isRead) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AppTheme.color.primary)
                )
            }
        }

        // ── Action buttons row (only shown when unread) ───────────
        AnimatedVisibility (!item.isRead) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Ghost: mark as read
                Box(
                    modifier = Modifier.fillMaxWidth().background(AppTheme.color.primary,RoundedCornerShape(12.dp))
                        .clickAnimation { onMarkAsRead() }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "تحديد كمقروء",
                        color = AppTheme.color.bg,
                        style = AppTheme.textStyle.body.small,
                    )
                }
            }
        }
    }
}
