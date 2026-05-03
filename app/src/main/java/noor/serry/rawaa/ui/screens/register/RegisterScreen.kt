package noor.serry.rawaa.ui.screens.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.BackStackProvider
import noor.serry.rawaa.ui.navigation.base.AppRoute
import noor.serry.rawaa.ui.screens.register.component.AccountSecurityPage
import noor.serry.rawaa.ui.screens.register.component.AcademicInfoPage
import noor.serry.rawaa.ui.screens.register.component.PersonalInfoPage
import noor.serry.rawaa.ui.screens.register.component.RegisterHeader
import noor.serry.rawaa.ui.screens.login.component.FormContainer

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    HandleEffects(
        effects = viewModel.effect
    )

    RegisterContent(
        state = state,
        interactionListener = viewModel
    )
}

@Composable
private fun RegisterContent(
    state: RegisterUiState,
    interactionListener: RegisterInteractionListener
) {
    val pagerState = rememberPagerState(pageCount = { 3 })

    LaunchedEffect(state.currentPage) {
        pagerState.animateScrollToPage(state.currentPage)
    }

    Box(
        modifier = Modifier
            .background(AppTheme.color.primary)
            .statusBarsPadding()
            .fillMaxSize()
            .background(AppTheme.color.bg)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        RegisterHeader(
            onBackClick = interactionListener::onPreviousPage,
            currentStep = state.currentPage
        )

        FormContainer(
            modifier = Modifier
                .padding(top = 224.dp)
        ) {
            // HorizontalPager with user scroll disabled — navigation only via ViewModel
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
            ) { page ->
                when (page) {
                    0 -> PersonalInfoPage(
                        fullName = state.fullName,
                        email = state.email,
                        phone = state.phone,
                        onFullNameChange = interactionListener::onFullNameChange,
                        onEmailChange = interactionListener::onEmailChange,
                        onPhoneChange = interactionListener::onPhoneChange,
                        onNext = interactionListener::onNextPage,
                        onGoogleSignUp = interactionListener::onGoogleSignUp,
                        onNavigateToLogin = interactionListener::onNavigateToLogin,
                    )

                    1 -> AcademicInfoPage(
                        university = state.university,
                        selectedRole = state.selectedRole,
                        departments = state.departments,
                        isDepartmentsLoading = state.isDepartmentsLoading,
                        selectedDepartment = state.selectedDepartment,
                        departmentError = state.departmentError,
                        roleTitle = state.roleTitle,
                        roleTitleError = state.roleTitleError,
                        onUniversityChange = interactionListener::onUniversityChange,
                        onRoleSelected = interactionListener::onRoleSelected,
                        onDepartmentSelected = interactionListener::onDepartmentSelected,
                        onRoleTitleChange = interactionListener::onRoleTitleChange,
                        onNext = interactionListener::onNextPage,
                    )

                    2 -> AccountSecurityPage(
                        password = state.password,
                        confirmPassword = state.confirmPassword,
                        passwordsMatch = state.passwordsMatch,
                        onPasswordChange = interactionListener::onPasswordChange,
                        onConfirmPasswordChange = interactionListener::onConfirmPasswordChange,
                        onRegister = interactionListener::onRegister,
                    )
                }
            }
        }
    }
}

@Composable
private fun HandleEffects(
    effects: Flow<RegisterEffect>
) {
    val navigationBackStack = BackStackProvider.current
    LaunchedEffect(Unit) {
        effects.collectLatest { effect ->
            when (effect) {
                RegisterEffect.NavigateToLogin -> navigationBackStack.add(AppRoute.Login)
                RegisterEffect.NavigateToHome -> {}
            }
        }
    }
}
