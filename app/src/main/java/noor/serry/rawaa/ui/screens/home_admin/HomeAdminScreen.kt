package noor.serry.rawaa.ui.screens.home_admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.navigation.admin.AdminBackStackProvider
import noor.serry.rawaa.ui.navigation.admin.AdminRouteKeys

// ─────────────────────────────────────────────────────────────────────────────
// Entry point
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun HomeAdminScreen(
    viewModel: HomeAdminViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    HandleEffects(effects = viewModel.effect)

    when {
        state.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppTheme.color.primary)
            }
        }

        state.errorMessage != null -> {
            AdminErrorState(
                message = state.errorMessage.orEmpty(),
                onRetry = viewModel::load,
            )
        }

        else -> {
            HomeAdminContent(state = state, listener = viewModel)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Root scrollable content
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HomeAdminContent(
    state: HomeAdminUiState,
    listener: HomeAdminInteractionListener,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.bgHover)
            .verticalScroll(rememberScrollState()),
    ) {

        // ── 1. Hero header ────────────────────────────────────────────────────
        AdminHomeHeader(state = state)

        Spacer(Modifier.height(24.dp))

        // ── 2. Quick actions ──────────────────────────────────────────────────
        QuickActionsSection(
            listener = listener,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(Modifier.height(24.dp))

        // ── 3. Overview stats ─────────────────────────────────────────────────
        OverviewSection(
            state    = state,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(Modifier.height(24.dp))

        // ── 4. Recent activities ──────────────────────────────────────────────
        if (state.recentRegistrations.isNotEmpty()) {
            RecentActivitiesSection(
                items     = state.recentRegistrations,
                onViewAll = listener::onViewAllActivitiesClick,
                modifier  = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(Modifier.height(24.dp))
        }

        Spacer(Modifier.height(16.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 1 ── Hero header
//
// Design shows:
//   • Shield icon in yellow box  — static icon, always shown
//   • "مرحباً، أ. <name>!"       — from UserDto.name (via getMe)
//   • "لوحة التحكم الإدارية"     — static label
//   • Chip 1: "إجمالي المستخدمين" / value = totalStudents + totalDoctors + totalEmployees
//   • Chip 2: "معدل النشاط"      — NOT in DTO; replaced with "المقررات النشطة" / activeCourses
//
// "معدل النشاط 87%" is shown in the design mockup but has NO corresponding DTO
// field.  Per the rules we must not display data that doesn't exist.
// activeCourses IS in AdminStatsDto and fills the second chip meaningfully.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AdminHomeHeader(
    state: HomeAdminUiState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(AppTheme.color.primary)
            .padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

            // Greeting row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                // Text block (RIGHT side in RTL)
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text      = "مرحباً، ${state.adminName}!",
                        color     = AppTheme.color.bg,
                        style     = AppTheme.textStyle.headline.medium,
                        textAlign = TextAlign.End,
                    )
                    Text(
                        text      = "لوحة التحكم الإدارية",
                        color     = AppTheme.color.bg.copy(alpha = .75f),
                        style     = AppTheme.textStyle.body.small.copy(
                            fontWeight = FontWeight.Normal,
                        ),
                        textAlign = TextAlign.End,
                    )
                }

                // Shield icon box (LEFT side in RTL)
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AppTheme.color.secondary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter  = painterResource(R.drawable.shield),
                        tint     = AppTheme.color.primary,
                        modifier = Modifier.size(26.dp),
                    )
                }
            }

            // Two stat chips
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Chip A — إجمالي المستخدمين (totalStudents + totalDoctors + totalEmployees)
                HeaderStatChip(
                    label    = "إجمالي المستخدمين",
                    value    = formatNumber(state.totalUsers),
                    modifier = Modifier.weight(1f),
                )
                // Chip B — المقررات النشطة (activeCourses from AdminStatsDto)
                // Design shows "معدل النشاط" but that field is NOT in the DTO.
                // activeCourses is the closest real value and fills this chip.
                HeaderStatChip(
                    label    = "المقررات النشطة",
                    value    = formatNumber(state.activeCourses),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun HeaderStatChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(AppTheme.color.bg.copy(alpha = .12f))
            .padding(horizontal = 14.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text      = label,
            color     = AppTheme.color.bg.copy(alpha = .80f),
            style     = AppTheme.textStyle.label.small,
            textAlign = TextAlign.End,
        )
        Text(
            text      = value,
            color     = AppTheme.color.bg,
            style     = AppTheme.textStyle.headline.small.copy(fontWeight = FontWeight.ExtraBold),
            textAlign = TextAlign.End,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 2 ── Quick actions  (2 × 2 grid)
//
// All four buttons exist because the backend has corresponding endpoints:
//   • إضافة مقرر    → POST /api/courses       (CourseRequest)
//   • إضافة مستخدم  → POST /api/students / /api/employees
//   • التقارير      → GET  /api/admin/reports/grades|attendance
//   • الإعدادات     → navigates to settings tab (profile / change-password)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun QuickActionsSection(
    listener: HomeAdminInteractionListener,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionTitle(text = "إجراءات سريعة")

        // Row 1 — primary actions
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            QuickActionCard(
                label      = "إضافة مقرر",
                iconRes    = R.drawable.ic_book,
                background = AppTheme.color.secondary,
                iconTint   = AppTheme.color.primary,
                textColor  = AppTheme.color.primary,
                modifier   = Modifier.weight(1f),
                onClick    = listener::onAddCourseClick,
            )
            QuickActionCard(
                label      = "إضافة مستخدم",
                iconRes    = R.drawable.person,
                background = AppTheme.color.primary,
                iconTint   = AppTheme.color.bg,
                textColor  = AppTheme.color.bg,
                modifier   = Modifier.weight(1f),
                onClick    = listener::onAddUserClick,
            )
        }

        // Row 2 — secondary actions
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            QuickActionCard(
                label      = "التقارير",
                iconRes    = R.drawable.badge,
                background = AppTheme.color.bg,
                iconTint   = AppTheme.color.textSecondary,
                textColor  = AppTheme.color.text,
                modifier   = Modifier.weight(1f),
                onClick    = listener::onReportsClick,
            )
            QuickActionCard(
                label      = "الإعدادات",
                iconRes    = R.drawable.shield,
                background = AppTheme.color.bg,
                iconTint   = AppTheme.color.textSecondary,
                textColor  = AppTheme.color.text,
                modifier   = Modifier.weight(1f),
                onClick    = listener::onSettingsClick,
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    label: String,
    iconRes: Int,
    background: Color,
    iconTint: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .border(
                width = 1.dp,
                color = AppTheme.color.border.copy(alpha = .5f),
                shape = RoundedCornerShape(16.dp),
            )
            .clickAnimation(onClick)
            .padding(vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            painter  = painterResource(iconRes),
            tint     = iconTint,
            modifier = Modifier.size(28.dp),
        )
        Text(
            text  = label,
            color = textColor,
            style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Medium),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 3 ── Overview  (4 stat cards)
//
// Design shows growth arrows (+12%, +5% …) — those percentages have NO
// corresponding field in AdminStatsDto, so we do NOT display them.
// We only display: label + value from AdminStatsDto fields.
//
// Card 1 — إجمالي الطلاب       → stats.totalStudents
// Card 2 — أعضاء هيئة التدريس  → stats.totalDoctors
// Card 3 — المقررات النشطة     → stats.activeCourses
// Card 4 — معدل النجاح          → stats.activeRegistrations
//             (The "معدل النجاح" label is in the design; the closest real field
//             is activeRegistrations. A label that reflects the data is used.)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun OverviewSection(
    state: HomeAdminUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionTitle(text = "نظرة عامة")

        OverviewStatCard(
            value    = formatNumber(state.totalStudents),
            label    = "إجمالي الطلاب",
            iconRes  = R.drawable.person,
            iconBg   = AppTheme.color.secondary,
            iconTint = AppTheme.color.primary,
        )
        OverviewStatCard(
            value    = formatNumber(state.totalDoctors),
            label    = "أعضاء هيئة التدريس",
            iconRes  = R.drawable.google,           // graduation icon
            iconBg   = Color(0xFFFEF9C3),
            iconTint = Color(0xFFF59E0B),
        )
        OverviewStatCard(
            value    = formatNumber(state.activeCourses),
            label    = "المقررات النشطة",
            iconRes  = R.drawable.ic_book,
            iconBg   = AppTheme.color.primary.copy(alpha = .08f),
            iconTint = AppTheme.color.primary,
        )
        OverviewStatCard(
            value    = formatNumber(state.activeRegistrations),
            label    = "التسجيلات النشطة",
            iconRes  = R.drawable.badge,
            iconBg   = Color(0xFFDCFCE7),
            iconTint = Color(0xFF16A34A),
        )
    }
}

@Composable
private fun OverviewStatCard(
    value: String,
    label: String,
    iconRes: Int,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg)
            .border(
                width = 1.dp,
                color = AppTheme.color.border.copy(alpha = .6f),
                shape = RoundedCornerShape(16.dp),
            )
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Icon box (left in RTL = visual right)
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter  = painterResource(iconRes),
                tint     = iconTint,
                modifier = Modifier.size(22.dp),
            )
        }

        // Text block (right in RTL = visual left)
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text      = label,
                color     = AppTheme.color.textSecondary,
                style     = AppTheme.textStyle.label.medium,
                textAlign = TextAlign.End,
            )
            Text(
                text      = value,
                color     = AppTheme.color.text,
                style     = AppTheme.textStyle.headline.small.copy(fontWeight = FontWeight.ExtraBold),
                textAlign = TextAlign.End,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 4 ── Recent activities
//
// Design shows three activity rows with different icons (student, course, exam).
// All rows come from AdminDashboardDto.recentRegistrations (RecentRegistrationDto).
// There is only one type of recent activity in the DTO, so we use a single
// consistent icon (person/student).  No fabricated activity types.
//
// "عرض الكل" button navigates to the users tab (closest available list screen).
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun RecentActivitiesSection(
    items: List<HomeAdminUiState.RecentRegistrationItem>,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Section header row with "عرض الكل"
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            // "عرض الكل" on the left (RTL layout)
            Text(
                text    = "عرض الكل",
                color   = AppTheme.color.primary,
                style   = AppTheme.textStyle.label.medium.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.clickAnimation(onViewAll),
            )
            // Title on the right (RTL layout)
            SectionTitle(text = "النشاطات الأخيرة")
        }

        items.forEach { item ->
            RecentActivityCard(item = item)
        }
    }
}

@Composable
private fun RecentActivityCard(
    item: HomeAdminUiState.RecentRegistrationItem,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppTheme.color.bg)
            .border(
                width = 1.dp,
                color = AppTheme.color.border.copy(alpha = .5f),
                shape = RoundedCornerShape(14.dp),
            )
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Text block — RIGHT side in RTL
        Column(
            modifier            = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalAlignment = Alignment.End,
        ) {
            // Student name as the activity title
            Text(
                text      = item.studentName,
                color     = AppTheme.color.text,
                style     = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.End,
            )
            // Course name + code
            Text(
                text      = "${item.courseName} - ${item.courseCode}",
                color     = AppTheme.color.textSecondary,
                style     = AppTheme.textStyle.label.small,
                textAlign = TextAlign.End,
            )
            // Registration timestamp
            Text(
                text      = item.registeredAt,
                color     = AppTheme.color.textSecondary,
                style     = AppTheme.textStyle.label.small.copy(fontSize = 10.sp),
                textAlign = TextAlign.End,
            )
        }

        // Icon — LEFT side in RTL
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AppTheme.color.primary.copy(alpha = .08f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter  = painterResource(R.drawable.person),
                tint     = AppTheme.color.primary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared helpers
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text      = text,
        modifier  = modifier,
        color     = AppTheme.color.text,
        style     = AppTheme.textStyle.body.large.copy(fontWeight = FontWeight.Bold),
        textAlign = TextAlign.End,
    )
}

@Composable
private fun AdminErrorState(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text  = message,
                color = AppTheme.color.textSecondary,
                style = AppTheme.textStyle.body.medium,
            )
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

/** Formats a raw Int as a locale-style string: 12800 → "12,800" */
private fun formatNumber(value: Int): String =
    "%,d".format(value)

// ─────────────────────────────────────────────────────────────────────────────
// Effect handler
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HandleEffects(effects: Flow<HomeAdminEffect>) {
    val backStack = AdminBackStackProvider.current

    LaunchedEffect(Unit) {
        effects.collectLatest { effect ->
            when (effect) {
                HomeAdminEffect.NavigateToUsers,
                HomeAdminEffect.NavigateToUsers2  -> backStack.add(AdminRouteKeys.Users)
                HomeAdminEffect.NavigateToCourses -> backStack.add(AdminRouteKeys.Courses)
                HomeAdminEffect.NavigateToReports -> backStack.add(AdminRouteKeys.Reports)
                HomeAdminEffect.NavigateToSettings -> backStack.add(AdminRouteKeys.Settings)
            }
        }
    }
}
