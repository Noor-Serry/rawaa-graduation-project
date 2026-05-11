package noor.serry.rawaa.ui.screens.home_teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.data.dto.CourseDto
import noor.serry.rawaa.data.dto.ScheduleSessionDto
import noor.serry.rawaa.data.dto.UpcomingExamDto
import noor.serry.rawaa.ui.navigation.teatcher.TeacherBackStackProvider
import noor.serry.rawaa.ui.navigation.teatcher.TeacherRouteKeys
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeTeacherScreen(
    viewModel: HomeTeacherViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val backStack = TeacherBackStackProvider.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeTeacherEffect.NavigateToCourses -> backStack.add(TeacherRouteKeys.Courses)
                is HomeTeacherEffect.NavigateToGrading -> backStack.add(TeacherRouteKeys.Assessment)
                is HomeTeacherEffect.NavigateToCourseDetail -> backStack.add(TeacherRouteKeys.Courses)
                is HomeTeacherEffect.ShowError -> {}
            }
        }
    }

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AppTheme.color.primary)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.bg),
    ) {
        HomeTeacherHeaderSection(
            doctorName = state.doctorName,
            totalStudents = state.totalStudents,
            totalCourses = state.totalCourses,
            upcomingExamsCount = state.upcomingExams.size,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        )

        // ── Scrollable body ────────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            // Quick actions
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                ) {
                    Text(
                        text = "إجراءات سريعة",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.color.text,
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        QuickActionCard(
                            modifier = Modifier.weight(1f),
                            iconRes = R.drawable.ic_book,
                            label = "إدارة المقررات",
                            subLabel = "عرض المقررات",
                            isPrimary = true,
                            onClick = { viewModel.onViewAllCoursesClicked() },
                        )
                        QuickActionCard(
                            modifier = Modifier.weight(1f),
                            iconRes = R.drawable.badge,
                            label = "التصحيح",
                            subLabel = "مراجعة الإجابات",
                            isPrimary = false,
                            onClick = { viewModel.onStartGradingClicked() },
                        )
                    }
                }
            }

            // Today's lectures
            if (state.todaySchedule.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "محاضرات اليوم",
                        onViewAll = { backStack.add(TeacherRouteKeys.Courses) },
                    )
                }
                items(state.todaySchedule.take(3)) { session ->
                    TodaySessionCard(session = session)
                }
            }

            // Upcoming exams
            if (state.upcomingExams.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "الاختبارات القادمة",
                        onViewAll = { backStack.add(TeacherRouteKeys.Assessment) },
                    )
                }
                items(state.upcomingExams.take(3)) { exam ->
                    UpcomingExamCard(exam = exam)
                }
            }

            // My courses
            if (state.courses.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "مقرراتي",
                        onViewAll = { viewModel.onViewAllCoursesClicked() },
                    )
                }
                items(state.courses) { course ->
                    DashboardCourseCard(
                        course = course,
                        onManage = { viewModel.onManageCourseClicked(course.id) },
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// StatCard — identical to CoursesTeacherScreen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun StatCard(
    modifier: Modifier,
    value: String,
    label: String,
    valueColor: Color,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = valueColor)
            Text(label, fontSize = 11.sp, color = AppTheme.color.textSecondary)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// QuickActionCard
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun QuickActionCard(
    modifier: Modifier,
    iconRes: Int,
    label: String,
    subLabel: String,
    isPrimary: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPrimary) AppTheme.color.primary else Color.White,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isPrimary) Color.White.copy(alpha = 0.15f)
                        else AppTheme.color.primary.copy(alpha = 0.1f),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = if (isPrimary) Color.White else AppTheme.color.primary,
                    modifier = Modifier.size(20.dp),
                )
            }
            Column {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isPrimary) Color.White else AppTheme.color.text,
                )
                Text(
                    text = subLabel,
                    fontSize = 11.sp,
                    color = if (isPrimary) Color.White.copy(0.75f) else AppTheme.color.textSecondary,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SectionHeader — identical pattern to CoursesTeacherScreen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String, onViewAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AppTheme.color.text,
        )
        Text(
            text = "عرض الكل",
            fontSize = 13.sp,
            color = AppTheme.color.primary,
            modifier = Modifier.clickable(onClick = onViewAll),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TodaySessionCard — same card shape / elevation / padding as CourseTeacherCard
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TodaySessionCard(session: ScheduleSessionDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppTheme.color.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_book),
                    contentDescription = null,
                    tint = AppTheme.color.primary,
                    modifier = Modifier.size(22.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.courseName ?: "",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = AppTheme.color.text,
                )
                val timeRange = "${session.startTime} – ${session.endTime}"
                val room = session.roomName?.let { " • $it" } ?: ""
                val students = session.enrolled?.let { " • $it طالب" } ?: ""
                Text(
                    text = "$timeRange$room$students",
                    fontSize = 12.sp,
                    color = AppTheme.color.textSecondary,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// UpcomingExamCard
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UpcomingExamCard(exam: UpcomingExamDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppTheme.color.secondary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.badge),
                    contentDescription = null,
                    tint = AppTheme.color.secondary,
                    modifier = Modifier.size(22.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exam.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = AppTheme.color.text,
                )
                val course = exam.courseName?.let { "$it • " } ?: ""
                val date = exam.startAt ?: ""
                Text(
                    text = "$course$date",
                    fontSize = 12.sp,
                    color = AppTheme.color.textSecondary,
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppTheme.color.primary.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = exam.type,
                    color = AppTheme.color.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DashboardCourseCard — mirrors CourseTeacherCard from CoursesTeacherScreen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DashboardCourseCard(course: CourseDto, onManage: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = course.code,
                        fontSize = 12.sp,
                        color = AppTheme.color.textSecondary,
                    )
                    Text(
                        text = course.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = AppTheme.color.text,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "${course.enrolledCount ?: 0} طالب",
                            fontSize = 12.sp,
                            color = AppTheme.color.textSecondary,
                        )
                        if (course.maxStudents > 0) {
                            Text("•", fontSize = 12.sp, color = AppTheme.color.textSecondary)
                            Text(
                                text = "الحد ${course.maxStudents}",
                                fontSize = 12.sp,
                                color = AppTheme.color.textSecondary,
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppTheme.color.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_book),
                        contentDescription = null,
                        tint = AppTheme.color.primary,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            if (course.isActive == 0) {
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AppTheme.color.bg)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = "غير نشط",
                        fontSize = 11.sp,
                        color = AppTheme.color.textSecondary,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onManage,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppTheme.color.primary),
            ) {
                Text("إدارة المقرر", color = Color.White, fontSize = 13.sp)
                Spacer(Modifier.width(4.dp))
                Text("›", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}