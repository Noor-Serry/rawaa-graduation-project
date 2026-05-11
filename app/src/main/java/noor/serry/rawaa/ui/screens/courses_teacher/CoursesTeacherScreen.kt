package noor.serry.rawaa.ui.screens.courses_teacher

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
import org.koin.androidx.compose.koinViewModel

@Composable
fun CoursesTeacherScreen(
    viewModel: CoursesTeacherViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    val displayedCourses = if (state.selectedTab == CourseTab.ACTIVE)
        state.activeCourses else state.archivedCourses

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.bg),
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppTheme.color.primaryDark)
                .padding(horizontal = 20.dp, vertical = 24.dp),
        ) {
            Column {
                Text(
                    text = "مقرراتي",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "إدارة وتتبع المقررات الدراسية",
                    color = Color.White.copy(0.75f),
                    fontSize = 13.sp,
                )
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = "127",
                        label = "قيد التصحيح",
                        valueColor = AppTheme.color.secondary,
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = "${state.totalStudents}",
                        label = "طالب",
                        valueColor = AppTheme.color.text,
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = "${state.activeCourses.size}",
                        label = "مقرر نشط",
                        valueColor = AppTheme.color.text,
                    )
                }
            }
        }

        // Add course button
        Button(
            onClick = { /* navigate to add course */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppTheme.color.primaryDark),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_home),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text("إضافة مقرر جديد", color = Color.White, fontSize = 14.sp)
        }

        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CourseTabButton(
                label = "المقررات النشطة (${state.activeCourses.size})",
                isSelected = state.selectedTab == CourseTab.ACTIVE,
                onClick = { viewModel.selectTab(CourseTab.ACTIVE) },
                modifier = Modifier.weight(1f),
            )
            CourseTabButton(
                label = "الأرشيف (${state.archivedCourses.size})",
                isSelected = state.selectedTab == CourseTab.ARCHIVED,
                onClick = { viewModel.selectTab(CourseTab.ARCHIVED) },
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(8.dp))

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppTheme.color.primary)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(displayedCourses) { course ->
                    CourseTeacherCard(
                        course = course,
                        onManage = { viewModel.onCourseClicked(course.id) },
                    )
                }
            }
        }
    }
}

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

@Composable
private fun CourseTabButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) AppTheme.color.primaryDark else Color.White)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else AppTheme.color.textSecondary,
        )
    }
}

@Composable
fun CourseTeacherCard(course: CourseDto, onManage: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    Text(
                        text = "الفصل الأول 2026",
                        fontSize = 12.sp,
                        color = AppTheme.color.textSecondary,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppTheme.color.primaryDark.copy(0.1f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_book),
                        contentDescription = null,
                        tint = AppTheme.color.primaryDark,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Average grade badge
                StatBadge(
                    value = "85%",
                    label = "معدل",
                    bgColor = Color(0xFFE8F5E9),
                    textColor = Color(0xFF2E7D32),
                )
                // Pending badge
                StatBadge(
                    value = "45",
                    label = "معلق",
                    bgColor = Color(0xFFFFF8E1),
                    textColor = AppTheme.color.secondary,
                )
                // Assignments
                StatBadge(
                    value = "${course.enrolledCount ?: 0}",
                    label = "واجب",
                    bgColor = AppTheme.color.bg,
                    textColor = AppTheme.color.text,
                )
                // Students
                StatBadge(
                    value = "${course.enrolledCount ?: 0}",
                    label = "طالب",
                    bgColor = AppTheme.color.bg,
                    textColor = AppTheme.color.text,
                )
            }

            Spacer(Modifier.height(12.dp))

            // Quick links row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                QuickLinkChip(iconRes = R.drawable.ic_book, label = "المحاضرات", modifier = Modifier.weight(1f))
                QuickLinkChip(iconRes = R.drawable.ic_book, label = "الواجبات", modifier = Modifier.weight(1f))
                QuickLinkChip(iconRes = R.drawable.ic_person, label = "الطلاب", modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(10.dp))

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
private fun StatBadge(value: String, label: String, bgColor: Color, textColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
        Text(label, fontSize = 10.sp, color = textColor.copy(0.7f))
    }
}

@Composable
private fun QuickLinkChip(iconRes: Int, label: String, modifier: Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(AppTheme.color.bg)
            .padding(vertical = 6.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = AppTheme.color.textSecondary,
            modifier = Modifier.size(14.dp),
        )
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 11.sp, color = AppTheme.color.textSecondary)
    }
}
