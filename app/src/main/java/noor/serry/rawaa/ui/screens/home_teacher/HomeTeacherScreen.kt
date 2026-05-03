package noor.serry.rawaa.ui.screens.home_teacher

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
import androidx.compose.foundation.lazy.items
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
fun HomeTeacherScreen(
    onNavigateToCourses: () -> Unit = {},
    onNavigateToStudents: () -> Unit = {},
    onNavigateToGrading: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    viewModel: HomeTeacherViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    HandleEffects(
        effects = viewModel.effect,
        onNavigateToCourses = onNavigateToCourses,
        onNavigateToStudents = onNavigateToStudents,
        onNavigateToGrading = onNavigateToGrading,
    )

    HomeTeacherContent(state = state, interactionListener = viewModel)
}

@Composable
private fun HomeTeacherContent(
    state: HomeTeacherUiState,
    interactionListener: HomeTeacherInteractionListener,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(AppTheme.color.bgHover),
    ) {
        item {
            TeacherHeader(
                teacherName = state.teacherName,
                activeCourses = state.activeCourses,
                totalStudents = state.totalStudents,
                pendingTasks = state.pendingTasks,
                modifier = Modifier.padding(bottom = 40.dp)
            )
        }

        item {
            QuickActionsRow(
                interactionListener = interactionListener,
                modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)
            )
        }

        if (state.todaySessions.isNotEmpty()) {
            item {
                TeacherSectionHeader(
                    title = "جدول اليوم",
                    onActionClick = interactionListener::onViewAllScheduleClick,
                    modifier = Modifier.padding(horizontal = 16.dp).padding(top = 24.dp)
                )
            }
            items(state.todaySessions, key = { it.id }) { session ->
                TeacherSessionCard(
                    session = session,
                    modifier = Modifier.padding(horizontal = 16.dp).padding(top = 12.dp)
                )
            }
        }

        if (state.courses.isNotEmpty()) {
            item {
                TeacherSectionHeader(
                    title = "مقرراتي",
                    onActionClick = interactionListener::onViewAllCoursesClick,
                    modifier = Modifier.padding(horizontal = 16.dp).padding(top = 24.dp)
                )
            }
            items(state.courses, key = { it.id }) { course ->
                TeacherCourseSummaryCard(
                    course = course,
                    onManageClick = { interactionListener.onManageCourseClick(course.id) },
                    modifier = Modifier.padding(horizontal = 16.dp).padding(top = 12.dp)
                )
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun TeacherHeader(
    teacherName: String,
    activeCourses: Int,
    totalStudents: Int,
    pendingTasks: Int,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth().height(192.dp)
                .background(brush = verticalGradient(listOf(AppTheme.color.primary, AppTheme.color.primaryLight)))
                .padding(horizontal = 24.dp).padding(top = 48.dp)
        ) {
            Text("مرحباً، $teacherName", color = AppTheme.color.bg, style = AppTheme.textStyle.headline.small, modifier = Modifier.fillMaxWidth())
            Text("لوحة تحكم الدكتور", color = AppTheme.color.bgSecondary,
                style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Normal),
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter).fillMaxWidth()
                .padding(horizontal = 24.dp).offset(y = 40.dp)
                .dropShadow(RoundedCornerShape(24.dp), Shadow(radius = 10.dp, spread = -6.dp, color = AppTheme.color.text.copy(alpha = .1f), offset = DpOffset(0.dp, 8.dp)))
                .dropShadow(RoundedCornerShape(24.dp), Shadow(radius = 25.dp, spread = -5.dp, color = AppTheme.color.text.copy(alpha = .1f), offset = DpOffset(0.dp, 20.dp)))
                .background(AppTheme.color.bg, RoundedCornerShape(16.dp)).padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TeacherStatItem(value = activeCourses.toString(), label = "مقرر نشط", modifier = Modifier.weight(1f))
            TeacherStatItem(value = totalStudents.toString(), label = "طالب", modifier = Modifier.weight(1f))
            TeacherStatItem(value = pendingTasks.toString(), label = "مهمة معلقة", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun TeacherStatItem(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = value, color = AppTheme.color.primary, style = AppTheme.textStyle.headline.small)
        Text(text = label, color = AppTheme.color.textSecondary, style = AppTheme.textStyle.label.medium)
    }
}

@Composable
private fun QuickActionsRow(interactionListener: HomeTeacherInteractionListener, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        QuickActionCard("إضافة مقرر", R.drawable.ic_book, interactionListener::onAddCourseClick, Modifier.weight(1f))
        QuickActionCard("تصحيح", R.drawable.ic_grades, interactionListener::onGradingClick, Modifier.weight(1f))
        QuickActionCard("تقارير", R.drawable.ic_info, interactionListener::onReportsClick, Modifier.weight(1f))
    }
}

@Composable
private fun QuickActionCard(label: String, iconRes: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg)
            .border(1.17.dp, AppTheme.color.border, RoundedCornerShape(16.dp))
            .clickAnimation { onClick() }.padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(AppTheme.color.primary.copy(alpha = .1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(painter = painterResource(iconRes), contentDescription = null, tint = AppTheme.color.primary, modifier = Modifier.size(20.dp))
        }
        Text(text = label, color = AppTheme.color.text, style = AppTheme.textStyle.label.medium.copy(fontWeight = FontWeight.Bold))
    }
}

@Composable
private fun TeacherSectionHeader(title: String, onActionClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, color = AppTheme.color.text, style = AppTheme.textStyle.headline.small)
        Text(text = "عرض الكل", color = AppTheme.color.textSecondary, style = AppTheme.textStyle.body.small, modifier = Modifier.clickAnimation { onActionClick() })
    }
}

@Composable
private fun TeacherSessionCard(session: TeacherSessionUiModel, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg).border(1.17.dp, AppTheme.color.border, RoundedCornerShape(16.dp)).padding(17.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(AppTheme.color.borderFocus.copy(alpha = .1f)), contentAlignment = Alignment.Center) {
            Icon(painter = painterResource(R.drawable.ic_clock), contentDescription = null, tint = AppTheme.color.primary, modifier = Modifier.size(24.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = session.courseName, color = AppTheme.color.text, style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold))
            Row(modifier = Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(R.drawable.ic_clock), contentDescription = null, tint = AppTheme.color.primary, modifier = Modifier.size(12.dp))
                Text(text = session.time, color = AppTheme.color.textSecondary, style = AppTheme.textStyle.label.medium, modifier = Modifier.padding(start = 4.dp))
                Text(text = session.location, color = AppTheme.color.textSecondary, style = AppTheme.textStyle.label.medium, modifier = Modifier.padding(start = 16.dp))
            }
        }
        Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(AppTheme.color.secondary), contentAlignment = Alignment.Center) {
            Icon(painter = painterResource(R.drawable.ic_calendar), contentDescription = null, tint = AppTheme.color.primary, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun TeacherCourseSummaryCard(course: TeacherCourseSummaryUiModel, onManageClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg).border(1.17.dp, AppTheme.color.border, RoundedCornerShape(16.dp)).padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = course.name, color = AppTheme.color.text, style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold))
                Text(text = "${course.totalStudents} طالب  •  ${course.totalAssignments} واجب", color = AppTheme.color.textSecondary, style = AppTheme.textStyle.label.medium, modifier = Modifier.padding(top = 4.dp))
            }
            Box(
                modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(AppTheme.color.primary.copy(alpha = .1f)).clickAnimation { onManageClick() }.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = "إدارة", color = AppTheme.color.primary, style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold))
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CourseMiniStat("متوسط الدرجة", "${course.averageGrade}%", Modifier.weight(1f))
            CourseMiniStat("التقدم", "${(course.averageProgress * 100).toInt()}%", Modifier.weight(1f))
        }
    }
}

@Composable
private fun CourseMiniStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.clip(RoundedCornerShape(12.dp)).background(AppTheme.color.bgHover).padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = AppTheme.color.primary, style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold))
        Text(text = label, color = AppTheme.color.textSecondary, style = AppTheme.textStyle.label.medium, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun HandleEffects(
    effects: Flow<HomeTeacherEffect>,
    onNavigateToCourses: () -> Unit,
    onNavigateToStudents: () -> Unit,
    onNavigateToGrading: () -> Unit,
) {
    LaunchedEffect(Unit) {
        effects.collectLatest { effect ->
            when (effect) {
                HomeTeacherEffect.NavigateToCourses               -> onNavigateToCourses()
                HomeTeacherEffect.NavigateToStudents              -> onNavigateToStudents()
                HomeTeacherEffect.NavigateToGrading               -> onNavigateToGrading()
                HomeTeacherEffect.NavigateToSchedule              -> { /* TODO */ }
                HomeTeacherEffect.NavigateToReports               -> { /* TODO */ }
                HomeTeacherEffect.NavigateToAddCourse             -> { /* TODO */ }
                is HomeTeacherEffect.NavigateToManageCourse       -> { /* TODO */ }
            }
        }
    }
}
