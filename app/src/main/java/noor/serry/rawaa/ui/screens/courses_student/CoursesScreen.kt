package noor.serry.rawaa.ui.screens.courses_student

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.BackStackProvider
import noor.serry.rawaa.ui.navigation.base.AppRoute

@Composable
fun CoursesScreen(
    viewModel: CoursesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    HandleEffects(effects = viewModel.effect)

    CoursesContent(
        state = state,
        interactionListener = viewModel
    )
}

@Composable
private fun CoursesContent(
    state: CoursesStudentUiState,
    interactionListener: CoursesInteractionListener
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.bg),
        contentPadding = PaddingValues(top = 16.dp)
    ) {
        // ── Title ─────────────────────────────────────────────────
        item {
            Text(
                text = "المقررات الدراسية",
                color = AppTheme.color.primaryDark,
                style = AppTheme.textStyle.headline.small,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        stickyHeader {
            // ── Tab Row ───────────────────────────────────────────────
            CoursesTabRow(
                selectedTab = state.selectedTab,
                myCoursesCount = state.myCourses.size,
                availableCount = state.availableCourses.size,
                onTabSelected = interactionListener::onTabSelected,
            )
        }

        item {
            // ── Tab content ───────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppTheme.color.bgHover)
                    .padding( 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (state.selectedTab) {
                    CoursesTab.MY_COURSES -> {
                        state.myCourses.forEach { course ->
                            EnrolledCourseCard(
                                item = course,
                                onOpenCourse = { interactionListener.onOpenCourse(course.courseCode) },
                                onLecturesClick = { interactionListener.onLecturesClick(course.courseCode) },
                                onHomeworkClick = { interactionListener.onHomeworkClick(course.courseCode) },
                                onMaterialsClick = { interactionListener.onMaterialsClick(course.courseCode) },
                            )
                        }
                    }

                    CoursesTab.AVAILABLE -> {
                        state.availableCourses.forEach { course ->
                            AvailableCourseCard(
                                item = course,
                                onEnrollClick = { interactionListener.onEnrollClick(course.courseCode) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CoursesTabRow(
    selectedTab: CoursesTab,
    myCoursesCount: Int,
    availableCount: Int,
    onTabSelected: (CoursesTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = AppTheme.color.border
    Row(
        modifier = modifier.fillMaxWidth()
            .background(AppTheme.color.bg).drawBehind {
            val strokeWidth = 1.17.dp.toPx()
            val y = size.height - strokeWidth / 2
            drawLine(
                color = borderColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = strokeWidth
            )
        }
            .padding( 16.dp),

        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CoursesTab.entries.forEach { tab ->
            val isSelected = selectedTab == tab
            val label = when (tab) {
                CoursesTab.MY_COURSES -> "مقرراتي ($myCoursesCount)"
                CoursesTab.AVAILABLE -> "مقررات متاحة ($availableCount)"
            }
            val backgroundColor by animateColorAsState(
                if (isSelected) AppTheme.color.primary
                else AppTheme.color.bgHover,
                animationSpec = tween (500)
            )

            val textColor by animateColorAsState(
                targetValue = if (isSelected) AppTheme.color.bg else AppTheme.color.textSecondary,
                label = "TabText",
                animationSpec = tween(500)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(backgroundColor,RoundedCornerShape(12.dp))
                    .clickAnimation { onTabSelected(tab) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = textColor,
                    style = AppTheme.textStyle.body.medium,
                )
            }
        }
        }
}

@Composable
private fun HandleEffects(
    effects: Flow<CoursesEffect>
) {
    val navigationBackStack = BackStackProvider.current
    LaunchedEffect(Unit) {
        effects.collectLatest { effect ->
            when (effect) {
//                is CoursesEffect.NavigateToCourseDetails ->
//                    navigationBackStack.add(AppRoute.CourseDetails(effect.courseCode))
//                is CoursesEffect.NavigateToLectures ->
//                    navigationBackStack.add(AppRoute.Lectures(effect.courseCode))
//                is CoursesEffect.NavigateToHomework ->
//                    navigationBackStack.add(AppRoute.Homework)
//                is CoursesEffect.NavigateToMaterials ->
//                    navigationBackStack.add(AppRoute.Materials(effect.courseCode))
//                is CoursesEffect.NavigateToEnroll ->
//                    navigationBackStack.add(AppRoute.Enroll(effect.courseCode))
                else -> {}
            }
        }
    }
}
