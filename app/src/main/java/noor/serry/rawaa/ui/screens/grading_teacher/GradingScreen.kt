package noor.serry.rawaa.ui.screens.grading_teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.screens.grading_teacher.components.CourseGradingCard
import noor.serry.rawaa.ui.screens.grading_teacher.components.GradingSummaryCard
import org.koin.androidx.compose.koinViewModel

// ── Entry point ───────────────────────────────────────────────────────────────
// Mirrors ProfileTeacherScreen: early-return on loading/error,
// collect from viewModel.state (BaseViewModel exposes `state` not `uiState`)

@Composable
fun GradingScreen(
    viewModel: GradingViewModel = koinViewModel(),
) {
    val state  by viewModel.state.collectAsState()
    val colors = AppTheme.color

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = colors.primary)
        }
        return
    }

    if (state.error != null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text  = state.error.orEmpty(),
                    style = AppTheme.textStyle.body.medium,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.primary)
                        .clickable { viewModel.onRetry() }
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                ) {
                    Text(
                        text  = "إعادة المحاولة",
                        style = AppTheme.textStyle.body.medium,
                        color = colors.bg,
                    )
                }
            }
        }
        return
    }

    GradingContent(state = state, listener = viewModel)
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun GradingContent(
    state: GradingUiState,
    listener: GradingInteractionListener,
) {
    val colors    = AppTheme.color
    val textStyle = AppTheme.textStyle

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgHover)
    ) {

        // ── Dark header ───────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.primary)
                .padding(horizontal = 20.dp, vertical = 28.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text      = "التصحيح والتقييم",
                    style     = textStyle.headline.small,
                    color     = colors.bg,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text      = "تصحيح وتقييم أعمال الطلاب",
                    style     = textStyle.label.large,
                    color     = colors.bgSecondary,
                    textAlign = TextAlign.Center,
                )
            }
        }

        LazyColumn(
            modifier            = Modifier.fillMaxSize(),
            contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {

            // ── Summary cards ─────────────────────────────────────────
            // Source: DoctorDashboardDto.totalStudents / totalCourses
            item {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    GradingSummaryCard(
                        count      = state.totalStudents,
                        label      = "إجمالي الطلاب",
                        subLabel   = "طالب",
                        iconResId  = R.drawable.ic_person,
                        iconTint   = colors.success,
                        iconBg     = colors.successBg,
                        countColor = colors.success,
                        modifier   = Modifier.weight(1f),
                    )
                    GradingSummaryCard(
                        count      = state.totalCourses,
                        label      = "إجمالي المقررات",
                        subLabel   = "مقرر",
                        iconResId  = R.drawable.ic_clock,
                        iconTint   = colors.warning,
                        iconBg     = colors.warningBg,
                        countColor = colors.warning,
                        modifier   = Modifier.weight(1f),
                    )
                }
            }

            // ── Search + Filter row ───────────────────────────────────
            item {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    // Filter icon — decorative; no filter endpoint in API
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.bg)
                            .border(1.dp, colors.border, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter            = painterResource(R.drawable.filter),
                            contentDescription = null,
                            tint               = colors.textSecondary,
                            modifier           = Modifier.size(20.dp),
                        )
                    }

                    // Search — client-side filter on name / code / department
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.bg)
                            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                        ) {
                            BasicTextField(
                                value         = state.searchQuery,
                                onValueChange = listener::onSearchChange,
                                textStyle     = textStyle.body.small.copy(
                                    color     = colors.text,
                                    textAlign = TextAlign.End,
                                ),
                                modifier      = Modifier
                                    .weight(1f)
                                    .padding(vertical = 14.dp),
                                decorationBox = { innerTextField ->
                                    Box(contentAlignment = Alignment.CenterEnd) {
                                        if (state.searchQuery.isEmpty()) {
                                            Text(
                                                text      = "بحث عن مقرر...",
                                                style     = textStyle.body.small,
                                                color     = colors.textSecondary,
                                                textAlign = TextAlign.End,
                                            )
                                        }
                                        innerTextField()
                                    }
                                },
                            )
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                painter            = painterResource(R.drawable.search),
                                contentDescription = null,
                                tint               = colors.textSecondary,
                                modifier           = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            }

            // ── Tabs ──────────────────────────────────────────────────
            // Client-side split on CourseDto.isActive
            item {
                val activeCount   = state.allCourses.count {  it.isActive }
                val inactiveCount = state.allCourses.count { !it.isActive }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.bg)
                        .border(1.dp, colors.border, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    GradingTabItem(
                        label    = "نشطة ($activeCount)",
                        selected = state.selectedTab == GradingTab.ACTIVE,
                        onClick  = { listener.onTabSelected(GradingTab.ACTIVE) },
                        modifier = Modifier.weight(1f),
                    )
                    GradingTabItem(
                        label    = "غير نشطة ($inactiveCount)",
                        selected = state.selectedTab == GradingTab.INACTIVE,
                        onClick  = { listener.onTabSelected(GradingTab.INACTIVE) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // ── Course list ───────────────────────────────────────────
            if (state.filteredCourses.isEmpty()) {
                item {
                    Text(
                        text      = "لا توجد مقررات",
                        style     = textStyle.body.medium,
                        color     = colors.textSecondary,
                        modifier  = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                items(
                    items = state.filteredCourses,
                    key   = { it.id },
                ) { course ->
                    CourseGradingCard(course = course)
                }
            }
        }
    }
}

// ── Tab item ──────────────────────────────────────────────────────────────────

@Composable
private fun GradingTabItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors    = AppTheme.color
    val textStyle = AppTheme.textStyle

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) colors.primary else colors.bg)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text      = label,
            style     = textStyle.label.large,
            color     = if (selected) colors.bg else colors.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}
