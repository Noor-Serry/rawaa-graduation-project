package noor.serry.rawaa.ui.screens.profile_teacher

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
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R

@Composable
fun ProfileTeacherScreen(
    onBack: () -> Unit = {},
    viewModel: ProfileTeacherViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    HandleEffects(effects = viewModel.effect)

    ProfileTeacherContent(state = state, interactionListener = viewModel, onBack = onBack)
}

@Composable
private fun ProfileTeacherContent(
    state: ProfileTeacherUiState,
    interactionListener: ProfileTeacherInteractionListener,
    onBack: () -> Unit,
) {
    when {
        state.isLoading -> Box(Modifier.fillMaxSize().background(AppTheme.color.bgHover), contentAlignment = Alignment.Center) {
            Text(text = "جاري التحميل...", color = AppTheme.color.textSecondary, style = AppTheme.textStyle.body.medium)
        }
        state.errorMessage != null -> Box(Modifier.fillMaxSize().background(AppTheme.color.bgHover), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = state.errorMessage!!, color = AppTheme.color.error, style = AppTheme.textStyle.body.medium)
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(AppTheme.color.primary)
                        .clickAnimation { interactionListener.load() }.padding(horizontal = 24.dp, vertical = 10.dp)
                ) {
                    Text(text = "إعادة المحاولة", color = AppTheme.color.bg, style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize().background(AppTheme.color.bgHover),
        ) {
            item {
                ProfileTeacherHeader(
                    state = state,
                    onBack = onBack,
                    onEditClick = interactionListener::onEditProfileClick,
                )
            }
            item {
                ProfileSectionCard(
                    modifier = Modifier.padding(horizontal = 16.dp).padding(top = 24.dp)
                ) {
                    ProfileSectionHeader(title = "المعلومات الأكاديمية", iconRes = R.drawable.ic_book)
                    Spacer(Modifier.height(16.dp))
                    ProfileInfoRow(label = "رقم الموظف", value = state.employeeId)
                    ProfileDivider()
                    ProfileInfoRow(label = "القسم", value = state.department)
                    ProfileDivider()
                    ProfileInfoRow(label = "التخصص", value = state.specialization)
                    ProfileDivider()
                    ProfileInfoRow(label = "الدرجة العلمية", value = state.degree)
                    if (state.experienceYears > 0) {
                        ProfileDivider()
                        ProfileInfoRow(label = "سنوات الخبرة", value = "${state.experienceYears} سنة")
                    }
                }
            }
            if (state.office.isNotEmpty() || state.officeHours.isNotEmpty()) {
                item {
                    ProfileSectionCard(
                        modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)
                    ) {
                        ProfileSectionHeader(title = "معلومات المكتب", iconRes = R.drawable.ic_clock)
                        Spacer(Modifier.height(16.dp))
                        if (state.office.isNotEmpty()) ProfileInfoRow(label = "المكتب", value = state.office)
                        if (state.office.isNotEmpty() && state.officeHours.isNotEmpty()) ProfileDivider()
                        if (state.officeHours.isNotEmpty()) ProfileInfoRow(label = "ساعات الدوام", value = state.officeHours)
                    }
                }
            }
            item {
                ProfileSectionCard(
                    modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)
                ) {
                    ProfileSectionHeader(title = "معلومات التواصل", iconRes = R.drawable.ic_info)
                    Spacer(Modifier.height(16.dp))
                    ProfileInfoRow(label = "البريد الإلكتروني", value = state.email)
                    if (state.phone.isNotEmpty()) {
                        ProfileDivider()
                        ProfileInfoRow(label = "الهاتف", value = state.phone)
                    }
                }
            }
            if (state.currentCourses.isNotEmpty()) {
                item {
                    ProfileSectionCard(
                        modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)
                    ) {
                        ProfileSectionHeader(title = "المقررات الحالية", iconRes = R.drawable.ic_book)
                        Spacer(Modifier.height(16.dp))
                        state.currentCourses.forEachIndexed { index, course ->
                            if (index > 0) ProfileDivider()
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = course.name, color = AppTheme.color.text, style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold))
                                    Text(text = course.code, color = AppTheme.color.textSecondary, style = AppTheme.textStyle.label.medium, modifier = Modifier.padding(top = 2.dp))
                                }
                                Text(text = "${course.studentsCount} طالب", color = AppTheme.color.primary, style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun ProfileTeacherHeader(
    state: ProfileTeacherUiState,
    onBack: () -> Unit,
    onEditClick: () -> Unit,
) {
    Box {
        Column(
            modifier = Modifier.fillMaxWidth().height(220.dp)
                .background(brush = verticalGradient(listOf(AppTheme.color.primary, AppTheme.color.primaryLight)))
                .padding(horizontal = 24.dp).padding(top = 48.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                        .background(AppTheme.color.bg.copy(alpha = .15f)).clickAnimation { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painter = painterResource(R.drawable.ic_arrow_forward), contentDescription = "رجوع", tint = AppTheme.color.bg, modifier = Modifier.size(18.dp))
                }
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(AppTheme.color.secondary)
                        .clickAnimation { onEditClick() }.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = "تعديل", color = AppTheme.color.primaryDark, style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter).fillMaxWidth()
                .padding(horizontal = 24.dp).offset(y = 60.dp)
                .dropShadow(RoundedCornerShape(24.dp), Shadow(radius = 10.dp, spread = -6.dp, color = AppTheme.color.text.copy(alpha = .1f), offset = DpOffset(0.dp, 8.dp)))
                .dropShadow(RoundedCornerShape(24.dp), Shadow(radius = 25.dp, spread = -5.dp, color = AppTheme.color.text.copy(alpha = .1f), offset = DpOffset(0.dp, 20.dp)))
                .background(AppTheme.color.bg, RoundedCornerShape(24.dp)).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier.size(72.dp).clip(CircleShape).background(AppTheme.color.primary.copy(alpha = .15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = state.name.firstOrNull()?.toString() ?: "?", color = AppTheme.color.primary, style = AppTheme.textStyle.headline.small)
            }
            Text(text = state.name, color = AppTheme.color.text, style = AppTheme.textStyle.headline.small)
            if (state.degree.isNotEmpty()) {
                Text(text = state.degree, color = AppTheme.color.primary, style = AppTheme.textStyle.body.medium)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                ProfileStatBadge(value = state.activeCourses.toString(), label = "مقررات")
                ProfileStatBadge(value = state.totalStudents.toString(), label = "طلاب")
                if (state.experienceYears > 0) ProfileStatBadge(value = "${state.experienceYears} سنة", label = "خبرة")
            }
        }
    }
}

@Composable
private fun ProfileStatBadge(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = AppTheme.color.primary, style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold))
        Text(text = label, color = AppTheme.color.textSecondary, style = AppTheme.textStyle.label.medium)
    }
}

@Composable
private fun ProfileSectionCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg).border(1.17.dp, AppTheme.color.border, RoundedCornerShape(16.dp)).padding(17.dp)
    ) { content() }
}

@Composable
private fun ProfileSectionHeader(title: String, iconRes: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(AppTheme.color.primary.copy(alpha = .1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(painter = painterResource(iconRes), contentDescription = null, tint = AppTheme.color.primary, modifier = Modifier.size(16.dp))
        }
        Text(text = title, color = AppTheme.color.primaryDark, style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold))
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    if (value.isEmpty()) return
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = AppTheme.color.textSecondary, style = AppTheme.textStyle.body.small)
        Text(text = value, color = AppTheme.color.text, style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Medium))
    }
}

@Composable
private fun ProfileDivider() {
    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(AppTheme.color.border))
}

@Composable
private fun HandleEffects(effects: Flow<ProfileTeacherEffect>) {
    LaunchedEffect(Unit) {
        effects.collectLatest { effect ->
            when (effect) {
                ProfileTeacherEffect.NavigateToEditProfile -> { /* TODO */ }
            }
        }
    }
}
