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
import noor.serry.rawaa.BackStackProvider
import noor.serry.rawaa.ui.navigation.student.StudentBackStackProvider
import noor.serry.rawaa.ui.navigation.student.StudentRouteKeys
import noor.serry.rawaa.ui.screens.home_student.components.CourseProgressSection
import noor.serry.rawaa.ui.screens.home_student.components.HomeHeaderSection
import noor.serry.rawaa.ui.screens.home_student.components.TodayScheduleSection
import noor.serry.rawaa.ui.screens.home_student.components.UpcomingExamsSection

@Composable
fun HomeStudentScreen(
    viewModel: HomeStudentViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    HandleEffects(
        effects = viewModel.effect
    )

    HomeStudentContent(state = state, interactionListener = viewModel)
}

@Composable
private fun HomeStudentContent(
    state: HomeStudentUiState,
    interactionListener: HomeStudentInteractionListener,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.bgHover)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header — studentName, gpa, activeCoursesCount, upcomingExamsCount
        // All sourced from GET /api/student/dashboard → StudentDashboardDto
        HomeHeaderSection(
            studentName        = state.studentName,
            gpa                = state.gpa,
            upcomingExamsCount = state.upcomingExamsCount,
            activeCoursesCount = state.activeCoursesCount,
            modifier           = Modifier.padding(top = 16.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Today's schedule — from StudentDashboardDto.schedule (List<ScheduleSessionDto>)
        TodayScheduleSection(
            items     = state.scheduleItems,
            onViewAll = interactionListener::onViewAllSchedule,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Upcoming exams — from StudentDashboardDto.upcoming_exams (List<UpcomingExamDto>)
        // Replaces UpcomingHomeworkSection: no homework endpoint exists on the server
        UpcomingExamsSection(
            items = state.upcomingExams,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Course progress — StudentDashboardDto.courses filtered to status == "active"
        CourseProgressSection(
            items     = state.courseProgressItems,
            onViewAll = interactionListener::onViewAllCourses,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Removed: UpcomingHomeworkSection — no homework endpoint exists on the server
        // Removed: QuickActionsSection     — contained only static strings, no server data,
        //          and the actions (Grades, Homework, …) have no backing endpoints or routes
    }
}

@Composable
private fun HandleEffects(
    effects: Flow<HomeStudentEffect>,
) {
    val navigationBackStack = StudentBackStackProvider.current

    LaunchedEffect(Unit) {
        effects.collectLatest { effect ->
            when (effect) {
                HomeStudentEffect.NavigateToAllCourses,
                HomeStudentEffect.NavigateToMyCourses   -> navigationBackStack.add(StudentRouteKeys.Courses)
                HomeStudentEffect.NavigateToAllSchedule,
                HomeStudentEffect.NavigateToSchedule    -> navigationBackStack.add(StudentRouteKeys.Schedule)
                // Removed: NavigateToGrades — no grades screen in StudentRouteKeys
            }
        }
    }
}
