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
        // Header banner
        item {
            HomeTeacherHeader(
                name = state.doctorName,
                pendingTasks = state.pendingGrading,
                totalStudents = state.totalStudents,
                activeCourses = state.totalCourses,
            )
        }

        // Quick actions
        item {
            QuickActionsSection(
                onAddCourse = { backStack.add(TeacherRouteKeys.Courses) },
                onGrading = { viewModel.onStartGradingClicked() },
            )
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

        // Student performance this week
        item { StudentPerformanceCard() }
    }
}

@Composable
private fun HomeTeacherHeader(
    name: String,
    pendingTasks: Int,
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                HeaderStatItem(value = "$pendingTasks", label = "مهمة معلقة", color = AppTheme.color.secondary)
                HeaderStatItem(value = "$totalStudents", label = "طالب", color = Color.White)
                HeaderStatItem(value = "$activeCourses", label = "مقرر نشط", color = Color.White)
            }
        }
    }
}

@Composable
private fun HeaderStatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
    }
}

@Composable
private fun QuickActionsSection(
    onAddCourse: () -> Unit,
    onGrading: () -> Unit,
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
            QuickActionCard(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.ic_book,
                label = "إضافة مقرر",
                subLabel = "مقرر جديد",
                isPrimary = true,
                onClick = onAddCourse,
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.badge,
                label = "التصحيح",
                subLabel = "75 مهمة",
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
        modifier = modifier
            .clickable(onClick = onClick),
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
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
                Column {
                    Text(
                        text = session.courseName ?: "",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = AppTheme.color.text,
                    )
                    Text(
                        text = "${session.startTime} ص  •  ${session.roomName ?: ""}  •  ${session.enrolled ?: 0} طالب",
                        fontSize = 12.sp,
                        color = AppTheme.color.textSecondary,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppTheme.color.secondary)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text("ابدأ", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

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
                            Text(
                                text = "${course.enrolledCount ?: 0} طالب",
                                fontSize = 12.sp,
                                color = AppTheme.color.textSecondary,
                            )
                            Text("•", fontSize = 12.sp, color = AppTheme.color.textSecondary)
                            Text(
                                text = "${course.code}",
                                fontSize = 12.sp,
                                color = AppTheme.color.textSecondary,
                            )
                        }
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

@Composable
private fun StudentPerformanceCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.color.secondary),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_home),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "أداء الطلاب",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                )
                Spacer(Modifier.weight(1f))
                Text("هذا الأسبوع", color = Color.White.copy(0.75f), fontSize = 12.sp)
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                PerformanceStat(value = "85%", label = "معدل التسليم")
                PerformanceStat(value = "92%", label = "معدل الحضور")
            }
        }
    }
}

@Composable
private fun PerformanceStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Color.White.copy(0.8f), fontSize = 12.sp)
    }
}
