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
                is HomeTeacherEffect.ShowError -> { /* show snackbar if needed */ }
            }
        }
    }

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AppTheme.color.primary)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.bg),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        // ── Header banner ──────────────────────────────────────────────────
        // Shows: doctorName (GET /api/auth/me → UserDto.name)
        //        totalStudents (DoctorDashboardDto.totalStudents)
        //        totalCourses  (DoctorDashboardDto.totalCourses)
        //
        // "مهمة معلقة" stat REMOVED — DoctorDashboardDto has no pending-tasks count.
        item {
            HomeTeacherHeader(
                name = state.doctorName,
                totalStudents = state.totalStudents,
                activeCourses = state.totalCourses,
            )
        }

        // ── Quick actions ──────────────────────────────────────────────────
        // Only two actions are kept:
        //   • "التصحيح" → navigates to grading/assessment screen (endpoint exists)
        //   • "إدارة المقررات" → navigates to courses list (GET /api/courses)
        //
        // Removed: "إضافة مقرر" (admin-only endpoint), "واجب جديد" (no endpoint),
        //          "التقارير" (admin-only endpoint).
        item {
            QuickActionsSection(
                onGrading = { viewModel.onStartGradingClicked() },
                onViewCourses = { viewModel.onViewAllCoursesClicked() },
            )
        }

        // ── Today's lectures ───────────────────────────────────────────────
        // Source: DoctorDashboardDto.schedule (List<ScheduleSessionDto>)
        // Displayed fields: courseName, startTime, endTime, roomName, enrolled
        //
        // The "ابدأ" button from the original design is REMOVED — there is no
        // "start lecture" endpoint in the backend. Clicking it would be a no-op.
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

        // ── Upcoming exams ─────────────────────────────────────────────────
        // Source: DoctorDashboardDto.upcomingExams (List<UpcomingExamDto>)
        // Displayed fields: title, type, courseName, startAt
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

        // ── My courses ─────────────────────────────────────────────────────
        // Source: DoctorDashboardDto.courses (List<CourseDto>)
        // Displayed fields: name, code, enrolledCount, maxStudents, isActive
        //
        // REMOVED from card:
        //   • "85% المعدل" — no average/grade field in CourseDto
        //   • "12 واجب"    — no assignment count in CourseDto or DoctorDashboardDto
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

// ─────────────────────────────────────────────────────────────────────────────
// Header
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HomeTeacherHeader(
    name: String,
    totalStudents: Int,
    activeCourses: Int,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.color.primaryDark)
            .padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppTheme.color.secondary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_home),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "مرحباً، $name!",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "جاهز لبدء يوم تعليمي منمر",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 13.sp,
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Only two real stats are available from DoctorDashboardDto:
            //   totalStudents and totalCourses (= activeCourses).
            // "مهمة معلقة" stat removed — no backend field.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                HeaderStatItem(value = "$totalStudents", label = "طالب")
                HeaderStatItem(value = "$activeCourses", label = "مقرر نشط")
            }
        }
    }
}

@Composable
private fun HeaderStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Quick actions
// Only actions that have a matching backend endpoint or navigation destination.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun QuickActionsSection(
    onGrading: () -> Unit,
    onViewCourses: () -> Unit,
) {
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
            // "إدارة المقررات" — navigates to Courses (GET /api/courses available)
            QuickActionCard(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.ic_book,
                label = "إدارة المقررات",
                subLabel = "عرض المقررات",
                isPrimary = true,
                onClick = onViewCourses,
            )
            // "التصحيح" — navigates to Assessment/Grading screen
            QuickActionCard(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.badge,
                label = "التصحيح",
                subLabel = "مراجعة الإجابات",
                isPrimary = false,
                onClick = onGrading,
            )
        }
    }
}

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
            containerColor = if (isPrimary) AppTheme.color.primaryDark else Color.White,
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
                        else AppTheme.color.primary.copy(alpha = 0.1f)
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
// Section header
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
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.text)
        Text(
            text = "عرض الكل",
            fontSize = 13.sp,
            color = AppTheme.color.primary,
            modifier = Modifier.clickable(onClick = onViewAll),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Today session card
// Fields sourced from ScheduleSessionDto:
//   courseName, startTime, endTime, roomName, enrolled
//
// "ابدأ" action button REMOVED — no backend endpoint to start a lecture.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TodaySessionCard(session: ScheduleSessionDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
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
                    .background(AppTheme.color.primaryDark),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_book),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.courseName ?: "",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = AppTheme.color.text,
                )
                // Time range — both startTime and endTime are guaranteed non-null in ScheduleSessionDto
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
// Upcoming exam card
// Fields sourced from UpcomingExamDto: title, type, courseName, startAt
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UpcomingExamCard(exam: noor.serry.rawaa.data.dto.UpcomingExamDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
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
                    modifier = Modifier.size(20.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exam.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
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
            // Exam type badge
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
// Course card
// Fields sourced from CourseDto: name, code, enrolledCount, maxStudents, isActive
//
// REMOVED from design:
//   • "85% المعدل" — no average/grade field in CourseDto or DoctorDashboardDto
//   • "12 واجب"    — no assignment count field in any available DTO
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DashboardCourseCard(course: CourseDto, onManage: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AppTheme.color.primaryDark.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_book),
                            contentDescription = null,
                            tint = AppTheme.color.primaryDark,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Column {
                        Text(
                            text = course.name,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = AppTheme.color.text,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // enrolledCount: CourseDto field (nullable, defaults to null)
                            Text(
                                text = "${course.enrolledCount ?: 0} طالب",
                                fontSize = 12.sp,
                                color = AppTheme.color.textSecondary,
                            )
                            Text("•", fontSize = 12.sp, color = AppTheme.color.textSecondary)
                            Text(
                                text = course.code,
                                fontSize = 12.sp,
                                color = AppTheme.color.textSecondary,
                            )
                            // maxStudents is always present in CourseDto (defaults to 50)
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
                }
                // Active/inactive badge — sourced from CourseDto.isActive (Int 0/1)
                if (course.isActive == 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFF5F5F5))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            text = "غير نشط",
                            fontSize = 11.sp,
                            color = AppTheme.color.textSecondary,
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onManage,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppTheme.color.primaryDark),
            ) {
                Text("إدارة المقرر", color = Color.White, fontSize = 13.sp)
                Spacer(Modifier.width(4.dp))
                Text("›", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}