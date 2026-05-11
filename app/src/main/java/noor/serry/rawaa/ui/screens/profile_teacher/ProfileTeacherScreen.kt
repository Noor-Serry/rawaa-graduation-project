package noor.serry.rawaa.ui.screens.profile_teacher

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.data.dto.CourseDto
import noor.serry.rawaa.data.dto.UserDto
import org.koin.androidx.compose.koinViewModel

// ── Entry point ───────────────────────────────────────────────────────────────

@Composable
fun ProfileTeacherScreen(
    viewModel: ProfileTeacherViewModel = koinViewModel(),
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
                        .clickAnimation { viewModel.loadProfile() }
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

    ProfileTeacherContent(state = state)
}

// ── Root content ──────────────────────────────────────────────────────────────

@Composable
private fun ProfileTeacherContent(
    state: ProfileTeacherUiState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.bgHover),
    ) {

        // 1 ── Hero header (gradient strip + floating card)
        item {
            ProfileTeacherHero(state = state)
        }
        // 2 ── Stats row
        item {
            TeacherStatsRow(
                state    = state,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }

        // 3 ── Academic information card
        item {
            val profile      = state.user?.profile
            val employeeCode = profile?.nationalId
                ?: state.user?.id?.let { "FAC-%04d".format(it) }
                ?: "—"

            SectionCard(
                title    = "المعلومات الأكاديمية",
                iconRes  = R.drawable.ic_book,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                InfoRow(label = "الرقم الوظيفي",  value = employeeCode)
                RowDivider()
                InfoRow(label = "الكلية",          value = profile?.departmentName ?: "—")
                RowDivider()
                InfoRow(label = "التخصص",          value = profile?.roleTitle ?: "—")
                RowDivider()
                InfoRow(label = "الدرجة العلمية",  value = deriveDegree(profile?.roleTitle))
                RowDivider()
                InfoRow(
                    label = "سنوات الخبرة",
                    value = if (state.yearsOfExperience > 0) "${state.yearsOfExperience} سنوات" else "—",
                )
                RowDivider()
                InfoRow(
                    label = "تاريخ الالتحاق",
                    value = profile?.hireDate?.let { formatDateArabic(it) } ?: "—",
                )
            }
        }

        // 4 ── Contact information card
        item {
            val user    = state.user
            val profile = user?.profile
            SectionCard(
                title    = "معلومات الاتصال",
                iconRes  = R.drawable.ic_person,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
            ) {
                ContactRow(
                    label   = "البريد الإلكتروني",
                    value   = user?.email ?: "—",
                    iconRes = R.drawable.ic_email,
                )
                RowDivider()
                ContactRow(
                    label   = "رقم الهاتف",
                    value   = profile?.phone?.ifBlank { "—" } ?: "—",
                    iconRes = R.drawable.ic_phone,
                )
            }
        }

        // 5 ── Current courses card
        if (state.activeCourses.isNotEmpty()) {
            item {
                SectionCard(
                    title    = "المقررات الحالية",
                    iconRes  = R.drawable.ic_book,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp),
                ) {
                    state.activeCourses.forEachIndexed { index, course ->
                        CourseRow(course = course)
                        if (index < state.activeCourses.lastIndex) RowDivider()
                    }
                }
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

// ── Hero ──────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileTeacherHero(
    state: ProfileTeacherUiState,
    modifier: Modifier = Modifier,
) {
    val user    = state.user
    val profile = user?.profile

    Column(modifier = modifier) {
        // Gradient strip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    verticalGradient(listOf(AppTheme.color.primary, AppTheme.color.primaryLight))
                )
                .padding(horizontal = 24.dp, vertical = 48.dp),
            contentAlignment = Alignment.BottomStart,
        ) {
            Column {
                Text(
                    text  = "الملف الشخصي",
                    color = AppTheme.color.bg,
                    style = AppTheme.textStyle.headline.small,
                )
                Text(
                    text     = "معلوماتك الأكاديمية والمهنية",
                    color    = AppTheme.color.bg.copy(alpha = .75f),
                    style    = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }

        // Floating card — overlaps gradient strip by 36 dp (same as student)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .layout { measurable, constraints ->
                    val p = measurable.measure(constraints)
                    layout(p.width, p.height - 36.dp.roundToPx()) {
                        p.placeRelative(0, -36.dp.roundToPx())
                    }
                }
                .dropShadow(
                    shape  = RoundedCornerShape(24.dp),
                    shadow = Shadow(
                        radius = 20.dp, spread = -4.dp,
                        color  = AppTheme.color.primary.copy(alpha = .18f),
                        offset = DpOffset(0.dp, 10.dp),
                    ),
                )
                .background(AppTheme.color.bg, RoundedCornerShape(24.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Avatar — initial letter fallback (same as student)
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(
                            verticalGradient(
                                listOf(
                                    AppTheme.color.primary.copy(alpha = .2f),
                                    AppTheme.color.primaryLight.copy(alpha = .3f),
                                )
                            )
                        )
                        .border(3.dp, AppTheme.color.primary.copy(alpha = .25f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text  = user?.name?.firstOrNull()?.toString() ?: "؟",
                        color = AppTheme.color.primary,
                        style = AppTheme.textStyle.headline.medium.copy(fontWeight = FontWeight.Bold),
                    )
                }
                // Camera badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AppTheme.color.primary)
                        .border(2.dp, AppTheme.color.bg, CircleShape)
                        .clickAnimation { /* launch image picker */ },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter  = painterResource(R.drawable.ic_camera),
                        tint     = AppTheme.color.bg,
                        modifier = Modifier.size(15.dp),
                    )
                }
            }

            // Name + department + role badge
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                val prefix = if (user?.role == "doctor") "د. " else ""
                Text(
                    text  = "$prefix${user?.name ?: ""}",
                    color = AppTheme.color.text,
                    style = AppTheme.textStyle.headline.small.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text  = profile?.departmentName ?: profile?.roleTitle ?: "",
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.body.small,
                )
                // Faculty/role-title badge — mirrors student's faculty badge
                profile?.roleTitle?.takeIf { it.isNotBlank() }?.let { title ->
                    Box(
                        modifier = Modifier
                            .background(
                                AppTheme.color.primary.copy(alpha = .08f),
                                RoundedCornerShape(20.dp),
                            )
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text  = title,
                            color = AppTheme.color.primary,
                            style = AppTheme.textStyle.label.medium.copy(fontWeight = FontWeight.Medium),
                        )
                    }
                }
            }
        }
    }
}

// ── Stats row ─────────────────────────────────────────────────────────────────

@Composable
private fun TeacherStatsRow(state: ProfileTeacherUiState, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatChip(
            value    = if (state.yearsOfExperience > 0) "${state.yearsOfExperience}" else "—",
            label    = "سنوات\nالخبرة",
            iconRes  = R.drawable.ic_calendar,
            iconBg   = Color(0xFFFEF9C3),
            iconTint = Color(0xFFF59E0B),
            modifier = Modifier.weight(1f),
        )
        StatChip(
            value    = "${state.totalStudents}",
            label    = "إجمالي\nالطلاب",
            iconRes  = R.drawable.ic_person,
            iconBg   = Color(0xFFDBEAFE),
            iconTint = Color(0xFF3B82F6),
            modifier = Modifier.weight(1f),
        )
        StatChip(
            value    = "${state.activeCourses.size}",
            label    = "المقررات\nالحالية",
            iconRes  = R.drawable.ic_book,
            iconBg   = Color(0xFFDCFCE7),
            iconTint = Color(0xFF16A34A),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatChip(
    value: String,
    label: String,
    iconRes: Int,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(AppTheme.color.bg)
            .border(1.dp, AppTheme.color.border.copy(alpha = .6f), RoundedCornerShape(14.dp))
            .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter  = painterResource(iconRes),
                tint     = iconTint,
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            text      = value,
            color     = AppTheme.color.text,
            style     = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.ExtraBold),
            textAlign = TextAlign.Center,
        )
        Text(
            text      = label,
            color     = AppTheme.color.textSecondary,
            style     = AppTheme.textStyle.label.small.copy(fontSize = 10.sp),
            textAlign = TextAlign.Center,
            minLines  = 2,
        )
    }
}

// ── Section card ──────────────────────────────────────────────────────────────

@Composable
private fun SectionCard(
    title: String,
    iconRes: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg)
            .border(1.dp, AppTheme.color.border.copy(alpha = .7f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier              = Modifier.padding(bottom = 8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(AppTheme.color.primary.copy(alpha = .1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter  = painterResource(iconRes),
                    tint     = AppTheme.color.primary,
                    modifier = Modifier.size(18.dp),
                )
            }
            Text(
                text  = title,
                color = AppTheme.color.text,
                style = AppTheme.textStyle.body.large.copy(fontWeight = FontWeight.Bold),
            )
        }
        content()
    }
}

// ── Row components ────────────────────────────────────────────────────────────

@Composable
private fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier              = modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Text(
            text  = label,
            color = AppTheme.color.textSecondary,
            style = AppTheme.textStyle.body.small,
        )
        Text(
            text  = value.ifBlank { "—" },
            color = AppTheme.color.text,
            style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.SemiBold),
        )
    }
}

@Composable
private fun ContactRow(
    label: String,
    value: String,
    iconRes: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier              = modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(AppTheme.color.primary.copy(alpha = .08f), RoundedCornerShape(9.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter  = painterResource(iconRes),
                    tint     = AppTheme.color.primary,
                    modifier = Modifier.size(16.dp),
                )
            }
            Text(
                text  = label,
                color = AppTheme.color.textSecondary,
                style = AppTheme.textStyle.body.small,
            )
        }
        Text(
            text  = value,
            color = AppTheme.color.text,
            style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Medium),
        )
    }
}

@Composable
private fun CourseRow(course: CourseDto, modifier: Modifier = Modifier) {
    Row(
        modifier              = modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(AppTheme.color.primary.copy(alpha = .08f), RoundedCornerShape(9.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter  = painterResource(R.drawable.ic_book),
                    tint     = AppTheme.color.primary,
                    modifier = Modifier.size(16.dp),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text  = course.name,
                    color = AppTheme.color.text,
                    style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.SemiBold),
                )
                Text(
                    text  = "طالب ${course.enrolledCount ?: 0} • ${course.code}",
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.label.small,
                )
            }
        }
    }
}

@Composable
private fun RowDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(AppTheme.color.bgHover),
    )
}

// ── Utility helpers ───────────────────────────────────────────────────────────

private fun deriveDegree(roleTitle: String?): String {
    if (roleTitle.isNullOrBlank()) return "—"
    return when {
        roleTitle.contains("دكتور",     ignoreCase = true) ||
        roleTitle.contains("phd",       ignoreCase = true) ||
        roleTitle.contains("doctor",    ignoreCase = true) -> "دكتوراه"
        roleTitle.contains("ماجستير",   ignoreCase = true) ||
        roleTitle.contains("master",    ignoreCase = true) -> "ماجستير"
        roleTitle.contains("بكالوريوس", ignoreCase = true) ||
        roleTitle.contains("bachelor",  ignoreCase = true) -> "بكالوريوس"
        else -> roleTitle
    }
}

private fun formatDateArabic(dateStr: String): String {
    val months = listOf(
        "يناير", "فبراير", "مارس", "إبريل", "مايو", "يونيو",
        "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر",
    )
    return try {
        val parts = dateStr.take(10).split("-")
        val year  = parts[0]
        val month = parts[1].toInt()
        "${months[month - 1]} $year"
    } catch (e: Exception) {
        dateStr
    }
}
