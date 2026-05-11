package noor.serry.rawaa.ui.screens.profile_student

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
import noor.serry.rawaa.R

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    HandleEffects(effects = viewModel.effect)
    ProfileContent(state = state, listener = viewModel)
}

// ── Root content ──────────────────────────────────────────────────────────────

@Composable
private fun ProfileContent(
    state: ProfileUiState,
    listener: ProfileInteractionListener,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.bgHover),
    ) {
        // 1 ── Hero header
        item {
            ProfileHero(
                state    = state,
                listener = listener,
            )
        }

        // 2 ── Inline edit panel (slides in when isEditMode)
        item {
            AnimatedVisibility(
                visible = state.isEditMode,
                enter   = slideInVertically(tween(320)) { -it } + fadeIn(tween(320)),
                exit    = slideOutVertically(tween(280)) { -it } + fadeOut(tween(280)),
            ) {
                EditPanel(state = state, listener = listener)
            }
        }

        // 3 ── Stats row
        item {
            StatsRow(
                state    = state,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }

        // 4 ── Academic card
        item {
            SectionCard(
                title    = "المعلومات الأكاديمية",
                iconRes  = R.drawable.ic_book,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                InfoRow(label = "الرقم الجامعي", value = state.studentId,)
                RowDivider()
                InfoRow(label = "الكلية / التخصص", value = state.faculty)
                RowDivider()
                InfoRow(label = "المستوى", value = levelLabel(state.level))
                RowDivider()
                InfoRow(label = "سنة الالتحاق", value = state.enrollmentDate)
                RowDivider()
                InfoRow(label = "المعدل التراكمي", value = state.gpa)
            }
        }

        // 5 ── Personal / contact card
        item {
            SectionCard(
                title    = "معلومات التواصل",
                iconRes  = R.drawable.ic_person,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp, top = 8.dp),
            ) {
                ContactRow(
                    label   = "البريد الإلكتروني",
                    value   = state.email,
                    iconRes = R.drawable.ic_email,
                )
                RowDivider()
                ContactRow(
                    label   = "رقم الهاتف",
                    value   = state.phone.ifBlank { "—" },
                    iconRes = R.drawable.ic_phone,
                )
            }
        }
    }
}

// ── Hero header ───────────────────────────────────────────────────────────────

@Composable
private fun ProfileHero(
    state: ProfileUiState,
    listener: ProfileInteractionListener,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        // Gradient strip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    verticalGradient(
                        listOf(AppTheme.color.primary, AppTheme.color.primaryLight)
                    )
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
                    text  = "بياناتك الأكاديمية والشخصية",
                    color = AppTheme.color.bg.copy(alpha = .75f),
                    style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }

        // Floating card
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
            // Avatar
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
                    if (state.avatarUrl != null) {
                        // AsyncImage would go here — using initial letter fallback
                    }
                    Text(
                        text  = state.fullName.firstOrNull()?.toString() ?: "؟",
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
                        .clickAnimation { listener.onChangeAvatarClick() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter  = painterResource(R.drawable.ic_camera),
                        tint     = AppTheme.color.bg,
                        modifier = Modifier.size(15.dp),
                    )
                }
            }

            // Name + ID
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text  = state.fullName,
                    color = AppTheme.color.text,
                    style = AppTheme.textStyle.headline.small.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text  = "# ${state.studentId}",
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.body.small,
                )
                // Faculty badge
                if (state.faculty.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .background(
                                AppTheme.color.primary.copy(alpha = .08f),
                                RoundedCornerShape(20.dp),
                            )
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text  = state.faculty,
                            color = AppTheme.color.primary,
                            style = AppTheme.textStyle.label.medium.copy(fontWeight = FontWeight.Medium),
                        )
                    }
                }
            }

            // Edit / Save toggle button
            val editBg by animateColorAsState(
                if (state.isEditMode) AppTheme.color.bgHover else AppTheme.color.primary,
                tween(300),
            )
            val editTextColor by animateColorAsState(
                if (state.isEditMode) AppTheme.color.textSecondary else AppTheme.color.bg,
                tween(300),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(editBg)
                    .border(
                        1.dp,
                        if (state.isEditMode) AppTheme.color.border else Color.Transparent,
                        RoundedCornerShape(12.dp),
                    )
                    .clickAnimation {
                        if (state.isEditMode) listener.onCancelEditClick()
                        else listener.onEditProfileClick()
                    }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Icon(
                    painter  = painterResource(
                        if (state.isEditMode) R.drawable.outline_close_24 else R.drawable.ic_edit
                    ),
                    tint     = editTextColor,
                    modifier = Modifier.size(18.dp).padding(end = 0.dp),
                )
                Spacer(Modifier.size(8.dp))
                AnimatedContent(
                    targetState = state.isEditMode,
                    transitionSpec = {
                        fadeIn(tween(200)) togetherWith fadeOut(tween(150))
                    },
                ) { editing ->
                    Text(
                        text  = if (editing) "إلغاء التعديل" else "تعديل الملف الشخصي",
                        color = editTextColor,
                        style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold),
                    )
                }
            }
        }
    }
}

// ── Edit panel ────────────────────────────────────────────────────────────────

@Composable
private fun EditPanel(
    state: ProfileUiState,
    listener: ProfileInteractionListener,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg)
            .border(1.dp, AppTheme.color.primary.copy(alpha = .3f), RoundedCornerShape(16.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(AppTheme.color.primary.copy(alpha = .1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter  = painterResource(R.drawable.ic_edit),
                    tint     = AppTheme.color.primary,
                    modifier = Modifier.size(18.dp),
                )
            }
            Text(
                text  = "تعديل البيانات",
                color = AppTheme.color.primary,
                style = AppTheme.textStyle.body.large.copy(fontWeight = FontWeight.Bold),
            )
        }

        // Fields
        EditField(
            label       = "الاسم الكامل",
            value       = state.editName,
            onValueChange = listener::onNameChanged,
            iconRes     = R.drawable.ic_person,
            keyboardType = KeyboardType.Text,
        )
        EditField(
            label        = "البريد الإلكتروني",
            value        = state.editEmail,
            onValueChange = listener::onEmailChanged,
            iconRes      = R.drawable.ic_email,
            keyboardType = KeyboardType.Email,
        )
        EditField(
            label        = "رقم الهاتف",
            value        = state.editPhone,
            onValueChange = listener::onPhoneChanged,
            iconRes      = R.drawable.ic_phone,
            keyboardType = KeyboardType.Phone,
        )

        // Save button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (state.isSaving) AppTheme.color.primary.copy(alpha = .5f)
                    else AppTheme.color.primary
                )
                .clickAnimation { listener.onSaveProfileClick() }
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Icon(
                painter  = painterResource(R.drawable.chevronleft),
                tint     = AppTheme.color.bg,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text  = if (state.isSaving) "جارٍ الحفظ…" else "حفظ التغييرات",
                color = AppTheme.color.bg,
                style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold),
            )
        }
    }
}

@Composable
private fun EditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    iconRes: Int,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text  = label,
            color = AppTheme.color.textSecondary,
            style = AppTheme.textStyle.label.medium,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(AppTheme.color.bgHover)
                .border(1.dp, AppTheme.color.border, RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            BasicTextField(
                value         = value,
                onValueChange = onValueChange,
                modifier      = Modifier.weight(1f),
                textStyle     = AppTheme.textStyle.body.medium.copy(
                    color     = AppTheme.color.text,
                    textAlign = TextAlign.End,
                ),
                singleLine    = true,
                cursorBrush   = SolidColor(AppTheme.color.primary),
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction    = ImeAction.Next,
                ),
            )
            Icon(
                painter  = painterResource(iconRes),
                tint     = AppTheme.color.primary,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

// ── Stats row ─────────────────────────────────────────────────────────────────

@Composable
private fun StatsRow(state: ProfileUiState, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatChip(
            value    = state.studyYears.toString(),
            label    = "سنوات\nالدراسة",
            iconRes  = R.drawable.ic_calendar,
            iconBg   = Color(0xFFFEF9C3),
            iconTint = Color(0xFFF59E0B),
            modifier = Modifier.weight(1f),
        )
        StatChip(
            value    = state.completedCourses.toString(),
            label    = "مقررات\nمكتملة",
            iconRes  = R.drawable.ic_book,
            iconBg   = Color(0xFFDBEAFE),
            iconTint = Color(0xFF3B82F6),
            modifier = Modifier.weight(1f),
        )
        StatChip(
            value    = "${state.creditHours}",
            label    = "ساعة\nمعتمدة",
            iconRes  = R.drawable.ic_grades,
            iconBg   = Color(0xFFDCFCE7),
            iconTint = Color(0xFF16A34A),
            modifier = Modifier.weight(1f),
        )
        StatChip(
            value    = "${state.attendanceRate.toInt()}%",
            label    = "نسبة\nالحضور",
            iconRes  = R.drawable.ic_calendar,
            iconBg   = Color(0xFFF3E8FF),
            iconTint = Color(0xFF9333EA),
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
        // Section header
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

// ── Info rows ─────────────────────────────────────────────────────────────────

@Composable
private fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier              = modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Text(
            text  = value.ifBlank { "—" },
            color = AppTheme.color.text,
            style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.SemiBold),
        )
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text  = label,
                color = AppTheme.color.textSecondary,
                style = AppTheme.textStyle.body.small,
            )
        }
    }
}

@Composable
private fun ContactRow(label: String, value: String, iconRes: Int, modifier: Modifier = Modifier) {
    Row(
        modifier              = modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Text(
            text  = value,
            color = AppTheme.color.text,
            style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Medium),
        )
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text  = label,
                color = AppTheme.color.textSecondary,
                style = AppTheme.textStyle.body.small,
            )
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

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun levelLabel(level: String) = when (level) {
    "1" -> "الفرقة الأولى"
    "2" -> "الفرقة الثانية"
    "3" -> "الفرقة الثالثة"
    "4" -> "الفرقة الرابعة"
    else -> level.ifBlank { "—" }
}

// ── Effects ───────────────────────────────────────────────────────────────────

@Composable
private fun HandleEffects(effects: Flow<ProfileEffect>) {
    LaunchedEffect(Unit) {
        effects.collectLatest { effect ->
            when (effect) {
                is ProfileEffect.OpenImagePicker -> { /* launch image picker */ }
                is ProfileEffect.ShowSaveSuccess -> { /* show snackbar */ }
                is ProfileEffect.ShowError       -> { /* show error snackbar */ }
            }
        }
    }
}
