package noor.serry.rawaa.ui.screens.add_university_super_admin

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.navigation.admin.AdminBackStackProvider
import noor.serry.rawaa.ui.navigation.admin.AdminRouteKeys
import noor.serry.rawaa.ui.screens.universities_super_admin.UniversitiesUiState
import noor.serry.rawaa.ui.screens.universities_super_admin.UniversityFormContent
import org.koin.androidx.compose.koinViewModel

// ─────────────────────────────────────────────────────────────────────────────

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddUniversityScreen(
    viewModel: AddUniversityViewModel = koinViewModel(),
) {
    val state        by viewModel.state.collectAsState()
    val snackbarHost = remember { SnackbarHostState() }
    val backStack    = AdminBackStackProvider.current

    // ── Effect handling ───────────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AddUniversityEffect.NavigateBack,
                AddUniversityEffect.NavigateBackAfterCreate ->{
                  backStack.removeAll({it == AdminRouteKeys.Home})
                  backStack.add(AdminRouteKeys.Home)
                }

                is AddUniversityEffect.ShowSnackbar ->
                    snackbarHost.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        snackbarHost   = { SnackbarHost(snackbarHost) },
        containerColor = AppTheme.color.bgHover,
        topBar         = { AddUniversityTopBar(onBackClick = viewModel::onBackClick) },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // ── Reuse the exact same form used inside the create bottom sheet ─
            UniversityFormContent(
                title           = "إضافة جامعة جديدة",
                form            = state.toUniversityFormState(),
                isCreate        = true,
                isLoading       = state.isSubmitting,
                onConfirm       = viewModel::onSubmit,
                onDismiss       = viewModel::onBackClick,
                onNameChange    = viewModel::onNameChange,
                onNameEnChange  = viewModel::onNameEnChange,
                onSlugChange    = viewModel::onSlugChange,
                onEmailChange   = viewModel::onEmailChange,
                onPhoneChange   = viewModel::onPhoneChange,
                onAddressChange = viewModel::onAddressChange,
                onCountryChange = viewModel::onCountryChange,
                onPlanChange    = viewModel::onPlanChange,
                onExpiresChange = viewModel::onPlanExpiresAtChange,
                onMaxStudents   = viewModel::onMaxStudentsChange,
                onMaxStaff      = viewModel::onMaxStaffChange,
                onAdminName     = viewModel::onAdminNameChange,
                onAdminEmail    = viewModel::onAdminEmailChange,
                onAdminPassword = viewModel::onAdminPasswordChange,
            )

            // Full-screen loading overlay while submitting
            AnimatedVisibility(
                visible = state.isSubmitting,
                enter   = fadeIn(),
                exit    = fadeOut(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = AppTheme.color.primary)
                }
            }
        }
    }
}

// ── Top bar — mirrors the style used in UniversitiesTopBar ────────────────────

@Composable
private fun AddUniversityTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.color.bg)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        // Left spacer balances the back button so the title stays centred
        Spacer(Modifier.size(38.dp))

        Text(
            text  = "إضافة جامعة جديدة",
            color = AppTheme.color.text,
            style = AppTheme.textStyle.body.large.copy(fontWeight = FontWeight.Bold),
        )

        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(AppTheme.color.bgHover)
                .clickAnimation(onBackClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter  = painterResource(R.drawable.ic_arrow_forward),
                tint     = AppTheme.color.text,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

// ── Mapper: AddUniversityUiState → UniversityFormState ────────────────────────
// Bridges this screen's flat state into the shape UniversityFormContent expects,
// so validation errors flow through without any duplication.

private fun AddUniversityUiState.toUniversityFormState() =
    UniversitiesUiState.UniversityFormState(
        name               = name,
        nameEn             = nameEn,
        slug               = slug,
        email              = email,
        phone              = phone,
        address            = address,
        country            = country,
        plan               = plan,
        planExpiresAt      = planExpiresAt,
        maxStudents        = maxStudents,
        maxStaff           = maxStaff,
        adminName          = adminName,
        adminEmail         = adminEmail,
        adminPassword      = adminPassword,
        nameError          = nameError,
        slugError          = slugError,
        adminNameError     = adminNameError,
        adminEmailError    = adminEmailError,
        adminPasswordError = adminPasswordError,
    )
