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
import kotlinx.coroutines.flow.collectLatest
import noor.serry.designsystem.components.BaseButton
import noor.serry.designsystem.components.LabelInputField
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

// ─────────────────────────────────────────────────────────────────────────────
// Public entry-point
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Add-User bottom sheet backed by its own [AddUserViewModel].
 *
 * The sheet owns its form state via [AddUserUiState] and interacts with the
 * parent screen only through two callbacks:
 *   • [onDismiss]        – sheet should be hidden (cancel or successful submit)
 *   • [onUserCreated]    – called after a successful API response so the list
 *                          screen can refresh its data.
 *
 * @param departments   Pre-fetched list passed from [UsersAdminUiState] so the
 *                      sheet ViewModel does not fetch it independently.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserSheet(
    departments: List<UsersAdminUiState.DepartmentFilterItem>,
    onDismiss: () -> Unit,
    onUserCreated: (message: String) -> Unit = {},
    viewModel: AddUserViewModel = koinViewModel(
        parameters = { parametersOf(departments) },
    ),
) {
    val state by viewModel.state.collectAsState()

    // ── Effect handler ────────────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is AddUserEffect.UserCreatedSuccessfully -> {
                    onUserCreated(effect.message)
                    onDismiss()
                }
                is AddUserEffect.ShowError -> { /* parent snackbar / toast */ }
                is AddUserEffect.Dismissed -> onDismiss()
            }
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest  = viewModel::onDismissClicked,
        sheetState        = sheetState,
        containerColor    = AppTheme.color.bg,
        shape             = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle        = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 4.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(AppTheme.color.border),
            )
        },
    ) {
        AddUserSheetContent(state = state, listener = viewModel)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sheet content  (purely driven by [AddUserUiState] + [AddUserInteractionListener])
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AddUserSheetContent(
    state: AddUserUiState,
    listener: AddUserInteractionListener,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        // ── Sheet title ───────────────────────────────────────────────────────
        Text(
            text     = "إضافة مستخدم جديد",
            color    = AppTheme.color.primaryDark,
            style    = AppTheme.textStyle.headline.small,
            modifier = Modifier.padding(top = 8.dp, bottom = 20.dp),
        )

        // ── Role selector ─────────────────────────────────────────────────────
        Text(
            text  = "نوع المستخدم",
            color = AppTheme.color.primaryDark,
            style = AppTheme.textStyle.body.small,
        )
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            NewUserRole.entries.forEach { role ->
                NewUserRoleChip(
                    label      = role.labelAr,
                    isSelected = state.selectedRole == role,
                    onClick    = { listener.onRoleChanged(role) },
                    modifier   = Modifier.weight(1f),
                )
            }
        }

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
            hintText      = "البريد الإلكتروني الجامعي",
            label         = "البريد الإلكتروني",
            icon          = painterResource(noor.serry.designsystem.R.drawable.mail),
            isError       = state.emailError != null,
            errorMessage  = state.emailError,
        )
        Spacer(Modifier.height(12.dp))

        LabelInputField(
            text          = state.password,
            onValueChange = listener::onPasswordChanged,
            hintText      = "كلمة المرور",
            label         = "كلمة المرور",
            icon          = painterResource(R.drawable.lock),
            isPassword    = true,
            isError       = state.passwordError != null,
            errorMessage  = state.passwordError,
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
        DepartmentPickerField(
            selectedName = state.departmentName.ifBlank { "اختر القسم" },
            isError      = state.departmentError != null,
            errorMessage = state.departmentError,
            onClick      = listener::onDepartmentPickerOpened,
        )
        Spacer(Modifier.height(12.dp))

        // ── Student-only fields ───────────────────────────────────────────────
        AnimatedVisibility(
            visible = state.selectedRole == NewUserRole.STUDENT,
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

        // ── Employee-only fields ──────────────────────────────────────────────
        AnimatedVisibility(
            visible = state.selectedRole != NewUserRole.STUDENT,
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

        // ── Buttons ───────────────────────────────────────────────────────────
        Spacer(Modifier.height(8.dp))
        BaseButton(
            text              = if (state.isSubmitting) "جاري الإضافة..." else "إضافة المستخدم",
            onClick           = listener::onSubmitClicked,
            roundedCornerSize = 10.dp,
            isEnable           = !state.isSubmitting,
        )
        Spacer(Modifier.height(10.dp))
        BaseButton(
            text              = "إلغاء",
            onClick           = listener::onDismissClicked,
            roundedCornerSize = 10.dp,
            backgroundColor   = AppTheme.color.bgHover,
            textColor         = AppTheme.color.textSecondary,
            borderColor       = AppTheme.color.border,
            borderWidth       = 1.dp,
        )
    }

    // ── Department picker dialog ───────────────────────────────────────────────
    if (state.showDepartmentPicker) {
        DepartmentPickerDialog(
            departments = state.availableDepartments,
            onSelected  = listener::onDepartmentSelected,
            onDismiss   = listener::onDepartmentPickerDismissed,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Role chip
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun NewUserRoleChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) AppTheme.color.primary else AppTheme.color.bgHover)
            .border(
                width = 1.dp,
                color = if (isSelected) AppTheme.color.primary else AppTheme.color.border,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text  = label,
            color = if (isSelected) AppTheme.color.bg else AppTheme.color.textSecondary,
            style = AppTheme.textStyle.label.medium.copy(fontWeight = FontWeight.Medium),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Department picker trigger field
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DepartmentPickerField(
    selectedName: String,
    isError: Boolean,
    errorMessage: String?,
    onClick: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(AppTheme.color.bgHover)
                .border(
                    width = 1.dp,
                    color = if (isError) AppTheme.color.error else AppTheme.color.border,
                    shape = RoundedCornerShape(12.dp),
                )
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
        if (isError && errorMessage != null) {
            Text(
                text     = errorMessage,
                color    = AppTheme.color.error,
                style    = AppTheme.textStyle.label.small,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Department picker dialog
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DepartmentPickerDialog(
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
