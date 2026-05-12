package noor.serry.rawaa.ui.screens.home_super_admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
fun HomeSuperAdminScreen(
    viewModel: HomeSuperAdminViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val backstack = AdminBackStackProvider.current

    HandleEffects(
        effects                    = viewModel.effect,
        onNavigateToUniversities   = {
            backstack.add(AdminRouteKeys.University)
        },
        onNavigateToCreateUniversity = {},
        onNavigateToSettings       = {},
    )

    when {
        state.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppTheme.color.primary)
            }
        }

        state.errorMessage != null -> {
            SuperAdminErrorState(
                message = state.errorMessage.orEmpty(),
                onRetry = viewModel::onRetry,
            )
        }

        else -> {
            HomeSuperAdminContent(state = state, listener = viewModel)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Root scrollable content
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HomeSuperAdminContent(
    state: HomeSuperAdminUiState,
    listener: HomeSuperAdminInteractionListener,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.bgHover)
            .verticalScroll(rememberScrollState()),
    ) {
        // ── 1. Hero header ────────────────────────────────────────────────────
        SuperAdminHomeHeader(state = state)

        Spacer(Modifier.height(24.dp))

        // ── 2. Platform stats grid ────────────────────────────────────────────
        PlatformStatsSection(
            state    = state,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(Modifier.height(24.dp))

        // ── 3. Plan distribution ──────────────────────────────────────────────
        PlanDistributionSection(
            state    = state,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(Modifier.height(24.dp))

        // ── 4. Universities preview (up to 5 items) ───────────────────────────
        UniversitiesSection(
            state    = state,
            listener = listener,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Spacer(Modifier.height(32.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 1 ── Hero header
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SuperAdminHomeHeader(
    state: HomeSuperAdminUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AppTheme.color.primary,
                        AppTheme.color.primaryLight,
                    )
                ),
                shape = RoundedCornerShape(
                    bottomStart = 24.dp,
                    bottomEnd   = 24.dp,
                )
            )
            .padding(24.dp),
    ) {
        // Greeting row
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text  = "مرحباً، ${state.adminName}!",
                    color = AppTheme.color.bg,
                    style = AppTheme.textStyle.headline.small,
                )
                Text(
                    text  = "لوحة تحكم المنصة",
                    color = AppTheme.color.bg.copy(alpha = 0.8f),
                    style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Normal),
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppTheme.color.secondary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter           = painterResource(R.drawable.shield),
                    tint              = AppTheme.color.primary,
                    modifier          = Modifier.size(24.dp),
                    contentDescription = null,
                )
            }
        }

        // Stat chips
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SuperHeaderStatCard(
                value    = state.totalUniversities.toString(),
                label    = "إجمالي\nالجامعات",
                modifier = Modifier.weight(1f),
            )
            SuperHeaderStatCard(
                value    = state.activeUniversities.toString(),
                label    = "جامعة\nنشطة",
                modifier = Modifier.weight(1f),
            )
            SuperHeaderStatCard(
                value    = formatNumber(state.totalUsers),
                label    = "إجمالي\nالمستخدمين",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SuperHeaderStatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg.copy(alpha = .1f))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text  = value,
            color = AppTheme.color.bg,
            style = AppTheme.textStyle.headline.small,
        )
        Text(
            text      = label,
            color     = AppTheme.color.bg.copy(alpha = 0.8f),
            style     = AppTheme.textStyle.label.medium,
            modifier  = Modifier.padding(top = 4.dp),
            minLines  = 2,
            textAlign = TextAlign.Center,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 2 ── Platform stats grid
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PlatformStatsSection(
    state: HomeSuperAdminUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SuperSectionTitle(text = "إحصائيات المنصة")

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PlatformStatCard(
                value    = formatNumber(state.totalStudents),
                label    = "الطلاب",
                iconRes  = R.drawable.person,
                iconBg   = AppTheme.color.secondary,
                iconTint = AppTheme.color.primary,
                modifier = Modifier.weight(1f),
            )
            PlatformStatCard(
                value    = formatNumber(state.totalDoctors),
                label    = "أعضاء هيئة التدريس",
                iconRes  = R.drawable.badge,
                iconBg   = Color(0xFFFEF9C3),
                iconTint = Color(0xFFF59E0B),
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PlatformStatCard(
                value    = formatNumber(state.totalEmployees),
                label    = "الموظفون",
                iconRes  = R.drawable.google,
                iconBg   = Color(0xFFDCFCE7),
                iconTint = Color(0xFF16A34A),
                modifier = Modifier.weight(1f),
            )
            PlatformStatCard(
                value    = "${state.activeUniversities} / ${state.totalUniversities}",
                label    = "جامعات نشطة",
                iconRes  = R.drawable.shield,
                iconBg   = AppTheme.color.primary.copy(alpha = .08f),
                iconTint = AppTheme.color.primary,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun PlatformStatCard(
    value: String,
    label: String,
    iconRes: Int,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg)
            .border(
                width = 1.dp,
                color = AppTheme.color.border.copy(alpha = .6f),
                shape = RoundedCornerShape(16.dp),
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.End,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter  = painterResource(iconRes),
                tint     = iconTint,
                modifier = Modifier.size(20.dp),
            )
        }

        Text(
            text      = value,
            color     = AppTheme.color.text,
            style     = AppTheme.textStyle.headline.small.copy(fontWeight = FontWeight.ExtraBold),
            textAlign = TextAlign.End,
        )
        Text(
            text      = label,
            color     = AppTheme.color.textSecondary,
            style     = AppTheme.textStyle.label.small,
            textAlign = TextAlign.End,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 3 ── Plan distribution
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PlanDistributionSection(
    state: HomeSuperAdminUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SuperSectionTitle(text = "توزيع الخطط")

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PlanBadge(
                label     = "تجريبي",
                count     = state.planTrial,
                badgeBg   = Color(0xFFF3F4F6),
                textColor = AppTheme.color.textSecondary,
                modifier  = Modifier.weight(1f),
            )
            PlanBadge(
                label     = "أساسي",
                count     = state.planBasic,
                badgeBg   = Color(0xFFEFF6FF),
                textColor = Color(0xFF3B82F6),
                modifier  = Modifier.weight(1f),
            )
            PlanBadge(
                label     = "برو",
                count     = state.planPro,
                badgeBg   = AppTheme.color.secondary,
                textColor = AppTheme.color.primary,
                modifier  = Modifier.weight(1f),
            )
            PlanBadge(
                label     = "مؤسسي",
                count     = state.planEnterprise,
                badgeBg   = Color(0xFFF5F3FF),
                textColor = Color(0xFF7C3AED),
                modifier  = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun PlanBadge(
    label: String,
    count: Int,
    badgeBg: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(badgeBg)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text  = count.toString(),
            color = textColor,
            style = AppTheme.textStyle.headline.small.copy(fontWeight = FontWeight.ExtraBold),
        )
        Text(
            text      = label,
            color     = textColor.copy(alpha = .75f),
            style     = AppTheme.textStyle.label.small,
            textAlign = TextAlign.Center,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 4 ── Universities preview (up to 5 items)
//
// The section header shows a "عرض الكل" link that navigates to
// UniversitiesScreen where the user can see all universities and perform
// full CRUD (create / update / activate / deactivate / change plan).
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UniversitiesSection(
    state: HomeSuperAdminUiState,
    listener: HomeSuperAdminInteractionListener,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Section header row: title (RTL) + "عرض الكل" link
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Text(
                text     = "عرض الكل",
                color    = AppTheme.color.primary,
                style    = AppTheme.textStyle.label.medium.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.clickAnimation(listener::onViewAllUniversitiesClick),
            )
            SuperSectionTitle(text = "الجامعات")
        }

        if (state.universities.isEmpty()) {
            EmptyUniversities()
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Display at most 5 items (ViewModel already limits to 5, but
                // take(5) here is a safety net against future changes).
                state.universities.take(5).forEach { university ->
                    val isExpanded = state.selectedUniversityId == university.id

                    UniversityCard(
                        university = university,
                        isExpanded = isExpanded,
                        onClick    = {
                            listener.onUniversityClick(university.id, university.name)
                        },
                    )

                    // Animated admins panel beneath the selected card
                    AnimatedVisibility(
                        visible = isExpanded,
                        enter   = fadeIn(tween(200)) + expandVertically(tween(300)),
                        exit    = fadeOut(tween(150)) + shrinkVertically(tween(250)),
                    ) {
                        UniversityAdminsPanel(
                            universityName = state.selectedUniversityName,
                            admins         = state.universityAdmins,
                            isLoading      = state.adminsLoading,
                            onDismiss      = listener::onDismissAdminsPanel,
                        )
                    }
                }
            }

            // "عرض الكل" button at the bottom — only when there are more
            // than 5 universities in the platform
            if (state.totalUniversities > 5) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppTheme.color.primary.copy(alpha = .06f))
                        .border(
                            width = 1.dp,
                            color = AppTheme.color.primary.copy(alpha = .2f),
                            shape = RoundedCornerShape(12.dp),
                        )
                        .clickAnimation(listener::onViewAllUniversitiesClick)
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text  = "عرض جميع ${state.totalUniversities} جامعة",
                        color = AppTheme.color.primary,
                        style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.SemiBold),
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// University card (read-only preview; edits happen in UniversitiesScreen)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UniversityCard(
    university: HomeSuperAdminUiState.UniversityItem,
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg)
            .border(
                width = 1.dp,
                color = if (isExpanded)
                    AppTheme.color.primary.copy(alpha = .4f)
                else
                    AppTheme.color.border.copy(alpha = .6f),
                shape = RoundedCornerShape(16.dp),
            )
            .clickAnimation(onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Top row: name + status dot + chevron
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Icon(
                painter  = painterResource(
                    if (isExpanded) R.drawable.ic_trending_up else R.drawable.person
                ),
                tint     = AppTheme.color.textSecondary,
                modifier = Modifier.size(18.dp),
            )

            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (university.plan != null) {
                    PlanPill(plan = university.plan)
                }

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (university.isActiveBool) Color(0xFF22C55E)
                            else Color(0xFFEF4444)
                        ),
                )

                Text(
                    text      = university.name,
                    color     = AppTheme.color.text,
                    style     = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold),
                    maxLines  = 1,
                    overflow  = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                )
            }
        }

        // Bottom row: slug + student/doctor counts
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MiniCount(
                    iconRes = R.drawable.badge,
                    count   = university.doctorCount,
                    label   = "دكتور",
                )
                MiniCount(
                    iconRes = R.drawable.person,
                    count   = university.studentCount,
                    label   = "طالب",
                )
            }

            Text(
                text  = university.slug,
                color = AppTheme.color.textSecondary,
                style = AppTheme.textStyle.label.small,
            )
        }
    }
}

@Composable
private fun PlanPill(
    plan: String,
    modifier: Modifier = Modifier,
) {
    val (bg, fg) = when (plan.lowercase()) {
        "trial"      -> Color(0xFFF3F4F6) to AppTheme.color.textSecondary
        "basic"      -> Color(0xFFEFF6FF) to Color(0xFF3B82F6)
        "pro"        -> AppTheme.color.secondary to AppTheme.color.primary
        "enterprise" -> Color(0xFFF5F3FF) to Color(0xFF7C3AED)
        else         -> Color(0xFFF3F4F6) to AppTheme.color.textSecondary
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text  = plan.replaceFirstChar { it.uppercase() },
            color = fg,
            style = AppTheme.textStyle.label.small.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
        )
    }
}

@Composable
private fun MiniCount(
    iconRes: Int,
    count: Int,
    label: String,
) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            painter  = painterResource(iconRes),
            tint     = AppTheme.color.textSecondary,
            modifier = Modifier.size(12.dp),
        )
        Text(
            text  = "$count $label",
            color = AppTheme.color.textSecondary,
            style = AppTheme.textStyle.label.small,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Admins panel (expandable under a university card)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UniversityAdminsPanel(
    universityName: String,
    admins: List<HomeSuperAdminUiState.UniversityAdminItem>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(AppTheme.color.bg)
            .border(
                width = 1.dp,
                color = AppTheme.color.primary.copy(alpha = .2f),
                shape = RoundedCornerShape(14.dp),
            )
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(AppTheme.color.bgHover)
                    .clickAnimation(onDismiss),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter  = painterResource(R.drawable.ic_trending_up),
                    tint     = AppTheme.color.textSecondary,
                    modifier = Modifier.size(14.dp),
                )
            }

            Text(
                text  = "مشرفو $universityName",
                color = AppTheme.color.text,
                style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold),
            )
        }

        HorizontalDivider(color = AppTheme.color.border.copy(alpha = .5f))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(24.dp),
                        color       = AppTheme.color.primary,
                        strokeWidth = 2.dp,
                    )
                }
            }

            admins.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text  = "لا يوجد مشرفون مسجلون",
                        color = AppTheme.color.textSecondary,
                        style = AppTheme.textStyle.label.medium,
                    )
                }
            }

            else -> {
                admins.forEachIndexed { index, admin ->
                    AdminRow(admin = admin)
                    if (index < admins.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color    = AppTheme.color.border.copy(alpha = .3f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminRow(
    admin: HomeSuperAdminUiState.UniversityAdminItem,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier              = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(
                    if (admin.isActiveBool) Color(0xFF22C55E)
                    else Color(0xFFEF4444)
                ),
        )

        Spacer(Modifier.width(8.dp))

        Column(
            modifier            = Modifier.weight(1f),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text      = admin.name,
                color     = AppTheme.color.text,
                style     = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Medium),
                maxLines  = 1,
                overflow  = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
            )
            Text(
                text      = admin.email,
                color     = AppTheme.color.textSecondary,
                style     = AppTheme.textStyle.label.small,
                maxLines  = 1,
                overflow  = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
            )
        }

        Spacer(Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(AppTheme.color.primary.copy(alpha = .1f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text  = admin.name.firstOrNull()?.uppercaseChar()?.toString() ?: "؟",
                color = AppTheme.color.primary,
                style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Empty / Error states
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EmptyUniversities(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg)
            .border(
                width = 1.dp,
                color = AppTheme.color.border.copy(alpha = .5f),
                shape = RoundedCornerShape(16.dp),
            )
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text  = "لا توجد جامعات مسجلة في المنصة",
            color = AppTheme.color.textSecondary,
            style = AppTheme.textStyle.body.medium,
        )
    }
}

@Composable
private fun SuperAdminErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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

// ─────────────────────────────────────────────────────────────────────────────
// Shared helpers
// ─────────────────────────────────────────────────────────────────────────────

@Composable
internal fun SuperSectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text      = text,
        modifier  = modifier,
        color     = AppTheme.color.text,
        style     = AppTheme.textStyle.body.large.copy(fontWeight = FontWeight.Bold),
        textAlign = TextAlign.End,
    )
}

private fun formatNumber(value: Int): String = "%,d".format(value)

// ─────────────────────────────────────────────────────────────────────────────
// Effect handler
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HandleEffects(
    effects: Flow<HomeSuperAdminEffect>,
    onNavigateToUniversities: () -> Unit,
    onNavigateToCreateUniversity: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    LaunchedEffect(Unit) {
        effects.collectLatest { effect ->
            when (effect) {
                HomeSuperAdminEffect.NavigateToCreateUniversity -> onNavigateToCreateUniversity()
                HomeSuperAdminEffect.NavigateToUniversities     -> onNavigateToUniversities()
                HomeSuperAdminEffect.NavigateToSettings         -> onNavigateToSettings()
            }
        }
    }
}
