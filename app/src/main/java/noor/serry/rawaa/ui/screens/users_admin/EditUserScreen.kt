package noor.serry.rawaa.ui.screens.users_admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import noor.serry.designsystem.components.BaseButton
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.LabelInputField
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.navigation.university_admin.UniversityAdminBackStackProvider

// ─────────────────────────────────────────────────────────────────────────────
// Entry point
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EditUserScreen(
    userId: Int,
    userType: UsersAdminUiState.UserType,
    viewModel: EditUserViewModel = koinViewModel(
        key = "$userId-$userType",
        parameters = {
            parametersOf(userId, userType)
        }
    )) {
    val state    by viewModel.state.collectAsState()
    HandleEditEffects(effects = viewModel.effect)

    when {
        state.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppTheme.color.primary)
            }
        }
        state.errorMessage != null && !state.isSubmitting -> {
            EditUserErrorState(
                message = state.errorMessage ?: "",
                onRetry = viewModel::onRetryClicked,
            )
        }
        else -> {
            EditUserScaffold(state = state, listener = viewModel)
        }
    }

    // Department picker dialog
    if (state.showDepartmentPicker) {
        EditDepartmentPickerDialog(
            departments = state.availableDepartments,
            onSelected  = viewModel::onDepartmentSelected,
            onDismiss   = viewModel::onDepartmentPickerDismissed,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Scaffold
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditUserScaffold(
    state: EditUserUiState,
    listener: EditUserInteractionListener,
) {
    Scaffold(
        containerColor = AppTheme.color.bg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text  = "تعديل بيانات المستخدم",
                        color = AppTheme.color.primaryDark,
                        style = AppTheme.textStyle.headline.small,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = listener::onBackClicked) {
                        Icon(
                            painter            = painterResource(R.drawable.ic_arrow_forward),
                            contentDescription = "رجوع",
                            tint               = AppTheme.color.primaryDark,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.color.bg,
                ),
            )
        },
    ) { innerPadding ->
        EditUserContent(
            state    = state,
            listener = listener,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Content
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EditUserContent(
    state: EditUserUiState,
    listener: EditUserInteractionListener,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        Spacer(Modifier.height(8.dp))

        // ── User type badge (read-only) ───────────────────────────────────────
        EditUserTypeBadge(userType = state.userType)
        Spacer(Modifier.height(16.dp))

        // ── Common fields ─────────────────────────────────────────────────────
        LabelInputField(
            text          = state.name,
            onValueChange = listener::onNameChanged,
            hintText      = "الاسم الكامل",
            label         = "الاسم",
            icon          = painterResource(R.drawable.ic_person),
            isError       = state.nameError != null,
            errorMessage  = state.nameError,
        )
        Spacer(Modifier.height(12.dp))

        LabelInputField(
            text          = state.email,
            onValueChange = listener::onEmailChanged,
            hintText      = "البريد الإلكتروني",
            label         = "البريد الإلكتروني",
            icon          = painterResource(noor.serry.designsystem.R.drawable.mail),
            isError       = state.emailError != null,
            errorMessage  = state.emailError,
        )
        Spacer(Modifier.height(12.dp))

        LabelInputField(
            text          = state.phone,
            onValueChange = listener::onPhoneChanged,
            hintText      = "رقم الهاتف (اختياري)",
            label         = "الهاتف",
            icon          = painterResource(R.drawable.ic_phone),
        )
        Spacer(Modifier.height(12.dp))

        // ── Department picker ─────────────────────────────────────────────────
        Text(
            text     = "القسم",
            color    = AppTheme.color.primaryDark,
            style    = AppTheme.textStyle.body.small,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        EditDepartmentPickerField(
            selectedName = state.departmentName.ifBlank { "اختر القسم" },
            onClick      = listener::onDepartmentPickerOpened,
        )
        Spacer(Modifier.height(12.dp))

        // ── Active / Inactive toggle ──────────────────────────────────────────
        EditActiveToggle(
            isActive  = state.isActive,
            onToggled = listener::onActiveToggled,
        )
        Spacer(Modifier.height(12.dp))

        // ── Student-only fields ───────────────────────────────────────────────
        AnimatedVisibility(
            visible = state.userType == UsersAdminUiState.UserType.STUDENT,
            enter   = fadeIn(tween(200)) + slideInVertically(tween(200, easing = FastOutSlowInEasing)),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LabelInputField(
                    text          = state.level,
                    onValueChange = listener::onLevelChanged,
                    hintText      = "1",
                    label         = "المستوى الدراسي",
                    icon          = painterResource(R.drawable.badge),
                )
                LabelInputField(
                    text          = state.enrollmentYear,
                    onValueChange = listener::onEnrollmentYearChanged,
                    hintText      = "2025",
                    label         = "سنة الالتحاق",
                    icon          = painterResource(R.drawable.ic_clock),
                )
                Spacer(Modifier.height(0.dp))
            }
        }

        // ── Employee / Doctor-only fields ─────────────────────────────────────
        AnimatedVisibility(
            visible = state.userType != UsersAdminUiState.UserType.STUDENT,
            enter   = fadeIn(tween(200)) + slideInVertically(tween(200, easing = FastOutSlowInEasing)),
        ) {
            Column {
                LabelInputField(
                    text          = state.roleTitle,
                    onValueChange = listener::onRoleTitleChanged,
                    hintText      = "مثال: أستاذ مشارك",
                    label         = "المسمى الوظيفي",
                    icon          = painterResource(R.drawable.badge),
                    isError       = state.roleTitleError != null,
                    errorMessage  = state.roleTitleError,
                )
                Spacer(Modifier.height(12.dp))
            }
        }

        // ── API-level error banner ────────────────────────────────────────────
        if (state.errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFEE2E2))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Text(
                    text  = state.errorMessage,
                    color = Color(0xFFDC2626),
                    style = AppTheme.textStyle.body.small,
                )
            }
            Spacer(Modifier.height(12.dp))
        }

        // ── Action buttons ────────────────────────────────────────────────────
        Spacer(Modifier.height(8.dp))
        BaseButton(
            text              = if (state.isSubmitting) "جاري الحفظ..." else "حفظ التعديلات",
            onClick           = listener::onSaveClicked,
            roundedCornerSize = 10.dp,
            isEnable          = !state.isSubmitting,
        )
        Spacer(Modifier.height(10.dp))
        BaseButton(
            text              = "إلغاء",
            onClick           = listener::onBackClicked,
            roundedCornerSize = 10.dp,
            backgroundColor   = AppTheme.color.bgHover,
            textColor         = AppTheme.color.textSecondary,
            borderColor       = AppTheme.color.border,
            borderWidth       = 1.dp,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Small reusable composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EditUserTypeBadge(userType: UsersAdminUiState.UserType) {
    val (bg, tint, label) = when (userType) {
        UsersAdminUiState.UserType.STUDENT -> Triple(Color(0xFFEFF6FF), Color(0xFF1D4ED8), "🎓 طالب")
        UsersAdminUiState.UserType.DOCTOR  -> Triple(Color(0xFFFEF9C3), Color(0xFFB45309), "👨‍🏫 مدرس")
        UsersAdminUiState.UserType.ADMIN   -> Triple(Color(0xFFF3F4F6), Color(0xFF374151), "⚙️ موظف")
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.dp, tint.copy(alpha = .25f), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(
            text  = label,
            color = tint,
            style = AppTheme.textStyle.label.medium.copy(fontWeight = FontWeight.Bold),
        )
    }
}

@Composable
private fun EditActiveToggle(isActive: Boolean, onToggled: (Boolean) -> Unit) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppTheme.color.bgHover)
            .border(1.dp, AppTheme.color.border, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Switch(
            checked         = isActive,
            onCheckedChange = onToggled,
            colors          = SwitchDefaults.colors(
                checkedThumbColor       = AppTheme.color.bg,
                checkedTrackColor       = AppTheme.color.primary,
                uncheckedThumbColor     = AppTheme.color.bg,
                uncheckedTrackColor     = Color(0xFFDC2626),
            ),
        )
        Text(
            text  = if (isActive) "الحساب نشط" else "الحساب موقوف",
            color = if (isActive) Color(0xFF16A34A) else Color(0xFFDC2626),
            style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Medium),
        )
    }
}

@Composable
private fun EditDepartmentPickerField(
    selectedName: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppTheme.color.bgHover)
            .border(1.dp, AppTheme.color.border, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text  = selectedName,
            color = if (selectedName == "اختر القسم")
                AppTheme.color.textSecondary else AppTheme.color.text,
            style = AppTheme.textStyle.body.small,
        )
        Icon(
            painter            = painterResource(R.drawable.university),
            contentDescription = null,
            tint               = AppTheme.color.textSecondary,
            modifier           = Modifier.size(18.dp),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Department picker dialog  (same pattern as AddUserScreen)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EditDepartmentPickerDialog(
    departments: List<UsersAdminUiState.DepartmentFilterItem>,
    onSelected: (UsersAdminUiState.DepartmentFilterItem) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text  = "اختر القسم",
                color = AppTheme.color.text,
                style = AppTheme.textStyle.body.large.copy(fontWeight = FontWeight.Bold),
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                departments.forEach { dept ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelected(dept) }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                    ) {
                        Text(
                            text  = dept.name,
                            color = AppTheme.color.text,
                            style = AppTheme.textStyle.body.small,
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppTheme.color.bgHover)
                    .clickable(onClick = onDismiss)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Text(
                    text  = "إلغاء",
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.label.large,
                )
            }
        },
        containerColor = AppTheme.color.bg,
        shape          = RoundedCornerShape(16.dp),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Error state
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EditUserErrorState(message: String, onRetry: () -> Unit) {
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
                    .clickable(onClick = onRetry)
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
// Effects handler
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HandleEditEffects(
    effects: Flow<EditUserEffect>,
) {
    val navBackStack = UniversityAdminBackStackProvider.current
    LaunchedEffect(Unit) {
        effects.collectLatest { effect ->
            when (effect) {
                is EditUserEffect.UpdatedSuccessfully -> {
                    navBackStack.removeLastOrNull()
                }
                is EditUserEffect.NavigateBack -> {
                    navBackStack.removeLastOrNull()
                }
                is EditUserEffect.ShowError -> {
                    // show toast / snackbar
                }
            }
        }
    }
}
