package noor.serry.rawaa.ui.screens.notifications

import android.app.Notification
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.BackStackProvider
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.navigation.base.AppRoute
import noor.serry.rawaa.ui.screens.notifications.components.NotificationCard
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    HandleEffects(effects = viewModel.effect)
    LaunchedEffect(Unit) {
        viewModel.load()
    }
    NotificationsContent(
        state = state,
        interactionListener = viewModel
    )
}

@Composable
private fun NotificationsContent(
    state: NotificationsUiState,
    interactionListener: NotificationsInteractionListener,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.bgHover),
    ) {
        // ── 1. Header ──────────────────────────────────────────────
        item {
            NotificationsHeader(
                todayCount = state.todayCount,
                unreadCount = state.unreadCount,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }

        // ── 2. Mark all / Delete all row ───────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top =24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "تحديد الكل كمقروء",
                    color = AppTheme.color.primaryDark,
                    style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickAnimation { interactionListener.onMarkAllAsRead() }
                )
                Row(
                    modifier = Modifier.clickAnimation { interactionListener.onDeleteAll() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_trash),
                        contentDescription = null,
                        tint = AppTheme.color.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "حذف الكل",
                        color = AppTheme.color.error,
                        style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold),
                    )
                }
            }
        }

        // ── 3. Tab row ─────────────────────────────────────────────
        item {
            NotificationsTabRow(
                selectedTab = state.selectedTab,
                allCount = state.allCount,
                unreadCount = state.unreadCount,
                onTabSelected = interactionListener::onTabSelected,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            )
        }

        // ── 4. Notification cards ──────────────────────────────────
        item {
            NotificationsSection(
                displayed = state.displayedNotifications,
                interactionListener = interactionListener
            )
        }

        // ── 5. Filter section ──────────────────────────────────────
        item {
            FilterSection(
                activeFilters = state.activeFilters,
                onToggleFilter = interactionListener::onToggleFilter,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

// ── Header ───────────────────────────────────────────────────────────────────

@Composable
private fun NotificationsHeader(
    todayCount: Int,
    unreadCount: Int,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        // Gradient background strip
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(192.dp)
                .background(
                    brush = verticalGradient(
                        colors = listOf(
                            AppTheme.color.primary,
                            AppTheme.color.primaryLight,
                        )
                    )
                )
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp)
        ) {
            Text(
                text = "الإشعارات",
                color = AppTheme.color.bg,
                style = AppTheme.textStyle.headline.small,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "جميع التحديثات والتنبيهات",
                color = AppTheme.color.bgSecondary,
                style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Normal),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Floating white stat card
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp,)
                .offset(y = 32.dp)
                .dropShadow(
                    shape = RoundedCornerShape(24.dp),
                    shadow = Shadow(
                        radius = 10.dp,
                        spread = -6.dp,
                        color = AppTheme.color.text.copy(alpha = .1f),
                        offset = DpOffset(0.dp, 8.dp)
                    )
                )
                .dropShadow(
                    shape = RoundedCornerShape(24.dp),
                    shadow = Shadow(
                        radius = 25.dp,
                        spread = -5.dp,
                        color = AppTheme.color.text.copy(alpha = .1f),
                        offset = DpOffset(0.dp, 20.dp)
                    )
                )
                .background(AppTheme.color.bg, RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Unread stat
            NotificationsStatItem(
                value = unreadCount.toString(),
                label = "غير مقروء",
                iconRes = R.drawable.bell,
                iconBg = Color(0xFFDBEAFE),
                iconTint = Color(0xFF3B82F6),
                modifier = Modifier.weight(1f),
            )

            // Today stat
            NotificationsStatItem(
                value = todayCount.toString(),
                label = "اليوم",
                iconRes = R.drawable.ic_calendar,
                iconBg = Color(0xFFFEF3C7),
                iconTint = Color(0xFFF59E0B),
                modifier = Modifier.weight(1f),
            )

        }
    }
}

@Composable
private fun NotificationsStatItem(
    value: String,
    label: String,
    iconRes: Int,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp),
            )
        }
        Column(
            modifier = Modifier
        ) {
            Text(
                text = value,
                color = AppTheme.color.primaryDark,
                style = AppTheme.textStyle.body.large.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = label,
                color = AppTheme.color.textSecondary,
                style = AppTheme.textStyle.label.medium,
            )
        }

    }
}

// ── Tab row ───────────────────────────────────────────────────────────────────

@Composable
private fun NotificationsTabRow(
    selectedTab: NotificationsTab,
    allCount: Int,
    unreadCount: Int,
    onTabSelected: (NotificationsTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth().background(AppTheme.color.bg,RoundedCornerShape(16.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NotificationsTab.entries.forEach { tab ->
            val isSelected = selectedTab == tab
            val bgColor by animateColorAsState(
                if (isSelected) AppTheme.color.primary else AppTheme.color.bg,
                animationSpec = tween(700)
            )
            val textColor by animateColorAsState(
                if (isSelected) AppTheme.color.bg else AppTheme.color.textSecondary,
                animationSpec = tween(700)
            )
            val label = when (tab) {
                NotificationsTab.ALL -> "الكل ($allCount)"
                NotificationsTab.UNREAD -> "غير مقروء ($unreadCount)"
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(bgColor,RoundedCornerShape(12.dp))
                    .clickAnimation { onTabSelected(tab) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = textColor,
                    style = AppTheme.textStyle.body.small,
                )
            }
        }
    }
}

// ── Filter section ────────────────────────────────────────────────────────────

@Composable
private fun FilterSection(
    activeFilters: Set<NotificationType>,
    onToggleFilter: (NotificationType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "تصفية حسب النوع",
            color = AppTheme.color.primary,
            style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold),
        )
        // 2-column grid
        val filterTypes = NotificationType.entries
        filterTypes.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                rowItems.forEach { type ->
                    FilterChip(
                        type = type,
                        isActive = type in activeFilters,
                        onClick = { onToggleFilter(type) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowItems.size == 1) Box(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun FilterChip(
    type: NotificationType,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor by animateColorAsState(
        if (isActive) AppTheme.color.primary else AppTheme.color.bg,
        tween(700)
    )
    val contentColor by animateColorAsState(
        if (isActive) AppTheme.color.bg else AppTheme.color.text,
        tween(700)
    )
    val borderColor by animateColorAsState(
        if (isActive) AppTheme.color.primaryDark else AppTheme.color.border,
        tween(700)
    )
    val iconRes = when (type) {
        NotificationType.GRADE       -> R.drawable.ic_grades
        NotificationType.ASSIGNMENT    -> R.drawable.ic_book
        NotificationType.EXAM        -> R.drawable.ic_calendar
        NotificationType.ANNOUNCEMENT -> R.drawable.ic_info
    }

    val iconColor = when (type) {
        NotificationType.GRADE       -> Color(0xFF10B981)
        NotificationType.ASSIGNMENT    -> Color(0xFF3B82F6)
        NotificationType.EXAM        -> Color(0xFFF59E0B)
        NotificationType.ANNOUNCEMENT -> Color(0xFF8B5CF6)
    }


    Row(
        modifier = modifier
            .background(bgColor,RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickAnimation { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = type.label,
            color = contentColor,
            style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Medium),
        )
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun EmptyNotificationsMessage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "لا توجد إشعارات",
            color = AppTheme.color.textSecondary,
            style = AppTheme.textStyle.body.medium,
        )
    }
}

@Composable
fun NotificationsSection(
    displayed: List<NotificationItem>,
    interactionListener: NotificationsInteractionListener,
    modifier: Modifier = Modifier
) {

    LazyColumn(
        modifier = modifier.fillMaxWidth().heightIn(max = 10000.dp)
    ) {

        if (displayed.isEmpty()) {
            item {
                EmptyNotificationsMessage()
            }
        } else {
            items(
                items = displayed,
                key = { it.id }
            ) { notification ->
                NotificationCard(
                    item = notification,
                    onViewDetails = {
                        interactionListener.onViewDetails(notification.id)
                    },
                    onMarkAsRead = {
                        interactionListener.onMarkAsRead(notification.id)
                    },
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp, bottom = 16.dp)
                )
            }
        }
    }
}


// ── Effects ───────────────────────────────────────────────────────────────────

@Composable
private fun HandleEffects(
    effects: Flow<NotificationsEffect>
) {
    val navigationBackStack = BackStackProvider.current
    LaunchedEffect(Unit) {
        effects.collectLatest { effect ->
            when (effect) {
//                is NotificationsEffect.NavigateToDetails ->
//                    navigationBackStack.add(AppRoute.NotificationDetails(effect.notificationId))
                else -> {}
            }
        }
    }
}
