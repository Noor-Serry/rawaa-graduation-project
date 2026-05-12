package noor.serry.rawaa.ui.screens.reports_admin

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import kotlin.math.roundToInt

// ── Entry point ───────────────────────────────────────────────────────────────

@Composable
fun ReportsAdminScreen(
    viewModel: ReportsAdminViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    when {
        state.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppTheme.color.primary)
            }
        }
        state.errorMessage != null -> {
            ReportsErrorState(message = state.errorMessage ?: "", onRetry = viewModel::load)
        }
        else -> {
            ReportsAdminContent(state = state, listener = viewModel)
        }
    }
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun ReportsAdminContent(
    state: ReportsAdminUiState,
    listener: ReportsAdminInteractionListener,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.bgHover),
    ) {
        // 1 ── Hero
        item {
            ReportsHeroHeader(state = state)
        }

        // 2 ── Tab row
        item {
            ReportsTabRow(
                selectedTab   = state.selectedTab,
                gradesCount   = state.gradesRows.size,
                attendCount   = state.attendanceRows.size,
                onTabSelected = listener::onTabSelected,
                modifier      = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            )
        }

        // 3 ── Empty state
        val hasData = if (state.selectedTab == ReportsAdminUiState.ReportTab.GRADES)
            state.gradesRows.isNotEmpty() else state.attendanceRows.isNotEmpty()

        if (!hasData) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 60.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text  = "لا توجد بيانات",
                        color = AppTheme.color.textSecondary,
                        style = AppTheme.textStyle.body.medium,
                    )
                }
            }
        }

        // 4a ── Grades rows
        if (state.selectedTab == ReportsAdminUiState.ReportTab.GRADES) {
            items(state.gradesRows) { row ->
                GradesReportCard(
                    item     = row,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 10.dp),
                )
            }
        }

        // 4b ── Attendance rows
        if (state.selectedTab == ReportsAdminUiState.ReportTab.ATTENDANCE) {
            items(state.attendanceRows) { row ->
                AttendanceReportCard(
                    item     = row,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 10.dp),
                )
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

// ── Hero header ───────────────────────────────────────────────────────────────

@Composable
private fun ReportsHeroHeader(
    state: ReportsAdminUiState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(verticalGradient(listOf(AppTheme.color.primary, AppTheme.color.primaryLight)))
            .padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text  = "التقارير",
                        color = AppTheme.color.bg,
                        style = AppTheme.textStyle.headline.small,
                    )
                    Text(
                        text  = "تقارير الدرجات والحضور",
                        color = AppTheme.color.bg.copy(alpha = .75f),
                        style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppTheme.color.bg.copy(alpha = .15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter  = painterResource(R.drawable.google),
                        tint     = AppTheme.color.bg,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ReportsHeroChip(
                    label    = "مقررات في التقرير",
                    value    = "${state.gradesRows.size}",
                    modifier = Modifier.weight(1f),
                )
                ReportsHeroChip(
                    label    = "طلاب في الحضور",
                    value    = "${state.attendanceRows.size}",
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ReportsHeroChip(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AppTheme.color.bg.copy(alpha = .12f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(text = value, color = AppTheme.color.bg, style = AppTheme.textStyle.headline.small)
        Text(text = label, color = AppTheme.color.bg.copy(alpha = .8f), style = AppTheme.textStyle.label.small)
    }
}

// ── Tab row ───────────────────────────────────────────────────────────────────

@Composable
private fun ReportsTabRow(
    selectedTab: ReportsAdminUiState.ReportTab,
    gradesCount: Int,
    attendCount: Int,
    onTabSelected: (ReportsAdminUiState.ReportTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppTheme.color.bg)
            .border(1.dp, AppTheme.color.border.copy(alpha = .5f), RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        ReportsTabButton(
            label      = "الدرجات ($gradesCount)",
            isSelected = selectedTab == ReportsAdminUiState.ReportTab.GRADES,
            onClick    = { onTabSelected(ReportsAdminUiState.ReportTab.GRADES) },
            modifier   = Modifier.weight(1f),
        )
        ReportsTabButton(
            label      = "الحضور ($attendCount)",
            isSelected = selectedTab == ReportsAdminUiState.ReportTab.ATTENDANCE,
            onClick    = { onTabSelected(ReportsAdminUiState.ReportTab.ATTENDANCE) },
            modifier   = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ReportsTabButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor by animateColorAsState(
        targetValue   = if (isSelected) AppTheme.color.primary else AppTheme.color.bg,
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label         = "report_tab_bg",
    )
    val textColor by animateColorAsState(
        targetValue   = if (isSelected) AppTheme.color.bg else AppTheme.color.textSecondary,
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label         = "report_tab_text",
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text      = label,
            color     = textColor,
            style     = AppTheme.textStyle.label.large,
            textAlign = TextAlign.Center,
        )
    }
}

// ── Grades report card ────────────────────────────────────────────────────────

@Composable
private fun GradesReportCard(
    item: ReportsAdminUiState.GradesRowItem,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg)
            .border(1.dp, AppTheme.color.border.copy(alpha = .6f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Course name + code
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.Top,
        ) {
            Column(
                modifier            = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text  = item.code,
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.label.small,
                )
                Text(
                    text  = item.courseName,
                    color = AppTheme.color.text,
                    style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold),
                )
            }
            // Enrolled badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppTheme.color.primary.copy(alpha = .08f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text  = "${item.enrolled} طالب",
                    color = AppTheme.color.primary,
                    style = AppTheme.textStyle.label.small,
                )
            }
        }

        // Divider
        Box(Modifier.fillMaxWidth().height(1.dp).background(AppTheme.color.bgHover))

        // Grades stats row
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            GradeStatCell(
                value    = item.avgGrade?.let { "%.1f".format(it) } ?: "—",
                label    = "المتوسط",
                color    = AppTheme.color.primary,
                modifier = Modifier.weight(1f),
            )
            GradeStatCell(
                value    = item.maxGrade?.let { "%.1f".format(it) } ?: "—",
                label    = "الأعلى",
                color    = Color(0xFF16A34A),
                modifier = Modifier.weight(1f),
            )
            GradeStatCell(
                value    = item.minGrade?.let { "%.1f".format(it) } ?: "—",
                label    = "الأدنى",
                color    = Color(0xFFDC2626),
                modifier = Modifier.weight(1f),
            )
            GradeStatCell(
                value    = "${item.passed}",
                label    = "ناجح",
                color    = Color(0xFF16A34A),
                modifier = Modifier.weight(1f),
            )
            GradeStatCell(
                value    = "${item.failed}",
                label    = "راسب",
                color    = Color(0xFFDC2626),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun GradeStatCell(
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text      = value,
            color     = color,
            style     = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.ExtraBold),
            textAlign = TextAlign.Center,
        )
        Text(
            text      = label,
            color     = AppTheme.color.textSecondary,
            style     = AppTheme.textStyle.label.small.copy(fontSize = 10.sp),
            textAlign = TextAlign.Center,
        )
    }
}

// ── Attendance report card ────────────────────────────────────────────────────

@Composable
private fun AttendanceReportCard(
    item: ReportsAdminUiState.AttendanceRowItem,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg)
            .border(1.dp, AppTheme.color.border.copy(alpha = .6f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Student + meta row
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Column(
                modifier            = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text  = item.studentName,
                    color = AppTheme.color.text,
                    style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold),
                )
                val meta = listOfNotNull(
                    item.dept,
                    item.level?.let { "المستوى $it" },
                ).joinToString(" • ")
                if (meta.isNotBlank()) {
                    Text(
                        text  = meta,
                        color = AppTheme.color.textSecondary,
                        style = AppTheme.textStyle.label.small,
                    )
                }
            }
            // Attendance rate badge
            val rateColor = when {
                item.rate >= 80 -> Color(0xFF16A34A)
                item.rate >= 60 -> Color(0xFFF59E0B)
                else            -> Color(0xFFDC2626)
            }
            val rateBg = when {
                item.rate >= 80 -> Color(0xFFDCFCE7)
                item.rate >= 60 -> Color(0xFFFEF9C3)
                else            -> Color(0xFFFEE2E2)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(rateBg)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                Text(
                    text  = "${item.rate.roundToInt()}%",
                    color = rateColor,
                    style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold),
                )
            }
        }

        // Divider
        Box(Modifier.fillMaxWidth().height(1.dp).background(AppTheme.color.bgHover))

        // Session stats row
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AttendStatCell(
                value    = "${item.totalSessions}",
                label    = "إجمالي",
                color    = AppTheme.color.primary,
                modifier = Modifier.weight(1f),
            )
            AttendStatCell(
                value    = "${item.present}",
                label    = "حضر",
                color    = Color(0xFF16A34A),
                modifier = Modifier.weight(1f),
            )
            AttendStatCell(
                value    = "${item.absent}",
                label    = "غاب",
                color    = Color(0xFFDC2626),
                modifier = Modifier.weight(1f),
            )
        }

        // Attendance progress bar
        AttendanceProgressBar(rate = item.rate)
    }
}

@Composable
private fun AttendStatCell(
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(AppTheme.color.bgHover)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text      = value,
            color     = color,
            style     = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.ExtraBold),
            textAlign = TextAlign.Center,
        )
        Text(
            text      = label,
            color     = AppTheme.color.textSecondary,
            style     = AppTheme.textStyle.label.small.copy(fontSize = 10.sp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun AttendanceProgressBar(rate: Float, modifier: Modifier = Modifier) {
    val barColor = when {
        rate >= 80 -> Color(0xFF16A34A)
        rate >= 60 -> Color(0xFFF59E0B)
        else       -> Color(0xFFDC2626)
    }
    val progress = (rate / 100f).coerceIn(0f, 1f)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text  = "نسبة الحضور",
                color = AppTheme.color.textSecondary,
                style = AppTheme.textStyle.label.small.copy(fontSize = 10.sp),
            )
            Text(
                text  = "${rate.roundToInt()}%",
                color = barColor,
                style = AppTheme.textStyle.label.small.copy(
                    fontSize   = 10.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(AppTheme.color.bgHover),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(barColor),
            )
        }
    }
}

// ── Error state ───────────────────────────────────────────────────────────────

@Composable
private fun ReportsErrorState(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = message, color = AppTheme.color.textSecondary, style = AppTheme.textStyle.body.medium)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppTheme.color.primary)
                    .clickAnimation(onRetry)
                    .padding(horizontal = 24.dp, vertical = 12.dp),
            ) {
                Text(
                    text  = "إعادة المحاولة",
                    color = AppTheme.color.bg,
                    style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold),
                )
            }
        }
    }
}
