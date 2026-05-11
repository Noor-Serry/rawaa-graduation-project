package noor.serry.rawaa.ui.screens.schedule

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import noor.serry.designsystem.components.BaseButton
import org.koin.compose.viewmodel.koinViewModel
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.BackStackProvider
import noor.serry.rawaa.R

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    HandleEffects(effects = viewModel.effect)

    ScheduleContent(
        state = state,
        interactionListener = viewModel
    )
}

@Composable
private fun ScheduleContent(
    state: ScheduleUiState,
    interactionListener: ScheduleInteractionListener,
) {
    val sessions = state.sessionsForSelectedDay

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.bgHover),
    ) {
        // ── 1. Header (gradient + floating stat card) ──────────────
        item {
            ScheduleHeader(
                totalLecturesPerWeek = state.totalLecturesPerWeek,
                totalCourses = state.totalCourses,
                totalDays = state.totalDays,
                modifier = Modifier.padding(bottom = 40.dp)
            )
        }

        // ── 2. Day selector ────────────────────────────────────────
        item {
            DaySelector(
                days = DayOfWeek.entries,
                selectedDay = state.selectedDay,
                onDaySelected = interactionListener::onDaySelected,
                modifier = Modifier.padding(24.dp)
            )
        }

        // ── 3. Session cards or empty state ───────────────────────
        if (sessions.isEmpty()) {
            item {
                EmptyDayMessage()
            }
        } else {
            items(
                items = sessions,
                key = { it.courseCode + it.timeRange }
            ) { session ->
                SessionCard(
                    item = session,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        // ── 4. Full week CTA button ────────────────────────────────
        if (sessions.isNotEmpty())
            item {
            BaseButton(
                text = "عرض الجدول الأسبوعي الكامل",
                onClick = {},
                icon = painterResource(R.drawable.ic_calendar), // غير الاسم على حسب الـ drawable عندك
                backgroundColor = Color.Transparent,
                textColor = AppTheme.color.primary,
                iconColor = AppTheme.color.primary,
                borderColor = AppTheme.color.primary,
                borderWidth = 1.17.dp,
                roundedCornerSize = 16.dp,
                modifier = Modifier.padding(24.dp),
                textStyle = AppTheme.textStyle.body.medium.copy(
                    fontWeight = FontWeight
                        .Bold
                )
            )
        }
    }
}

// ── Header ──────────────────────────────────────────────────────────────────

@Composable
private fun ScheduleHeader(
    totalLecturesPerWeek: Int,
    totalCourses: Int,
    totalDays: Int,
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
                            AppTheme.color.primaryLight
                        )
                    )
                )
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp)
        ) {
            Text(
                text = "الجدول الدراسي",
                color = AppTheme.color.bg,
                style = AppTheme.textStyle.headline.small,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "مواعيد المحاضرات والعمليات",
                color = AppTheme.color.bgSecondary,
                style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Normal),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
            )
        }

        // Floating white stat card — offset below the gradient
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .offset(y = 40.dp)
                .dropShadow(
                    shape = RoundedCornerShape(24.dp),
                    shadow = Shadow(
                        radius = 10.dp,
                        spread = -6.dp,
                        color = AppTheme.color.text.copy(alpha = .1f),
                        offset = DpOffset(x = 0.dp, y = 8.dp)
                    )
                )
                .dropShadow(
                    shape = RoundedCornerShape(24.dp),
                    shadow = Shadow(
                        radius = 25.dp,
                        spread = -5.dp,
                        color = AppTheme.color.text.copy(alpha = .1f),
                        offset = DpOffset(x = 0.dp, y = 20.dp)
                    )
                )
                .background(AppTheme.color.bg, RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ScheduleStatItem(
                value = totalLecturesPerWeek.toString(),
                label = "محاضرة أسبوعياً",
                modifier = Modifier.weight(1f)
            )
            ScheduleStatItem(
                value = totalCourses.toString(),
                label = "مقررات",
                modifier = Modifier.weight(1f)
            )
            ScheduleStatItem(
                value = totalDays.toString(),
                label = "أيام",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ScheduleStatItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            color = AppTheme.color.primary,
            style = AppTheme.textStyle.headline.small,
        )
        Text(
            text = label,
            color = AppTheme.color.textSecondary,
            style = AppTheme.textStyle.label.medium,
        )
    }
}

// ── Day selector ─────────────────────────────────────────────────────────────

@Composable
private fun DaySelector(
    days: List<DayOfWeek>,
    selectedDay: DayOfWeek,
    onDaySelected: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .height(106.dp)
            .background(AppTheme.color.bg, RoundedCornerShape(16.dp)),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(days, key = { it }) { day ->
            val isSelected = day == selectedDay
            val backgroundColor by animateColorAsState(
                if (isSelected) AppTheme.color.primary else AppTheme.color.bgHover,
                tween(700)
            )
            val textColor by animateColorAsState(
                if (isSelected) AppTheme.color.bg else AppTheme.color.textSecondary,
                tween(700)
            )

            Column(
                modifier = Modifier
                    .defaultMinSize(minWidth = 70.dp)
                    .background(backgroundColor, RoundedCornerShape(12.dp))
                    .clickAnimation { onDaySelected(day) }
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = day.shortLabel,
                    color = textColor,
                    style = AppTheme.textStyle.label.large.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = day.label,
                    color = textColor,
                    style = AppTheme.textStyle.label.medium.copy(fontWeight = FontWeight.Bold),
                )
            }
        }
    }
}
// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun EmptyDayMessage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "لا توجد محاضرات هذا اليوم",
            color = AppTheme.color.textSecondary,
            style = AppTheme.textStyle.body.medium,
        )
    }
}

// ── Effects ───────────────────────────────────────────────────────────────────

@Composable
private fun HandleEffects(
    effects: Flow<ScheduleEffect>
) {
    val navigationBackStack = BackStackProvider.current
    LaunchedEffect(Unit) {
        effects.collectLatest { effect ->
            when (effect) {
//                is ScheduleEffect.NavigateToSessionDetails ->
//                    navigationBackStack.add(AppRoute.SessionDetails(effect.courseCode))
//                ScheduleEffect.NavigateToFullWeekSchedule ->
//                    navigationBackStack.add(AppRoute.FullWeekSchedule)
                else -> {}
            }
        }
    }
}