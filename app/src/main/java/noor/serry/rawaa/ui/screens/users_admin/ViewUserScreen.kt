package noor.serry.rawaa.ui.screens.users_admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.navigation.university_admin.UniversityAdminBackStackProvider
import noor.serry.rawaa.ui.navigation.university_admin.UniversityAdminRouteKeys

// ─────────────────────────────────────────────────────────────────────────────
// Entry point
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ViewUserScreen(
    userId: Int,
    userType: UsersAdminUiState.UserType,
    viewModel: ViewUserViewModel = koinViewModel(parameters = { parametersOf(userId, userType) }),
) {
    val state    by viewModel.state.collectAsState()

    HandleViewEffects(effects = viewModel.effect)

    when {
        state.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppTheme.color.primary)
            }
        }
        state.errorMessage != null -> {
            ViewUserErrorState(
                message = state.errorMessage ?: "",
                onRetry = viewModel::onRetryClicked,
            )
        }
        else -> {
            ViewUserScaffold(state = state, listener = viewModel)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Scaffold
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ViewUserScaffold(
    state: ViewUserUiState,
    listener: ViewUserInteractionListener,
) {
    Scaffold(
        containerColor = AppTheme.color.bgHover,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text  = "عرض الملف الشخصي",
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
                actions = {
                    // تعديل action in the top bar
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AppTheme.color.primary.copy(alpha = .12f))
                            .clickable(onClick = listener::onEditClicked)
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter  = painterResource(R.drawable.ic_edit),
                                tint     = AppTheme.color.primary,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                text  = "تعديل",
                                color = AppTheme.color.primary,
                                style = AppTheme.textStyle.label.medium.copy(fontWeight = FontWeight.Bold),
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppTheme.color.bg),
            )
        },
    ) { innerPadding ->
        ViewUserContent(
            state    = state,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Content
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ViewUserContent(
    state: ViewUserUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // ── Profile hero card ─────────────────────────────────────────────────
        ProfileHeroCard(state = state)

        // ── Contact info card ─────────────────────────────────────────────────
        InfoCard(title = "معلومات التواصل") {
            ProfileInfoRow(iconRes = noor.serry.designsystem.R.drawable.mail,  label = "البريد الإلكتروني", value = state.email)
            if (state.phone.isNotBlank())
                ProfileInfoRow(iconRes = R.drawable.ic_phone, label = "رقم الهاتف", value = state.phone)
        }

        // ── Role-specific card ────────────────────────────────────────────────
        when (state.userType) {
            UsersAdminUiState.UserType.STUDENT -> StudentInfoCard(state)
            UsersAdminUiState.UserType.DOCTOR  -> EmployeeInfoCard(state, isDoctor = true)
            UsersAdminUiState.UserType.ADMIN   -> EmployeeInfoCard(state, isDoctor = false)
        }

        // ── Account info card ─────────────────────────────────────────────────
        InfoCard(title = "معلومات الحساب") {
            if (state.createdAt.isNotBlank())
                ProfileInfoRow(iconRes = R.drawable.ic_clock, label = "تاريخ الانضمام", value = formatJoinDate(state.createdAt))
            if (state.departmentName.isNotBlank())
                ProfileInfoRow(iconRes = R.drawable.university, label = "القسم", value = state.departmentName)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Profile hero card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileHeroCard(state: ViewUserUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(AppTheme.color.bg),
    ) {
        // Coloured top strip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(AppTheme.color.primary),
        )

        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(top = 44.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(avatarColorFromType(state.userType))
                    .border(3.dp, AppTheme.color.bg, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text  = state.name.take(1),
                    color = Color.White,
                    style = AppTheme.textStyle.headline.small.copy(fontSize = 28.sp),
                )
            }

            Spacer(Modifier.height(4.dp))

            // Name
            Text(
                text      = state.name,
                color     = AppTheme.color.text,
                style     = AppTheme.textStyle.body.large.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
            )

            // Badges row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                ViewRoleBadge(userType = state.userType, role = state.role)
                ViewActiveBadge(isActive = state.isActive)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Generic section card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun InfoCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        Text(
            text     = title,
            color    = AppTheme.color.primaryDark,
            style    = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 12.dp),
        )
        Divider(color = AppTheme.color.border.copy(alpha = .5f), thickness = 0.8.dp)
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun ProfileInfoRow(iconRes: Int, label: String, value: String) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment     = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Value (RTL: right side)
        Text(
            text     = value,
            color    = AppTheme.color.text,
            style    = AppTheme.textStyle.body.small,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
        )
        // Label + icon (LTR: left side)
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment     = Alignment.CenterVertically,
            modifier              = Modifier.padding(start = 12.dp),
        ) {
            Text(
                text  = label,
                color = AppTheme.color.textSecondary,
                style = AppTheme.textStyle.label.medium,
            )
            Icon(
                painter  = painterResource(iconRes),
                tint     = AppTheme.color.textSecondary,
                modifier = Modifier.size(15.dp),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Role-specific cards
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun StudentInfoCard(state: ViewUserUiState) {
    InfoCard(title = "بيانات الطالب") {
        state.level?.let {
            ProfileInfoRow(iconRes = R.drawable.badge, label = "المستوى الدراسي", value = "المستوى $it")
        }
        state.enrollmentYear?.let {
            ProfileInfoRow(iconRes = R.drawable.ic_clock, label = "سنة الالتحاق", value = "$it")
        }
        state.gpa?.let {
            ProfileInfoRow(iconRes = R.drawable.badge, label = "المعدل التراكمي (GPA)", value = "%.2f".format(it))
        }
    }
}

@Composable
private fun EmployeeInfoCard(state: ViewUserUiState, isDoctor: Boolean) {
    InfoCard(title = if (isDoctor) "بيانات أعضاء هيئة التدريس" else "بيانات الموظف") {
        if (!state.roleTitle.isNullOrBlank())
            ProfileInfoRow(iconRes = R.drawable.badge, label = "المسمى الوظيفي", value = state.roleTitle)
        ProfileInfoRow(
            iconRes = R.drawable.university,
            label   = "الدور",
            value   = if (isDoctor) "دكتور / مدرس" else "موظف إداري",
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Badges (local copies – avoid sharing private fns from the list screen)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ViewRoleBadge(userType: UsersAdminUiState.UserType, role: String) {
    val (bg, tint, label) = when (userType) {
        UsersAdminUiState.UserType.STUDENT -> Triple(Color(0xFFEFF6FF), Color(0xFF1D4ED8), "🎓 طالب")
        UsersAdminUiState.UserType.DOCTOR  -> Triple(Color(0xFFFEF9C3), Color(0xFFB45309), "👨‍🏫 مدرس")
        UsersAdminUiState.UserType.ADMIN   -> Triple(Color(0xFFF3F4F6), Color(0xFF374151), role)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(text = label, color = tint, style = AppTheme.textStyle.label.small)
    }
}

@Composable
private fun ViewActiveBadge(isActive: Boolean) {
    val bg    = if (isActive) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
    val tint  = if (isActive) Color(0xFF16A34A) else Color(0xFFDC2626)
    val label = if (isActive) "نشط" else "موقوف"
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(text = label, color = tint, style = AppTheme.textStyle.label.small)
    }
}

private fun avatarColorFromType(type: UsersAdminUiState.UserType): Color = when (type) {
    UsersAdminUiState.UserType.STUDENT -> Color(0xFF1D4ED8)
    UsersAdminUiState.UserType.DOCTOR  -> Color(0xFFB45309)
    UsersAdminUiState.UserType.ADMIN   -> Color(0xFF374151)
}

// ─────────────────────────────────────────────────────────────────────────────
// Error state
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ViewUserErrorState(message: String, onRetry: () -> Unit) {
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
private fun HandleViewEffects(
    effects: Flow<ViewUserEffect>,
) {
    val navBackStack = UniversityAdminBackStackProvider.current
    LaunchedEffect(Unit) {
        effects.collectLatest { effect ->
            when (effect) {
                is ViewUserEffect.NavigateBack -> {
                    navBackStack.removeLastOrNull()
                }
                is ViewUserEffect.NavigateToEdit -> {
                    navBackStack.add(UniversityAdminRouteKeys.EditUser(effect.userId, effect.userType))
                }
                is ViewUserEffect.ShowError -> {
                    // show toast / snackbar
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

private fun formatJoinDate(createdAt: String): String =
    createdAt.take(10).ifBlank { createdAt }
