package noor.serry.rawaa.ui.screens.courses_admin

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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

// ── Entry point ───────────────────────────────────────────────────────────────

@Composable
fun CoursesAdminScreen(
    viewModel: CoursesAdminViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    when {
        state.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppTheme.color.primary)
            }
        }
        state.errorMessage != null -> {
            CoursesAdminError(message = state.errorMessage ?: "", onRetry = viewModel::load)
        }
        else -> {
            CoursesAdminContent(state = state, listener = viewModel)
        }
    }
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun CoursesAdminContent(
    state: CoursesAdminUiState,
    listener: CoursesAdminInteractionListener,
) {
    val displayList = if (state.selectedTab == CoursesAdminUiState.CourseAdminTab.ACTIVE)
        state.activeCourses else state.inactiveCourses

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.bgHover),
    ) {
        // 1 ── Hero header
        item {
            CoursesAdminHero(state = state)
        }

        // 2 ── Tab row
        item {
            CoursesAdminTabRow(
                state       = state,
                onTabSelect = listener::onTabSelected,
                modifier    = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            )
        }

        // 3 ── Department filter chips
        if (state.departments.isNotEmpty()) {
            item {
                CourseDepartmentFilter(
                    departments  = state.departments,
                    selectedId   = state.selectedDepartmentId,
                    onSelected   = listener::onDepartmentFilterSelected,
                    modifier     = Modifier.padding(bottom = 10.dp),
                )
            }
        }

        // 4 ── Empty state
        if (displayList.isEmpty()) {
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

        // 5 ── Course cards
        items(displayList, key = { it.id }) { course ->
            CourseAdminCard(
                item    = course,
                onClick = { listener.onCourseClicked(course.id) },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp),
            )
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

// ── Hero header ───────────────────────────────────────────────────────────────

@Composable
private fun CoursesAdminHero(
    state: CoursesAdminUiState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(verticalGradient(listOf(AppTheme.color.primary, AppTheme.color.primaryLight)))
            .padding(horizontal = 24.dp, vertical = 28.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text  = "المقررات الدراسية",
                    color = AppTheme.color.bg,
                    style = AppTheme.textStyle.headline.small,
                )
                Text(
                    text  = "إدارة وتتبع المقررات",
                    color = AppTheme.color.bg.copy(alpha = .75f),
                    style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier              = Modifier.fillMaxWidth(),
            ) {
                CoursesHeroStatCard(
                    value    = "${state.activeCourses.size}",
                    label    = "نشط",
                    modifier = Modifier.weight(1f),
                )
                CoursesHeroStatCard(
                    value    = "${state.inactiveCourses.size}",
                    label    = "غير نشط",
                    modifier = Modifier.weight(1f),
                )
                CoursesHeroStatCard(
                    value    = "${state.activeCourses.size + state.inactiveCourses.size}",
                    label    = "الإجمالي",
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun CoursesHeroStatCard(value: String, label: String, modifier: Modifier = Modifier) {
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
            color     = AppTheme.color.primary,
            style     = AppTheme.textStyle.headline.small.copy(fontWeight = FontWeight.ExtraBold),
            textAlign = TextAlign.Center,
        )
        Text(
            text      = label,
            color     = AppTheme.color.textSecondary,
            style     = AppTheme.textStyle.label.small.copy(fontWeight = FontWeight.Medium),
            textAlign = TextAlign.Center,
        )
    }
}

// ── Tab row ───────────────────────────────────────────────────────────────────

@Composable
private fun CoursesAdminTabRow(
    state: CoursesAdminUiState,
    onTabSelect: (CoursesAdminUiState.CourseAdminTab) -> Unit,
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
        CoursesAdminTabButton(
            label      = "النشطة (${state.activeCourses.size})",
            isSelected = state.selectedTab == CoursesAdminUiState.CourseAdminTab.ACTIVE,
            onClick    = { onTabSelect(CoursesAdminUiState.CourseAdminTab.ACTIVE) },
            modifier   = Modifier.weight(1f),
        )
        CoursesAdminTabButton(
            label      = "غير النشطة (${state.inactiveCourses.size})",
            isSelected = state.selectedTab == CoursesAdminUiState.CourseAdminTab.INACTIVE,
            onClick    = { onTabSelect(CoursesAdminUiState.CourseAdminTab.INACTIVE) },
            modifier   = Modifier.weight(1f),
        )
    }
}

@Composable
private fun CoursesAdminTabButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor by animateColorAsState(
        targetValue   = if (isSelected) AppTheme.color.primary else AppTheme.color.bg,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label         = "course_admin_tab_bg",
    )
    val textColor by animateColorAsState(
        targetValue   = if (isSelected) AppTheme.color.bg else AppTheme.color.textSecondary,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label         = "course_admin_tab_text",
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

// ── Department filter ─────────────────────────────────────────────────────────

@Composable
private fun CourseDepartmentFilter(
    departments: List<CoursesAdminUiState.DeptFilterItem>,
    selectedId: Int?,
    onSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier              = modifier,
        contentPadding        = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            CourseFilterChip(label = "الكل", isSelected = selectedId == null, onClick = { onSelected(null) })
        }
        items(departments, key = { it.id }) { dept ->
            CourseFilterChip(
                label      = dept.name,
                isSelected = selectedId == dept.id,
                onClick    = { onSelected(dept.id) },
            )
        }
    }
}

@Composable
private fun CourseFilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val bg   = if (isSelected) AppTheme.color.primary else AppTheme.color.bg
    val text = if (isSelected) AppTheme.color.bg else AppTheme.color.textSecondary
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(1.dp, AppTheme.color.border.copy(alpha = .6f), RoundedCornerShape(20.dp))
            .clickAnimation(onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, color = text, style = AppTheme.textStyle.label.medium)
    }
}

// ── Course card ───────────────────────────────────────────────────────────────

@Composable
private fun CourseAdminCard(
    item: CoursesAdminUiState.CourseAdminItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg)
            .border(1.dp, AppTheme.color.border.copy(alpha = .7f), RoundedCornerShape(16.dp))
            .clickAnimation(onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Title row
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
                    text  = item.code,
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.label.small,
                )
                Text(
                    text  = item.name,
                    color = AppTheme.color.text,
                    style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold),
                )
                val semesterLabel = buildSemesterLabel(item.semester, item.academicYear)
                if (semesterLabel != null) {
                    Text(
                        text  = semesterLabel,
                        color = AppTheme.color.textSecondary,
                        style = AppTheme.textStyle.label.small,
                    )
                }
            }

            if (item.departmentName != null) {
                Box(
                    modifier = Modifier
                        .background(
                            AppTheme.color.primary.copy(alpha = .08f),
                            RoundedCornerShape(20.dp),
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text  = item.departmentName,
                        color = AppTheme.color.primary,
                        style = AppTheme.textStyle.label.small.copy(fontWeight = FontWeight.Medium),
                    )
                }
            }
        }

        // Doctor row (if assigned)
        if (item.doctorName != null) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    painter  = painterResource(R.drawable.ic_person),
                    tint     = AppTheme.color.textSecondary,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text  = item.doctorName,
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.label.small,
                )
            }
        }

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(AppTheme.color.bgHover),
        )

        // Stats badges row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier              = Modifier.fillMaxWidth(),
        ) {
            CourseAdminBadge(
                value    = "${item.enrolledCount}",
                label    = "طالب",
                iconRes  = R.drawable.ic_person,
                iconTint = Color(0xFF3B82F6),
                iconBg   = Color(0xFFDBEAFE),
                modifier = Modifier.weight(1f),
            )
            CourseAdminBadge(
                value    = "${item.creditHours}",
                label    = "ساعة",
                iconRes  = R.drawable.ic_calendar,
                iconTint = Color(0xFFF59E0B),
                iconBg   = Color(0xFFFEF9C3),
                modifier = Modifier.weight(1f),
            )
            CourseAdminBadge(
                value    = "${item.maxStudents}",
                label    = "الحد الأقصى",
                iconRes  = R.drawable.ic_person,
                iconTint = Color(0xFF16A34A),
                iconBg   = Color(0xFFDCFCE7),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun CourseAdminBadge(
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

// ── Error state ───────────────────────────────────────────────────────────────

@Composable
private fun CoursesAdminError(message: String, onRetry: () -> Unit) {
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

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun buildSemesterLabel(semester: String?, academicYear: Int?): String? {
    if (semester == null && academicYear == null) return null
    return listOfNotNull(semester, academicYear?.toString()).joinToString(" ")
}
