package noor.serry.rawaa.ui.screens.courses_teacher

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
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.data.dto.CourseDto
import org.koin.androidx.compose.koinViewModel

// ── Entry point ───────────────────────────────────────────────────────────────

@Composable
fun CoursesTeacherScreen(
    viewModel: CoursesTeacherViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AppTheme.color.primary)
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
                    text  = state.error ?: "",
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.body.medium,
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppTheme.color.primary)
                        .clickAnimation { viewModel.loadCourses() }
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
        return
    }

    CoursesTeacherContent(
        state         = state,
        onTabSelected = viewModel::selectTab,
        onViewDetails = viewModel::onCourseClicked,
    )
}

// ── Root content ──────────────────────────────────────────────────────────────

@Composable
private fun CoursesTeacherContent(
    state: CoursesTeacherUiState,
    onTabSelected: (CourseTab) -> Unit,
    onViewDetails: (Int) -> Unit,
) {
    val displayedCourses = if (state.selectedTab == CourseTab.ACTIVE)
        state.activeCourses else state.archivedCourses

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.bgHover),
    ) {

        // 1 ── Hero header (gradient strip + stats)
        item {
            CoursesTeacherHero(state = state)
        }

        // 2 ── Tab selector
        item {
            CoursesTabRow(
                state         = state,
                onTabSelected = onTabSelected,
                modifier      = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }

        // 3 ── Empty state
        if (displayedCourses.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text  = "لا توجد مقررات",
                        color = AppTheme.color.textSecondary,
                        style = AppTheme.textStyle.body.medium,
                    )
                }
            }
        }

        // 4 ── Course cards
        items(displayedCourses) { course ->
            CourseTeacherCard(
                course        = course,
                onViewDetails = { onViewDetails(course.id) },
                modifier      = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp),
            )
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

// ── Hero ──────────────────────────────────────────────────────────────────────

@Composable
private fun CoursesTeacherHero(
    state: CoursesTeacherUiState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                verticalGradient(listOf(AppTheme.color.primary, AppTheme.color.primaryLight))
            )
            .padding(horizontal = 24.dp, vertical = 28.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            // Title block
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text  = "مقرراتي",
                    color = AppTheme.color.bg,
                    style = AppTheme.textStyle.headline.small,
                )
                Text(
                    text  = "إدارة وتتبع المقررات الدراسية",
                    color = AppTheme.color.bg.copy(alpha = .75f),
                    style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
                )
            }

            // Stats row
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier              = Modifier.fillMaxWidth(),
            ) {
                HeroStatCard(
                    value      = "${state.totalStudents}",
                    label      = "طالب",
                    valueColor = Color(0xFFF59E0B),
                    modifier   = Modifier.weight(1f),
                )
                HeroStatCard(
                    value      = "${state.activeCourses.size}",
                    label      = "مقرر نشط",
                    valueColor = AppTheme.color.text,
                    modifier   = Modifier.weight(1f),
                )
                HeroStatCard(
                    value      = "${state.archivedCourses.size}",
                    label      = "مؤرشفة",
                    valueColor = AppTheme.color.text,
                    modifier   = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun HeroStatCard(
    value: String,
    label: String,
    valueColor: Color = AppTheme.color.primary,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(AppTheme.color.bg)
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text      = value,
            color     = valueColor,
            style     = AppTheme.textStyle.headline.small.copy(fontWeight = FontWeight.ExtraBold),
            textAlign = TextAlign.Center,
        )
        Text(
            text      = label,
            color     = AppTheme.color.textSecondary,
            style     = AppTheme.textStyle.label.small.copy(fontSize = 11.sp),
            textAlign = TextAlign.Center,
        )
    }
}

// ── Tab selector ──────────────────────────────────────────────────────────────

@Composable
private fun CoursesTabRow(
    state: CoursesTeacherUiState,
    onTabSelected: (CourseTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.color

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.bg)
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        CourseTabButton(
            label      = "المقررات النشطة (${state.activeCourses.size})",
            isSelected = state.selectedTab == CourseTab.ACTIVE,
            onClick    = { onTabSelected(CourseTab.ACTIVE) },
            modifier   = Modifier.weight(1f),
        )
        CourseTabButton(
            label      = "الأرشيف (${state.archivedCourses.size})",
            isSelected = state.selectedTab == CourseTab.ARCHIVED,
            onClick    = { onTabSelected(CourseTab.ARCHIVED) },
            modifier   = Modifier.weight(1f),
        )
    }
}

@Composable
private fun CourseTabButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors    = AppTheme.color
    val textStyle = AppTheme.textStyle

    val bgColor by animateColorAsState(
        targetValue   = if (isSelected) colors.primary else colors.bg,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label         = "tab_bg",
    )
    val textColor by animateColorAsState(
        targetValue   = if (isSelected) colors.bg else colors.textSecondary,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label         = "tab_text",
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
            style     = textStyle.label.large,
            color     = textColor,
            textAlign = TextAlign.Center,
        )
    }
}

// ── Course card ───────────────────────────────────────────────────────────────

@Composable
fun CourseTeacherCard(
    course: CourseDto,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg)
            .border(1.dp, AppTheme.color.border.copy(alpha = .7f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // ── Title row ──────────────────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppTheme.color.primary.copy(alpha = .10f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter  = painterResource(R.drawable.ic_book),
                    tint     = AppTheme.color.primary,
                    modifier = Modifier.size(20.dp),
                )
            }

            Spacer(Modifier.width(10.dp))

            Column(
                modifier            = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text  = course.code,
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.label.small,
                )
                Text(
                    text  = course.name,
                    color = AppTheme.color.text,
                    style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold),
                )
                val semesterLabel = buildSemesterLabel(course.semester, course.academicYear)
                if (semesterLabel != null) {
                    Text(
                        text  = semesterLabel,
                        color = AppTheme.color.textSecondary,
                        style = AppTheme.textStyle.label.small,
                    )
                }
            }

            if (course.departmentName != null) {
                Box(
                    modifier = Modifier
                        .background(
                            AppTheme.color.primary.copy(alpha = .08f),
                            RoundedCornerShape(20.dp),
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text  = course.departmentName,
                        color = AppTheme.color.primary,
                        style = AppTheme.textStyle.label.small.copy(fontWeight = FontWeight.Medium),
                    )
                }
            }
        }

        // ── Divider ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(AppTheme.color.bgHover),
        )

        // ── Info badges row ────────────────────────────────────────────────────
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier              = Modifier.fillMaxWidth(),
        ) {
            CourseInfoBadge(
                value    = "${course.enrolledCount ?: 0}",
                label    = "طالب",
                iconRes  = R.drawable.ic_person,
                iconTint = Color(0xFF3B82F6),
                iconBg   = Color(0xFFDBEAFE),
                modifier = Modifier.weight(1f),
            )
            CourseInfoBadge(
                value    = "${course.creditHours}",
                label    = "ساعة",
                iconRes  = R.drawable.ic_calendar,
                iconTint = Color(0xFFF59E0B),
                iconBg   = Color(0xFFFEF9C3),
                modifier = Modifier.weight(1f),
            )
            CourseInfoBadge(
                value    = "${course.maxStudents}",
                label    = "الحد الأقصى",
                iconRes  = R.drawable.ic_person,
                iconTint = Color(0xFF16A34A),
                iconBg   = Color(0xFFDCFCE7),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// ── Info badge ────────────────────────────────────────────────────────────────

@Composable
private fun CourseInfoBadge(
    value: String,
    label: String,
    iconRes: Int,
    iconTint: Color,
    iconBg: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(AppTheme.color.bgHover)
            .border(1.dp, AppTheme.color.border.copy(alpha = .5f), RoundedCornerShape(10.dp))
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter  = painterResource(iconRes),
                tint     = iconTint,
                modifier = Modifier.size(14.dp),
            )
        }
        Text(
            text      = value,
            color     = AppTheme.color.text,
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

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun buildSemesterLabel(semester: String?, academicYear: Int?): String? {
    if (semester == null && academicYear == null) return null
    return listOfNotNull(semester, academicYear?.toString()).joinToString(" ")
}