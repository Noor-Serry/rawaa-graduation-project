package noor.serry.rawaa.ui.screens.profile_teacher

import android.R.attr.translationY
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.screens.negativeBottomPadding

// ── Design tokens matching Figma ─────────────────────────────────────────────
private val NavyDark  = Color(0xFF1F2C47)
private val NavyLight = Color(0xFF2D3F5F)
private val TextPrimary   = Color(0xFF0F172A)
private val TextSecondary = Color(0xFF64748B)
private val BgSurface     = Color(0xFFF8FAFC)
private val White         = Color(0xFFFFFFFF)
private val GreenBadgeBg  = Color(0xFFD1FAE5)
private val GreenBadgeFg  = Color(0xFF10B981)
private val YellowBadge   = Color(0xFFFACC15)
private val AwardBg       = Color(0xFFFEF3C7)
private val AwardIcon     = Color(0xFFF59E0B)
private val BlueIconBg    = Color(0x213B82F6)   // 13 % opacity
private val GreenIconBg   = Color(0x2110B981)
private val AmberIconBg   = Color(0x21F59E0B)
private val Divider       = Color(0xFFF8FAFC)

// ── Screen entry point ────────────────────────────────────────────────────────
@Composable
fun ProfileTeacherScreen(
    onBack: () -> Unit = {},
    viewModel: ProfileTeacherViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    HandleEffects(effects = viewModel.effect)
    ProfileTeacherContent(state = state, interactionListener = viewModel, onBack = onBack)
}

// ── Root content ──────────────────────────────────────────────────────────────
@Composable
private fun ProfileTeacherContent(
    state: ProfileTeacherUiState,
    interactionListener: ProfileTeacherInteractionListener,
    onBack: () -> Unit,
) {
    when {
        state.isLoading -> LoadingBox()
        state.errorMessage != null -> ErrorBox(state.errorMessage!!, interactionListener)
        else -> ProfileBody(state, interactionListener, onBack)
    }
}

@Composable
private fun LoadingBox() {
    Box(Modifier.fillMaxSize().background(BgSurface), contentAlignment = Alignment.Center) {
        Text("جاري التحميل...", color = TextSecondary, style = AppTheme.textStyle.body.medium)
    }
}

@Composable
private fun ErrorBox(message: String, listener: ProfileTeacherInteractionListener) {
    Box(Modifier.fillMaxSize().background(BgSurface), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, color = AppTheme.color.error, style = AppTheme.textStyle.body.medium)
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(NavyDark)
                    .clickAnimation { listener.load() }
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Text("إعادة المحاولة", color = White,
                    style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

// ── Main scrollable body ──────────────────────────────────────────────────────
@Composable
private fun ProfileBody(
    state: ProfileTeacherUiState,
    listener: ProfileTeacherInteractionListener,
    onBack: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BgSurface),
    ) {
        // ── Profile card (avatar + stats + edit button) ───────────────
        item {
            ProfileCard(
                state = state,
                onEditClick = listener::onEditProfileClick,
            )
        }

        // ── Academic info ─────────────────────────────────────────────
        item {
            SectionCard(modifier = Modifier.padding(top = 16.dp)) {
                SectionHeader(title = "المعلومات الأكاديمية", iconRes = R.drawable.ic_book)
                Spacer(Modifier.height(16.dp))
                InfoRow(label = "الرقم الوظيفي", value = state.employeeId)
                SectionDivider()
                InfoRow(label = "الكلية", value = state.department)
                SectionDivider()
                InfoRow(label = "التخصص", value = state.specialization)
                SectionDivider()
                InfoRow(label = "الدرجة العلمية", value = state.degree)
                if (state.experienceYears > 0) {
                    SectionDivider()
                    InfoRow(label = "سنوات الخبرة", value = "${state.experienceYears} سنوات")
                }
                if (state.enrollmentDate.isNotEmpty()) {
                    SectionDivider()
                    InfoRow(label = "تاريخ الالتحاق", value = state.enrollmentDate)
                }
            }
        }

        // ── Contact info ──────────────────────────────────────────────
        item {
            SectionCard(modifier = Modifier.padding(top = 16.dp)) {
                SectionHeader(title = "معلومات الاتصال", iconRes = noor.serry.designsystem.R.drawable.mail)
                Spacer(Modifier.height(16.dp))
                if (state.email.isNotEmpty()) ContactRow(label = "البريد الإلكتروني", value = state.email, iconRes = noor.serry.designsystem.R.drawable.mail)
                if (state.email.isNotEmpty() && state.phone.isNotEmpty()) SectionDivider()
                if (state.phone.isNotEmpty()) ContactRow(label = "رقم الهاتف", value = state.phone, iconRes = R.drawable.ic_phone)
                if ((state.phone.isNotEmpty() || state.email.isNotEmpty()) && state.office.isNotEmpty()) SectionDivider()
                if (state.office.isNotEmpty()) ContactRow(label = "المكتب", value = state.office, iconRes = R.drawable.mappin)
                if (state.office.isNotEmpty() && state.officeHours.isNotEmpty()) SectionDivider()
                if (state.officeHours.isNotEmpty()) ContactRow(label = "الساعات المكتبية", value = state.officeHours, iconRes = R.drawable.ic_clock)
            }
        }

        // ── Current courses ───────────────────────────────────────────
        if (state.currentCourses.isNotEmpty()) {
            item {
                SectionCard(modifier = Modifier.padding(horizontal = 24.dp).padding(top = 16.dp)) {
                    SectionHeader(title = "المقررات الحالية", iconRes = R.drawable.lock)
                    Spacer(Modifier.height(16.dp))
                    state.currentCourses.forEachIndexed { index, course ->
                        if (index > 0) Spacer(Modifier.height(12.dp))
                        CourseRow(course = course)
                    }
                }
            }
        }

        // ── Achievements ──────────────────────────────────────────────
        if (state.achievements.isNotEmpty()) {
            item {
                SectionCard(modifier = Modifier.padding(horizontal = 24.dp).padding(top = 16.dp)) {
                    SectionHeader(title = "الإنجازات والجوائز", iconRes = R.drawable.badge)
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        state.achievements.forEach { achievement ->
                            AwardCard(
                                modifier = Modifier.weight(1f),
                                title = achievement.title,
                                year = achievement.year,
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}


@Composable
private fun ProfileCard(
    state: ProfileTeacherUiState,
    onEditClick: () -> Unit,
) {
    val overlap = 24.dp

    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val (gradient, card) = createRefs()

        // Gradient strip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(NavyDark, NavyLight)))
                .padding(horizontal = 24.dp, vertical = 48.dp)
                .constrainAs(gradient) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "الملف الشخصي",
                    color = White,
                    style = AppTheme.textStyle.headline.small.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp),
                )
                Text(
                    text = "معلوماتك الأكاديمية والمهنية",
                    color = White.copy(alpha = 0.8f),
                    style = AppTheme.textStyle.body.medium,
                )
            }
        }

        // White card — top linked to gradient bottom with negative margin = overlap
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(White)
                .padding(24.dp)
                .constrainAs(card) {
                    top.linkTo(gradient.bottom, margin = -overlap)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Avatar circle with camera badge
            Box(modifier = Modifier.size(96.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Brush.verticalGradient(listOf(NavyDark, NavyLight))),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(painterResource(R.drawable.person), contentDescription=null, tint = White, modifier = Modifier.size(48.dp))
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(YellowBadge),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(painterResource(R.drawable.ic_camera), contentDescription = "تغيير الصورة", tint = NavyDark, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(state.name, color = TextPrimary, style = AppTheme.textStyle.headline.small.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp))
            if (state.specialization.isNotEmpty()) {
                Text(state.specialization, color = TextSecondary, style = AppTheme.textStyle.body.small)
            }
            if (state.degree.isNotEmpty()) {
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(GreenBadgeBg).padding(horizontal = 12.dp, vertical = 4.dp)) {
                    Text(state.degree, color = GreenBadgeFg, style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold))
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatTile(Modifier.weight(1f), state.totalStudents.toString(), "إجمالي الطلاب", GreenBadgeFg, GreenIconBg, R.drawable.ic_person)
                StatTile(Modifier.weight(1f), state.activeCourses.toString(), "المقررات الحالية", Color(0xFF3B82F6), BlueIconBg, R.drawable.ic_book)
            }
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(NavyDark)
                    .clickAnimation { onEditClick() }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(painterResource(R.drawable.ic_edit), contentDescription=null, tint = White, modifier = Modifier.size(20.dp))
                    Text("تعديل الملف الشخصي", color = White, style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Medium, fontSize = 16.sp))
                }
            }
        }
    }
}
// ── Stat tile ─────────────────────────────────────────────────────────────────
@Composable
private fun StatTile(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    iconTint: Color,
    iconBgColor: Color,
    iconRes: Int,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BgSurface)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBgColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            text = value,
            color = TextPrimary,
            style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
        )
        Text(
            text = label,
            color = TextSecondary,
            style = AppTheme.textStyle.label.medium.copy(fontSize = 12.sp),
        )
    }
}

// ── Section card wrapper ──────────────────────────────────────────────────────
@Composable
private fun SectionCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth().padding(horizontal = 24.dp)

            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .padding(16.dp),
    ) { content() }
}

// ── Section header row (icon + title) ────────────────────────────────────────
@Composable
private fun SectionHeader(title: String, iconRes: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End, // RTL: icon on right
    ) {
        Text(
            text = title,
            color = TextPrimary,
            style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(8.dp))
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = NavyDark,
            modifier = Modifier.size(20.dp),
        )
    }
}

// ── Key-value info row ────────────────────────────────────────────────────────
@Composable
private fun InfoRow(label: String, value: String) {
    if (value.isEmpty()) return
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Value on the right in RTL
        Text(
            text = value,
            color = TextPrimary,
            style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
        )
        Text(
            text = label,
            color = TextSecondary,
            style = AppTheme.textStyle.body.small.copy(fontSize = 14.sp),
        )
    }
}

// ── Contact info row (with icon box on left, label/value stacked, edit icon) ──
@Composable
private fun ContactRow(label: String, value: String, iconRes: Int) {
    if (value.isEmpty()) return
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Edit pencil (far right in RTL)
        Icon(
            painter = painterResource(R.drawable.ic_edit),
            contentDescription = "تعديل",
            tint = NavyDark,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.width(8.dp))
        // Label + value stacked
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = TextSecondary,
                style = AppTheme.textStyle.label.medium.copy(fontSize = 12.sp),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                color = TextPrimary,
                style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Medium, fontSize = 14.sp),
            )
        }
        Spacer(Modifier.width(8.dp))
        // Icon box on the left (RTL)
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(BgSurface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

// ── Course row ────────────────────────────────────────────────────────────────
@Composable
private fun CourseRow(course: CourseRefUiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BgSurface)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Icon on left side (RTL)
        Icon(
            painter = painterResource(R.drawable.ic_person),
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(8.dp))
        // Name + code
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = course.name,
                color = TextPrimary,
                style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${course.code} - ${course.studentsCount} طالب",
                color = TextSecondary,
                style = AppTheme.textStyle.label.medium.copy(fontSize = 12.sp),
            )
        }
    }
}

// ── Award card ────────────────────────────────────────────────────────────────
@Composable
private fun AwardCard(modifier: Modifier = Modifier, title: String, year: String) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AwardBg)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.badge),
            contentDescription = null,
            tint = AwardIcon,
            modifier = Modifier.size(32.dp),
        )
        Text(
            text = title,
            color = TextPrimary,
            style = AppTheme.textStyle.label.medium.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
        )
        Text(
            text = year,
            color = TextSecondary,
            style = AppTheme.textStyle.label.medium.copy(fontSize = 12.sp),
        )
    }
}

// ── Divider ───────────────────────────────────────────────────────────────────
@Composable
private fun SectionDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Divider),
    )
}

// ── Effect handler ────────────────────────────────────────────────────────────
@Composable
private fun HandleEffects(effects: Flow<ProfileTeacherEffect>) {
    LaunchedEffect(Unit) {
        effects.collectLatest { effect ->
            when (effect) {
                ProfileTeacherEffect.NavigateToEditProfile -> { /* TODO: navigate */ }
            }
        }
    }
}