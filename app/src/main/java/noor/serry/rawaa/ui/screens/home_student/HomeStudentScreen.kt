package noor.serry.rawaa.ui.screens.home_student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import noor.serry.rawaa.ui.screens.home_student.components.CourseProgressSection
import noor.serry.rawaa.ui.screens.home_student.components.HomeHeaderSection
import noor.serry.rawaa.ui.screens.home_student.components.QuickActionsSection
import noor.serry.rawaa.ui.screens.home_student.components.TodayScheduleSection
import noor.serry.rawaa.ui.screens.home_student.components.UpcomingHomeworkSection

@Composable
fun HomeStudentScreen(
    onNavigateToCourses: () -> Unit = {},
    onNavigateToSchedule: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    viewModel: HomeStudentViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    HandleEffects(
        effects = viewModel.effect,
        onNavigateToCourses = onNavigateToCourses,
        onNavigateToSchedule = onNavigateToSchedule,
    )

    HomeStudentContent(state = state, interactionListener = viewModel)
}

@Composable
private fun HomeStudentContent(
    state: HomeStudentUiState,
    interactionListener: HomeStudentInteractionListener
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.bgHover)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        HomeHeaderSection(
            studentName = state.studentName,
            gpa = state.gpa,
            homeworkCount = state.homeworkCount,
            activeCoursesCount = state.activeCoursesCount,
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        TodayScheduleSection(
            items = state.scheduleItems,
            onViewAll = interactionListener::onViewAllSchedule
        )

        Spacer(modifier = Modifier.height(24.dp))

        UpcomingHomeworkSection(
            items = state.homeworkItems,
            onViewAll = interactionListener::onViewAllHomework
        )

        Spacer(modifier = Modifier.height(24.dp))

        CourseProgressSection(
            items = state.courseProgressItems,
            onViewAll = interactionListener::onViewAllCourses
        )

        Spacer(modifier = Modifier.height(24.dp))

        QuickActionsSection(actions = state.quickActions, Modifier.padding(bottom = 16.dp))
    }
}

@Composable
private fun HandleEffects(
    effects: Flow<HomeStudentEffect>,
    onNavigateToCourses: () -> Unit,
    onNavigateToSchedule: () -> Unit,
) {
    LaunchedEffect(Unit) {
        effects.collectLatest { effect ->
            when (effect) {
                HomeStudentEffect.NavigateToAllCourses,
                HomeStudentEffect.NavigateToMyCourses   -> onNavigateToCourses()
                HomeStudentEffect.NavigateToAllSchedule,
                HomeStudentEffect.NavigateToSchedule    -> onNavigateToSchedule()
                HomeStudentEffect.NavigateToAllHomework,
                HomeStudentEffect.NavigateToHomework    -> { /* TODO */ }
                HomeStudentEffect.NavigateToGrades      -> { /* TODO */ }
            }
        }
    }
}
