package noor.serry.rawaa.ui.screens.student_profile_teacher

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.navigation.teatcher.TeacherBackStackProvider

// ── Entry point ───────────────────────────────────────────────────────────────
//
// studentId is passed from the nav host via TeacherRouteKeys.StudentProfile(studentId).
// Koin resolves StudentProfileViewModel through the module that declares:
//
//   viewModel { (id: Int) -> StudentProfileViewModel(id, get(), get()) }
//
// koinViewModel(parameters = { parametersOf(studentId) }) forwards that Int at
// creation time so Koin injects the correct instance per student.

@Composable
fun StudentProfileScreen(
    studentId: Int,
    viewModel: StudentProfileViewModel = koinViewModel(
        key = "student_profile_$studentId",
        parameters = { parametersOf(studentId) }),
) {
    val state by viewModel.state.collectAsState()
    HandleEffects(effects = viewModel.effect)
    StudentProfileContent(state = state, onBack = { viewModel.onBackClick() }, onRetry = viewModel::load)
}

// ── Root content ──────────────────────────────────────────────────────────────

@Composable
private fun StudentProfileContent(
    state: StudentProfileUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    when {
        state.isLoading -> LoadingScreen()
        state.error != null -> ErrorScreen(message = state.error, onRetry = onRetry, onBack = onBack)
        else -> ProfileBody(state = state, onBack = onBack)
    }
}

@Composable
private fun ProfileBody(state: StudentProfileUiState, onBack: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.bgHover),
    ) {
        // 1 ── Hero header with identity card
        item {
            ProfileHero(state = state, onBack = onBack)
        }

        // 2 ── Stats row (credit hours, level, enrollment year, GPA)
        item {
            StatsRow(
                state    = state,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }

        // 3 ── Academic info card
        item {
            SectionCard(
                title   = "المعلومات الأكاديمية",
                iconRes = R.drawable.ic_book,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                if (!state.nationalId.isNullOrBlank()) {
                    InfoRow(label = "الرقم الوطني", value = state.nationalId)
                    RowDivider()
                }
                InfoRow(label = "الكلية / التخصص", value = state.departmentName ?: "—")
                RowDivider()
                InfoRow(label = "المستوى", value = levelLabel(state.level))
                RowDivider()
                InfoRow(label = "سنة الالتحاق", value = state.enrollmentYear?.toString() ?: "—")
                RowDivider()
                InfoRow(label = "المعدل التراكمي", value = state.gpa ?: "—")
                RowDivider()
                InfoRow(
                    label = "الساعات الحالية",
                    value = state.currentCreditHours?.toString() ?: "—",
                )
            }
        }

        // 4 ── Contact info card
        item {
            SectionCard(
                title   = "معلومات التواصل",
                iconRes = R.drawable.ic_person,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                ContactRow(
                    label   = "البريد الإلكتروني",
                    value   = state.email,
                    iconRes = R.drawable.ic_email,
                )
                if (!state.phone.isNullOrBlank()) {
                    RowDivider()
                    ContactRow(
                        label   = "رقم الهاتف",
                        value   = state.phone,
                        iconRes = R.drawable.ic_phone,
                    )
                }
            }
        }

        // 5 ── Enrolled courses section header
        item {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 4.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(AppTheme.color.primary.copy(alpha = .1f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter  = painterResource(R.drawable.ic_book),
                            tint     = AppTheme.color.primary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Text(
                        text  = "المقررات المسجّلة",
                        color = AppTheme.color.text,
                        style = AppTheme.textStyle.headline.small.copy(fontSize = 18.sp),
                    )
                }
                Text(
                    text  = "${state.courses.size} مقرر",
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.body.small,
                )
            }
        }

        // 6 ── Course cards — GET /api/students/{id}/courses
        if (state.courses.isEmpty()) {
            item {
                Box(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AppTheme.color.bg)
                        .border(1.dp, AppTheme.color.border, RoundedCornerShape(14.dp))
                        .padding(vertical = 24.dp),
                    contentAlignment  = Alignment.Center,
                ) {
                    Text(
                        text  = "لا توجد مقررات مسجّلة",
                        color = AppTheme.color.textSecondary,
                        style = AppTheme.textStyle.body.medium,
                    )
                }
            }
        } else {
            items(state.courses, key = { it.courseId }) { course ->
                CourseCard(
                    course   = course,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

// ── Hero header ───────────────────────────────────────────────────────────────

@Composable
private fun ProfileHero(
    state: StudentProfileUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val statusColor = statusColor(state.statusType)

    Column(modifier = modifier) {
        // Gradient strip with back button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(verticalGradient(listOf(AppTheme.color.primary, AppTheme.color.primaryLight)))
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp),
        ) {
            // Back button
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppTheme.color.bg.copy(alpha = .15f))
                    .clickAnimation { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter  = painterResource(R.drawable.ic_arrow_forward),
                    tint     = AppTheme.color.bg,
                    modifier = Modifier.size(18.dp),
                )
            }

            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Text(
                    text  = "الملف الشخصي للطالب",
                    color = AppTheme.color.bg,
                    style = AppTheme.textStyle.headline.small,
                )
                Text(
                    text  = "بيانات الطالب الأكاديمية",
                    color = AppTheme.color.bg.copy(alpha = .75f),
                    style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }

        // Floating identity card — same layout as ProfileScreen's floating card
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Avatar initial circle
            Box(
                modifier = Modifier
                    .size(88.dp)
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
                    text  = state.name.firstOrNull()?.toString() ?: "؟",
                    color = AppTheme.color.primary,
                    style = AppTheme.textStyle.headline.medium.copy(fontWeight = FontWeight.Bold),
                )
            }

            // Name + status badge
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text  = state.name,
                    color = AppTheme.color.text,
                    style = AppTheme.textStyle.headline.small.copy(fontWeight = FontWeight.Bold),
                )
                if (!state.displayId.isNullOrBlank()) {
                    Text(
                        text  = "# ${state.displayId}",
                        color = AppTheme.color.textSecondary,
                        style = AppTheme.textStyle.body.small,
                    )
                }
                // Status badge (ممتاز / جيد / يحتاج متابعة)
                Box(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = .1f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 5.dp),
                ) {
                    Text(
                        text  = state.statusLabel,
                        color = statusColor,
                        style = AppTheme.textStyle.label.medium.copy(fontWeight = FontWeight.Bold),
                    )
                }
                // Department badge
                if (!state.departmentName.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .background(AppTheme.color.primary.copy(alpha = .08f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text  = state.departmentName,
                            color = AppTheme.color.primary,
                            style = AppTheme.textStyle.label.medium.copy(fontWeight = FontWeight.Medium),
                        )
                    }
                }

                // Active / inactive chip
                val activeColor = if (state.isActive) Color(0xFF10B981) else AppTheme.color.error
                Box(
                    modifier = Modifier
                        .background(activeColor.copy(alpha = .08f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 3.dp),
                ) {
                    Text(
                        text  = if (state.isActive) "نشط" else "غير نشط",
                        color = activeColor,
                        style = AppTheme.textStyle.label.small,
                    )
                }
            }
        }
    }
}

// ── Stats row ─────────────────────────────────────────────────────────────────

@Composable
private fun StatsRow(state: StudentProfileUiState, modifier: Modifier = Modifier) {
    Row(
        modifier              = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatChip(
            value    = levelLabel(state.level),
            label    = "المستوى",
            iconRes  = R.drawable.ic_grades,
            iconBg   = Color(0xFFDBEAFE),
            iconTint = Color(0xFF3B82F6),
            modifier = Modifier.weight(1f),
        )
        StatChip(
            value    = state.gpa ?: "—",
            label    = "المعدل",
            iconRes  = R.drawable.ic_trending_up,
            iconBg   = Color(0xFFDCFCE7),
            iconTint = Color(0xFF16A34A),
            modifier = Modifier.weight(1f),
        )
        StatChip(
            value    = state.currentCreditHours?.toString() ?: "—",
            label    = "الساعات",
            iconRes  = R.drawable.ic_book,
            iconBg   = Color(0xFFF3E8FF),
            iconTint = Color(0xFF9333EA),
            modifier = Modifier.weight(1f),
        )
        StatChip(
            value    = state.enrollmentYear?.toString() ?: "—",
            label    = "الالتحاق",
            iconRes  = R.drawable.ic_calendar,
            iconBg   = Color(0xFFFEF9C3),
            iconTint = Color(0xFFF59E0B),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatChip(
    value: String, label: String,
    iconRes: Int, iconBg: Color, iconTint: Color,
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
            Icon(painter = painterResource(iconRes), tint = iconTint, modifier = Modifier.size(18.dp))
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
        )
    }
}

// ── Course card ───────────────────────────────────────────────────────────────

@Composable
private fun CourseCard(course: StudentCourseItem, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg)
            .border(1.17.dp, AppTheme.color.border, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        // Course icon box
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AppTheme.color.primary.copy(alpha = .1f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter  = painterResource(R.drawable.ic_book),
                tint     = AppTheme.color.primary,
                modifier = Modifier.size(22.dp),
            )
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text  = course.courseName,
                color = AppTheme.color.text,
                style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text  = course.code,
                color = AppTheme.color.textSecondary,
                style = AppTheme.textStyle.label.medium,
            )
            if (!course.doctorName.isNullOrBlank()) {
                Text(
                    text  = course.doctorName,
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.label.medium,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically,
                modifier              = Modifier.padding(top = 2.dp),
            ) {
                // Semester chip
                if (!course.semester.isNullOrBlank()) {
                    InfoChip(text = course.semester)
                }
                // Credit hours chip
                InfoChip(text = "${course.creditHours} س.م")
            }
        }

        // Grade column — null until the teacher/admin assigns a grade
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            if (course.grade != null) {
                Text(
                    text  = "${course.grade.toInt()}",
                    color = gradeColor(course.grade),
                    style = AppTheme.textStyle.headline.small.copy(fontWeight = FontWeight.ExtraBold),
                )
                Text(
                    text  = "الدرجة",
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.label.small,
                )
            } else {
                Text(
                    text  = "—",
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.body.medium,
                )
            }
        }
    }
}

@Composable
private fun InfoChip(text: String) {
    Box(
        modifier = Modifier
            .background(AppTheme.color.bgHover, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text  = text,
            color = AppTheme.color.textSecondary,
            style = AppTheme.textStyle.label.small,
        )
    }
}

// ── Section card (reused from ProfileScreen) ──────────────────────────────────

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
                Icon(painter = painterResource(iconRes), tint = AppTheme.color.primary, modifier = Modifier.size(18.dp))
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

// ── Info rows (reused from ProfileScreen) ─────────────────────────────────────

@Composable
private fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier              = modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Text(text = label, color = AppTheme.color.textSecondary, style = AppTheme.textStyle.body.small)
        Text(
            text  = value.ifBlank { "—" },
            color = AppTheme.color.text,
            style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.SemiBold),
        )
    }
}

@Composable
private fun ContactRow(label: String, value: String, iconRes: Int, modifier: Modifier = Modifier) {
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
                Icon(painter = painterResource(iconRes), tint = AppTheme.color.primary, modifier = Modifier.size(16.dp))
            }
            Text(text = label, color = AppTheme.color.textSecondary, style = AppTheme.textStyle.body.small)
        }
        Text(text = value, color = AppTheme.color.text, style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Medium))
    }
}

@Composable
private fun RowDivider() {
    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(AppTheme.color.bgHover))
}

// ── Loading / error screens ───────────────────────────────────────────────────

@Composable
private fun LoadingScreen() {
    Box(Modifier.fillMaxSize().background(AppTheme.color.bgHover), contentAlignment = Alignment.Center) {
        Text(text = "جاري التحميل...", color = AppTheme.color.textSecondary, style = AppTheme.textStyle.body.medium)
    }
}

@Composable
private fun ErrorScreen(message: String, onRetry: () -> Unit, onBack: () -> Unit) {
    Column(
        modifier              = Modifier.fillMaxSize().background(AppTheme.color.bgHover).padding(24.dp),
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = Arrangement.Center,
    ) {
        Text(text = message, color = AppTheme.color.error, style = AppTheme.textStyle.body.medium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppTheme.color.bgHover)
                    .border(1.dp, AppTheme.color.border, RoundedCornerShape(12.dp))
                    .clickAnimation { onBack() }
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                Text(text = "رجوع", color = AppTheme.color.textSecondary, style = AppTheme.textStyle.body.medium)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppTheme.color.primary)
                    .clickAnimation { onRetry() }
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                Text(text = "إعادة المحاولة", color = AppTheme.color.bg, style = AppTheme.textStyle.body.medium)
            }
        }
    }
}

// ── Effects ───────────────────────────────────────────────────────────────────

@Composable
private fun HandleEffects(effects: Flow<StudentProfileEffect>) {
    val backstack = TeacherBackStackProvider.current

    LaunchedEffect(Unit) {
        effects.collectLatest { effect ->
            when (effect) {
                is StudentProfileEffect.NavigateBack   -> {backstack.removeLastOrNull()}
                is StudentProfileEffect.ShowError      -> { /* show snackbar */ }
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun levelLabel(level: Int?) = when (level) {
    1    -> "الأولى"
    2    -> "الثانية"
    3    -> "الثالثة"
    4    -> "الرابعة"
    null -> "—"
    else -> level.toString()
}

@Composable
private fun statusColor(type: StudentStatusType) = when (type) {
    StudentStatusType.EXCELLENT       -> Color(0xFF10B981)
    StudentStatusType.GOOD            -> AppTheme.color.primary
    StudentStatusType.NEEDS_FOLLOW_UP -> AppTheme.color.error
}

@Composable
private fun gradeColor(grade: Float) = when {
    grade >= 85 -> Color(0xFF10B981)
    grade >= 60 -> AppTheme.color.primary
    else        -> AppTheme.color.error
}
