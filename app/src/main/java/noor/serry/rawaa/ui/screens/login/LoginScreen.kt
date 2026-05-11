package noor.serry.rawaa.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import noor.serry.designsystem.components.BaseButton
import noor.serry.designsystem.components.LabelInputField
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.BackStackProvider
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.navigation.base.AppRoute
import noor.serry.rawaa.ui.screens.login.component.ActionBanner
import noor.serry.rawaa.ui.screens.login.component.FormContainer
import noor.serry.rawaa.ui.screens.login.component.LoginFooter
import noor.serry.rawaa.ui.screens.login.component.LoginHeader
import noor.serry.rawaa.ui.screens.login.component.UserRoleCard

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    HandleEffects(effects = viewModel.effect)

    LoginContent(state = state, interactionListener = viewModel)
}

@Composable
private fun LoginContent(
    state: LoginUiState,
    interactionListener: LoginInteractionListener,
) {
    Box(
        modifier = Modifier
            .background(AppTheme.color.primary)
            .statusBarsPadding()
            .fillMaxSize()
            .background(AppTheme.color.bg)
            .navigationBarsPadding()
            .padding(bottom = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        LoginHeader()

        FormContainer(
            modifier = Modifier
                .padding(top = 168.dp)
                .padding(horizontal = 24.dp),
            tailContent = {
                LoginFooter(Modifier.padding(top = 32.dp))
            },
        ) {
            Column(
                modifier = Modifier
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(16.dp))
                    .padding(vertical = 12.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.rowaa),
                    color = AppTheme.color.primary,
                    style = AppTheme.textStyle.headline.small,
                )
                Text(
                    stringResource(R.string.egyption_unversity),
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.label.medium
                )
            }

            // ── Role selector ────────────────────────────────────────────────
            Column(Modifier.fillMaxWidth().padding(top = 32.dp)) {
                Text(
                    stringResource(R.string.login_as),
                    color = AppTheme.color.primaryDark,
                    style = AppTheme.textStyle.body.small
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LoginRole.entries.forEach { role ->
                        UserRoleCard(
                            isSelected = state.selectedRole == role,
                            label = role.label,
                            onClick = { interactionListener.onRoleSelected(role) },
                        )
                    }
                }
            }

            // ── Email ────────────────────────────────────────────────────────
            LabelInputField(
                text = state.email,
                onValueChange = interactionListener::onEmailChange,
                hintText = stringResource(R.string.unversity_email),
                label = stringResource(R.string.email),
                icon = painterResource(noor.serry.designsystem.R.drawable.mail),
                modifier = Modifier.padding(top = 24.dp),
                isError = state.emailError != null,
                errorMessage = state.emailError,
            )

            // ── Password ─────────────────────────────────────────────────────
            LabelInputField(
                text = state.password,
                onValueChange = interactionListener::onPasswordChange,
                hintText = stringResource(R.string.password),
                label = stringResource(R.string.password),
                icon = painterResource(R.drawable.lock),
                isPassword = true,
                modifier = Modifier.padding(top = 16.dp),
                isError = state.passwordError != null,
                errorMessage = state.passwordError,
            )

            // ── University slug (hidden for SuperAdmin) ───────────────────────
            if (state.selectedRole != LoginRole.ADMIN) {
                LabelInputField(
                    text = state.universitySlug,
                    onValueChange = interactionListener::onUniversitySlugChange,
                    hintText = "رمز الجامعة",
                    label = "رمز الجامعة",
                    icon = painterResource(noor.serry.designsystem.R.drawable.mail),
                    modifier = Modifier.padding(top = 16.dp),
                    isError = state.slugError != null,
                    errorMessage = state.slugError,
                )
            }

            // ── General error ─────────────────────────────────────────────────
            state.generalError?.let {
                Text(
                    text = it,
                    color = AppTheme.color.error,
                    style = AppTheme.textStyle.body.small,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // ── Forgot password + Login button ────────────────────────────────
            Column(Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.forgot_password),
                    color = AppTheme.color.primary,
                    style = AppTheme.textStyle.body.small,
                    modifier = Modifier
                        .padding(top = 18.dp)
                        .align(Alignment.End),
                )
                BaseButton(
                    text = if (state.isLoading) "..." else stringResource(R.string.login),
                    onClick = interactionListener::onLoginClick,
                    modifier = Modifier.padding(top = 32.dp),
                    roundedCornerSize = 8.dp,
                )
            }

            // ── Divider ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(
                    Modifier
                        .height(1.dp)
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .background(AppTheme.color.border)
                )
                Text(
                    text = stringResource(R.string.or),
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal)
                )
                Spacer(
                    Modifier
                        .height(1.dp)
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .background(AppTheme.color.border)
                )
            }

            // ── Google login ──────────────────────────────────────────────────
            BaseButton(
                text = stringResource(R.string.login_with_google),
                onClick = interactionListener::onGoogleLoginClick,
                modifier = Modifier.padding(top = 12.dp),
                roundedCornerSize = 12.dp,
                icon = painterResource(R.drawable.google),
                backgroundColor = AppTheme.color.bg,
                borderColor = AppTheme.color.border,
                borderWidth = 1.17.dp,
                textStyle = AppTheme.textStyle.body.medium,
                textColor = AppTheme.color.primaryDark,
                isMirror = false
            )

            ActionBanner(
                text = stringResource(R.string.do_not_have_account),
                actionText = stringResource(R.string.create_new_account),
                onActionClick = interactionListener::onNavigateToRegister,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }
}

@Composable
private fun HandleEffects(effects: Flow<LoginEffect>) {
    val backStack = BackStackProvider.current
    LaunchedEffect(Unit) {
        effects.collectLatest { effect ->
            when (effect) {
                LoginEffect.NavigateToStudentHome -> {
                    backStack.clear()
                    backStack.add(AppRoute.StudentEntry)
                }
                LoginEffect.NavigateToTeacherHome -> {
                    backStack.clear()
                    backStack.add(AppRoute.TeacherEntry)
                }
                LoginEffect.NavigateToAdminHome -> {
                    backStack.clear()
                    backStack.add(AppRoute.AdminEntry)
                }
                LoginEffect.NavigateToSuperAdminHome -> {
                    backStack.clear()
                    backStack.add(AppRoute.SuperAdminEntry)
                }
                LoginEffect.NavigateToRegister -> {
                    backStack.clear()
                    backStack.add(AppRoute.Register)
                }
                LoginEffect.NavigateToForgotPassword -> { /* TODO */ }
            }
        }
    }
}
