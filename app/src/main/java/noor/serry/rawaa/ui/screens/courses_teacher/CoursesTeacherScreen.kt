package noor.serry.rawaa.ui.screens.courses_teacher

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
fun CoursesTeacherScreen(
    onBack: () -> Unit = {},
    onNavigateToStudents: () -> Unit = {},
    onNavigateToGrading: () -> Unit = {},
    viewModel: CoursesTeacherViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    HandleEffects(effects = viewModel.effect)

    CoursesTeacherContent(
        state = state,
        interactionListener = viewModel,
        onBack = onBack,
        onNavigateToStudents = onNavigateToStudents,
        onNavigateToGrading = onNavigateToGrading,
    )
}

@Composable
private fun CoursesTeacherContent(
    state: CoursesTeacherUiState,
    interactionListener: CoursesTeacherInteractionListener,
    onBack: () -> Unit,
    onNavigateToStudents: () -> Unit,
    onNavigateToGrading: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().background(AppTheme.color.bgHover)) {
        CoursesTeacherHeader(
            activeCourseCount = state.activeCourseCount,
            totalStudents = state.totalStudents,
            totalPendingGrades = state.totalPendingGrades,
            onBack = onBack,
            onAddCourseClick = interactionListener::onAddCourseClick,
        )

        CoursesTeacherTabRow(
            selectedTab = state.selectedTab,
            activeCount = state.activeCourses.size,
            archivedCount = state.archivedCourses.size,
            onTabSelected = interactionListener::onTabSelected,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "جاري التحميل...", color = AppTheme.color.textSecondary, style = AppTheme.textStyle.body.medium)
            }
            state.errorMessage != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
            state.displayedCourses.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "لا توجد مقررات مسندة إليك", color = AppTheme.color.textSecondary, style = AppTheme.textStyle.body.medium)
            }
            else -> LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.displayedCourses, key = { it.courseId }) { course ->
                    TeacherCourseCard(
                        course = course,
                        onManageClick = { interactionListener.onManageCourseClick(course.courseId) },
                        onStudentsClick = onNavigateToStudents,
                        onGradingClick = onNavigateToGrading,
                    )
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun CoursesTeacherHeader(
    activeCourseCount: Int,
    totalStudents: Int,
    totalPendingGrades: Int,
    onBack: () -> Unit,
    onAddCourseClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().height(140.dp)
            .background(brush = verticalGradient(listOf(AppTheme.color.primary, AppTheme.color.primaryLight)))
            .padding(horizontal = 24.dp).padding(top = 48.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                        .background(AppTheme.color.bg.copy(alpha = .15f)).clickAnimation { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painter = painterResource(R.drawable.ic_arrow_forward), contentDescription = "رجوع", tint = AppTheme.color.bg, modifier = Modifier.size(18.dp))
                }
                Text(text = "مقرراتي", color = AppTheme.color.bg, style = AppTheme.textStyle.headline.small)
            }
            Box(
                modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(AppTheme.color.secondary)
                    .clickAnimation { onAddCourseClick() }.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = "إضافة مقرر", color = AppTheme.color.primaryDark, style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
private fun CoursesTeacherTabRow(
    selectedTab: CoursesTeacherTab,
    activeCount: Int,
    archivedCount: Int,
    onTabSelected: (CoursesTeacherTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().background(AppTheme.color.bg, RoundedCornerShape(16.dp)).padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CoursesTeacherTab.entries.forEach { tab ->
            val isSelected = selectedTab == tab
            val bgColor by animateColorAsState(if (isSelected) AppTheme.color.primary else AppTheme.color.bg, tween(700))
            val textColor by animateColorAsState(if (isSelected) AppTheme.color.bg else AppTheme.color.textSecondary, tween(700))
            val label = when (tab) {
                CoursesTeacherTab.ACTIVE   -> "نشطة ($activeCount)"
                CoursesTeacherTab.ARCHIVED -> "مؤرشفة ($archivedCount)"
            }
            Box(
                modifier = Modifier.weight(1f).background(bgColor, RoundedCornerShape(12.dp))
                    .clickAnimation { onTabSelected(tab) }.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = label, color = textColor, style = AppTheme.textStyle.body.small)
            }
        }
    }
}

@Composable
private fun TeacherCourseCard(
    course: TeacherCourseUiModel,
    onManageClick: () -> Unit,
    onStudentsClick: () -> Unit,
    onGradingClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg).border(1.17.dp, AppTheme.color.border, RoundedCornerShape(16.dp)).padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = course.courseName, color = AppTheme.color.text, style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold))
                Text(text = course.courseCode, color = AppTheme.color.textSecondary, style = AppTheme.textStyle.label.medium, modifier = Modifier.padding(top = 2.dp))
                Text(text = "${course.totalStudents} طالب  •  ${course.totalAssignments} واجب", color = AppTheme.color.textSecondary, style = AppTheme.textStyle.label.medium, modifier = Modifier.padding(top = 4.dp))
            }
            if (course.pendingGrades > 0) {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(AppTheme.color.error.copy(alpha = .1f)).padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "${course.pendingGrades} معلق", color = AppTheme.color.error, style = AppTheme.textStyle.label.medium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        // Progress bar
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "تقدم المقرر", color = AppTheme.color.textSecondary, style = AppTheme.textStyle.label.medium)
                Text(text = "${(course.averageProgress * 100).toInt()}%", color = AppTheme.color.primary, style = AppTheme.textStyle.label.medium.copy(fontWeight = FontWeight.Bold))
            }
            Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(AppTheme.color.borderFocus.copy(alpha = .2f))) {
                Box(modifier = Modifier.fillMaxWidth(course.averageProgress).height(6.dp).background(AppTheme.color.primary, RoundedCornerShape(3.dp)))
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                    .border(1.17.dp, AppTheme.color.border, RoundedCornerShape(12.dp))
                    .clickAnimation { onStudentsClick() }.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "الطلاب", color = AppTheme.color.primaryDark, style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold))
            }
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                    .background(AppTheme.color.primary)
                    .clickAnimation { onGradingClick() }.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "الدرجات", color = AppTheme.color.bg, style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
private fun HandleEffects(effects: Flow<CoursesTeacherEffect>) {
    LaunchedEffect(Unit) {
        effects.collectLatest { effect ->
            when (effect) {
                is CoursesTeacherEffect.NavigateToManageCourse -> { /* TODO */ }
                CoursesTeacherEffect.NavigateToAddCourse       -> { /* TODO */ }
            }
        }
    }
}
