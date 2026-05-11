package noor.serry.rawaa.ui.screens.profile_student

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
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.BackStackProvider
import noor.serry.rawaa.R

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    HandleEffects(effects = viewModel.effect)
    ProfileContent(state = state, interactionListener = viewModel)
}

@Composable
private fun ProfileContent(
    state: ProfileUiState,
    interactionListener: ProfileInteractionListener,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.bgHover),
    ) {
        // ── 1. Header ──────────────────────────────────────────────
        item {
            ProfileHeader(
                state = state,
                onEditProfileClick  = interactionListener::onEditProfileClick,
                onChangeAvatarClick = interactionListener::onChangeAvatarClick,
            )
        }

        // ── 2. Academic info ───────────────────────────────────────
        // All fields come from UserProfileDto (departmentName, level, enrollmentYear, nationalId, gpa)
        item {
            ProfileSectionCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 24.dp)
            ) {
                ProfileSectionHeader(title = "المعلومات الأكاديمية", iconRes = R.drawable.ic_book)
                Spacer(Modifier.height(16.dp))
                // UserProfileDto.nationalId or UserDto.id
                AcademicInfoRow(label = "الرقم الجامعي", value = state.studentId)
                ProfileDivider()
                // UserProfileDto.departmentName
                AcademicInfoRow(label = "الكلية", value = state.faculty)
                ProfileDivider()
                // UserProfileDto.departmentName
                AcademicInfoRow(label = "التخصص", value = state.major)
                ProfileDivider()
                // UserProfileDto.level
                AcademicInfoRow(label = "المستوى", value = state.level)
                ProfileDivider()
                // UserProfileDto.enrollmentYear
                AcademicInfoRow(label = "تاريخ الالتحاق", value = state.enrollmentDate)
            }
        }

        // ── 3. Personal info ───────────────────────────────────────
        // Only fields that exist in UserDto / UserProfileDto are shown
        item {
            ProfileSectionCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp)
            ) {
                ProfileSectionHeader(title = "المعلومات الشخصية", iconRes = R.drawable.ic_person)
                Spacer(Modifier.height(16.dp))
                // UserDto.email
                PersonalInfoRow(
                    label    = "البريد الإلكتروني",
                    value    = state.email,
                    iconRes  = R.drawable.ic_email,
                    onEditClick = { interactionListener.onEditFieldClick(ProfileField.EMAIL) },
                )
                ProfileDivider()
                // UserProfileDto.phone
                PersonalInfoRow(
                    label    = "رقم الهاتف",
                    value    = state.phone,
                    iconRes  = R.drawable.ic_phone,
                    onEditClick = { interactionListener.onEditFieldClick(ProfileField.PHONE) },
                )
                // Removed: birthDate row — UserDto and UserProfileDto have no birthDate field
                // Removed: address row  — UserDto and UserProfileDto have no address field
            }
        }

        // Removed: Bottom achievements/certificates cards
        // achievementsCount and certificatesCount had no server endpoints or DTOs
    }
}

// ── Header ────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileHeader(
    state: ProfileUiState,
    onEditProfileClick: () -> Unit,
    onChangeAvatarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(192.dp)
                .background(
                    brush = verticalGradient(
                        colors = listOf(AppTheme.color.primary, AppTheme.color.primaryLight)
                    )
                )
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp),
            contentAlignment = Alignment.TopEnd,
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "الملف الشخصي",
                    color = AppTheme.color.bg,
                    style = AppTheme.textStyle.headline.small,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "معلوماتك الأكاديمية والشخصية",
                    color = AppTheme.color.bgSecondary,
                    style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Normal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(placeable.width, placeable.height - 32.dp.roundToPx()) {
                        placeable.placeRelative(0, -32.dp.roundToPx())
                    }
                }
                .dropShadow(
                    shape = RoundedCornerShape(24.dp),
                    shadow = Shadow(
                        radius = 10.dp, spread = -6.dp,
                        color = AppTheme.color.text.copy(alpha = .1f),
                        offset = DpOffset(0.dp, 8.dp),
                    )
                )
                .background(AppTheme.color.bg, RoundedCornerShape(24.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Avatar with camera badge
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(AppTheme.color.border),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_person),
                        contentDescription = null,
                        tint = AppTheme.color.textSecondary,
                        modifier = Modifier.size(40.dp),
                    )
                }
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF59E0B))
                        .clickAnimation { onChangeAvatarClick() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_camera),
                        contentDescription = null,
                        tint = AppTheme.color.bg,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // UserDto.name
                Text(
                    text = state.fullName,
                    color = AppTheme.color.primaryDark,
                    style = AppTheme.textStyle.headline.small.copy(fontWeight = FontWeight.Bold),
                )
                // UserProfileDto.nationalId or UserDto.id
                Text(
                    text = state.studentId,
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.body.small,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppTheme.color.primaryDark)
                    .clickAnimation { onEditProfileClick() }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_edit),
                    contentDescription = null,
                    tint = AppTheme.color.bg,
                    modifier = Modifier.padding(end = 8.dp).size(18.dp),
                )
                Text(
                    text = "تعديل الملف الشخصي",
                    color = AppTheme.color.bg,
                    style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold),
                )
            }

            // Stat chips — all backed by server data
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Computed: current year - UserProfileDto.enrollmentYear
                ProfileStatChip(
                    value   = state.studyYears.toString(),
                    label   = "سنوات\nالدراسة",
                    iconRes = R.drawable.ic_calendar,
                    iconBg  = Color(0xFFFEF9C3),
                    iconTint= Color(0xFFF59E0B),
                    modifier = Modifier.weight(1f),
                )
                // StudentDashboardDto.courses.count { status == "completed" }
                ProfileStatChip(
                    value   = state.completedCourses.toString(),
                    label   = "المقررات\nالمكتملة",
                    iconRes = R.drawable.ic_book,
                    iconBg  = Color(0xFFDBEAFE),
                    iconTint= Color(0xFF3B82F6),
                    modifier = Modifier.weight(1f),
                )
                // UserProfileDto.gpa or StudentDashboardDto.student.gpa
                ProfileStatChip(
                    value   = state.gpa,
                    label   = "المعدل\nالتراكمي",
                    iconRes = R.drawable.ic_grades,
                    iconBg  = Color(0xFFDCFCE7),
                    iconTint= Color(0xFF16A34A),
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun ProfileStatChip(
    value: String, label: String,
    iconRes: Int, iconBg: Color, iconTint: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AppTheme.color.bgHover)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(painter = painterResource(iconRes), contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Text(text = value, color = AppTheme.color.primaryDark, style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold))
        Text(text = label, color = AppTheme.color.textSecondary, style = AppTheme.textStyle.label.small, minLines = 2)
    }
}

@Composable
private fun ProfileSectionCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg)
            .border(1.17.dp, AppTheme.color.border, RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) { content() }
}

@Composable
private fun ProfileSectionHeader(title: String, iconRes: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(painter = painterResource(iconRes), contentDescription = null, tint = AppTheme.color.primary, modifier = Modifier.size(20.dp))
        Text(text = title, color = AppTheme.color.primary, style = AppTheme.textStyle.body.large.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp))
    }
}

@Composable
private fun AcademicInfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = value, color = AppTheme.color.primaryDark, style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold))
        Text(text = label, color = AppTheme.color.textSecondary, style = AppTheme.textStyle.body.small)
    }
}

@Composable
private fun PersonalInfoRow(label: String, value: String, iconRes: Int, onEditClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painter = painterResource(R.drawable.ic_edit), contentDescription = null, tint = AppTheme.color.textSecondary, modifier = Modifier.size(18.dp).clickAnimation { onEditClick() })
        Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp), horizontalAlignment = Alignment.End) {
            Text(text = label, color = AppTheme.color.textSecondary, style = AppTheme.textStyle.label.medium)
            Text(text = value, color = AppTheme.color.primaryDark, style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Medium), modifier = Modifier.padding(top = 2.dp))
        }
        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(AppTheme.color.primary.copy(alpha = .08f)), contentAlignment = Alignment.Center) {
            Icon(painter = painterResource(iconRes), contentDescription = null, tint = AppTheme.color.primary, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun ProfileDivider() {
    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(AppTheme.color.bgHover))
}

@Composable
private fun HandleEffects(effects: Flow<ProfileEffect>) {
    LaunchedEffect(Unit) {
        effects.collectLatest { /* navigation handled by caller */ }
    }
}
