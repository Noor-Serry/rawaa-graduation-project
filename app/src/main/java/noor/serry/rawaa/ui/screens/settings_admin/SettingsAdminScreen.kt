package noor.serry.rawaa.ui.screens.settings_admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.MainViewModel
import noor.serry.rawaa.ui.navigation.university_admin.UniversityAdminBackStackProvider

// ── Entry point ───────────────────────────────────────────────────────────────

@Composable
fun SettingsAdminScreen(
    mainViewModel: MainViewModel = koinViewModel(),
    viewModel: SettingsAdminViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    HandleEffects(effects = viewModel.effect, mainViewModel = mainViewModel)

    when {
        state.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppTheme.color.primary)
            }
        }
        else -> {
            SettingsAdminContent(
                state    = state,
                listener = viewModel,
            )
        }
    }
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun SettingsAdminContent(
    state: SettingsAdminUiState,
    listener: SettingsAdminInteractionListener,
) {
    var showChangePassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.bgHover)
            .verticalScroll(rememberScrollState()),
    ) {
        // 1 ── Profile header banner
        ProfileHeaderBanner(state = state)

        Spacer(Modifier.height(20.dp))

        // 2 ── University info card
        if (state.universityName.isNotEmpty()) {
            UniversityInfoCard(state = state)
            Spacer(Modifier.height(12.dp))
        }

        // 3 ── Account info items
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SettingsSectionLabel(text = "معلومات الحساب")
            ProfileInfoRow(
                iconRes = R.drawable.ic_person,
                label   = "الاسم",
                value   = state.adminName,
            )
            ProfileInfoRow(
                iconRes = R.drawable.ic_email,
                label   = "البريد الإلكتروني",
                value   = state.adminEmail,
            )
            if (!state.phone.isNullOrBlank()) {
                ProfileInfoRow(
                    iconRes = R.drawable.ic_phone,
                    label   = "الهاتف",
                    value   = state.phone,
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // 4 ── Account actions
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SettingsSectionLabel(text = "الحساب")

            SettingsActionRow(
                iconRes     = R.drawable.lock,
                label       = "تغيير كلمة المرور",
                iconBg      = AppTheme.color.primary.copy(alpha = .08f),
                iconTint    = AppTheme.color.primary,
                showArrow   = true,
                onClick     = { showChangePassword = true },
            )

            SettingsActionRow(
                iconRes     = R.drawable.logout,
                label       = "تسجيل الخروج",
                iconBg      = Color(0xFFFEE2E2),
                iconTint    = Color(0xFFDC2626),
                labelColor  = Color(0xFFDC2626),
                showArrow   = false,
                onClick     = listener::onLogoutClick,
            )
        }

        Spacer(Modifier.height(32.dp))
    }

    // Change password dialog
    if (showChangePassword) {
        ChangePasswordDialog(
            state    = state,
            listener = listener,
            onDismiss = {
                showChangePassword = false
                listener.onDismissChangePassword()
            },
        )
    }
}

// ── Profile banner ────────────────────────────────────────────────────────────

@Composable
private fun ProfileHeaderBanner(state: SettingsAdminUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                verticalGradient(listOf(AppTheme.color.primary, AppTheme.color.primaryLight))
            )
            .padding(horizontal = 20.dp, vertical = 28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Avatar circle
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(AppTheme.color.bg.copy(alpha = .18f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text  = state.adminName.take(1),
                    color = AppTheme.color.bg,
                    style = AppTheme.textStyle.headline.medium.copy(fontWeight = FontWeight.Bold),
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text  = state.adminName,
                    color = AppTheme.color.bg,
                    style = AppTheme.textStyle.body.large.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text  = state.adminEmail,
                    color = AppTheme.color.bg.copy(alpha = .8f),
                    style = AppTheme.textStyle.label.medium,
                )
                if (state.roleTitle.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(AppTheme.color.bg.copy(alpha = .15f))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text  = state.roleTitle,
                            color = AppTheme.color.bg,
                            style = AppTheme.textStyle.label.small,
                        )
                    }
                }
            }
        }
    }
}

// ── University info card ──────────────────────────────────────────────────────

@Composable
private fun UniversityInfoCard(
    state: SettingsAdminUiState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg)
            .border(1.dp, AppTheme.color.border.copy(alpha = .6f), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AppTheme.color.secondary),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter  = painterResource(R.drawable.shield),
                tint     = AppTheme.color.primary,
                modifier = Modifier.size(22.dp),
            )
        }
        Column(
            modifier            = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text  = state.universityName,
                color = AppTheme.color.text,
                style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold),
            )
            if (state.universityPlan.isNotBlank()) {
                Text(
                    text  = "الخطة: ${state.universityPlan}",
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.label.small,
                )
            }
        }
    }
}

// ── Info row ──────────────────────────────────────────────────────────────────

@Composable
private fun ProfileInfoRow(
    iconRes: Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppTheme.color.bg)
            .border(1.dp, AppTheme.color.border.copy(alpha = .5f), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AppTheme.color.bgHover),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter  = painterResource(iconRes),
                tint     = AppTheme.color.textSecondary,
                modifier = Modifier.size(18.dp),
            )
        }
        Column(
            modifier            = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(text = label, color = AppTheme.color.textSecondary, style = AppTheme.textStyle.label.small)
            Text(text = value, color = AppTheme.color.text, style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Medium))
        }
    }
}

// ── Action row ────────────────────────────────────────────────────────────────

@Composable
private fun SettingsActionRow(
    iconRes: Int,
    label: String,
    iconBg: Color,
    iconTint: Color,
    showArrow: Boolean,
    onClick: () -> Unit,
    labelColor: Color = AppTheme.color.text,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppTheme.color.bg)
            .border(1.dp, AppTheme.color.border.copy(alpha = .5f), RoundedCornerShape(14.dp))
            .clickAnimation(onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
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
            text     = label,
            color    = labelColor,
            style    = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.weight(1f),
        )
        if (showArrow) {
            Icon(
                painter  = painterResource(R.drawable.ic_arrow_forward),
                tint     = AppTheme.color.textSecondary,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

// ── Section label ─────────────────────────────────────────────────────────────

@Composable
private fun SettingsSectionLabel(text: String) {
    Text(
        text  = text,
        color = AppTheme.color.textSecondary,
        style = AppTheme.textStyle.label.medium.copy(fontWeight = FontWeight.Bold),
    )
}

// ── Change password dialog ────────────────────────────────────────────────────

@Composable
private fun ChangePasswordDialog(
    state: SettingsAdminUiState,
    listener: SettingsAdminInteractionListener,
    onDismiss: () -> Unit,
) {
    var current  by remember { mutableStateOf("") }
    var newPass  by remember { mutableStateOf("") }
    var confirm  by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = .5f))
            .clickAnimation(onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(AppTheme.color.bg)
                .clickAnimation {}           // consume clicks so dialog doesn't close on tap
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text  = "تغيير كلمة المرور",
                color = AppTheme.color.text,
                style = AppTheme.textStyle.body.large.copy(fontWeight = FontWeight.Bold),
            )

            if (state.changePasswordSuccess) {
                Text(
                    text  = "تم تغيير كلمة المرور بنجاح",
                    color = Color(0xFF16A34A),
                    style = AppTheme.textStyle.body.small,
                )
            } else {
                PasswordField(hint = "كلمة المرور الحالية", value = current, onChanged = { current = it })
                PasswordField(hint = "كلمة المرور الجديدة", value = newPass, onChanged = { newPass = it })
                PasswordField(hint = "تأكيد كلمة المرور", value = confirm, onChanged = { confirm = it })

                if (state.changePasswordError != null) {
                    Text(
                        text  = state.changePasswordError,
                        color = Color(0xFFDC2626),
                        style = AppTheme.textStyle.label.small,
                    )
                }

                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AppTheme.color.bgHover)
                            .clickAnimation(onDismiss)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text  = "إلغاء",
                            color = AppTheme.color.textSecondary,
                            style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Medium),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AppTheme.color.primary)
                            .clickAnimation {
                                listener.onChangePasswordConfirm(current, newPass, confirm)
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (state.isChangingPassword) {
                            CircularProgressIndicator(
                                color    = AppTheme.color.bg,
                                modifier = Modifier.size(18.dp),
                            )
                        } else {
                            Text(
                                text  = "حفظ",
                                color = AppTheme.color.bg,
                                style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PasswordField(
    hint: String,
    value: String,
    onChanged: (String) -> Unit,
) {
    BasicTextField(
        value               = value,
        onValueChange       = onChanged,
        singleLine          = true,
        visualTransformation = PasswordVisualTransformation(),
        textStyle           = AppTheme.textStyle.body.small.copy(color = AppTheme.color.text),
        modifier            = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppTheme.color.bgHover)
            .border(1.dp, AppTheme.color.border.copy(alpha = .6f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 14.dp),
        decorationBox = { inner ->
            if (value.isEmpty()) {
                Text(
                    text  = hint,
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.body.small,
                )
            }
            inner()
        },
    )
}

// ── Effect handler ────────────────────────────────────────────────────────────

@Composable
private fun HandleEffects(
    effects: Flow<SettingsAdminEffect>,
    mainViewModel: MainViewModel,
) {
    val navBackStack = UniversityAdminBackStackProvider.current
    LaunchedEffect(Unit) {
        effects.collectLatest { effect ->
            when (effect) {
                SettingsAdminEffect.LoggedOut -> {
                    mainViewModel.onOnboardingCompleted()
                    navBackStack.clear()
                }
            }
        }
    }
}
