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
        // ── Header ────────────────────────────────────────────────────────────
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
                    // total_students comes from DoctorDashboardDto
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = "${state.totalStudents}",
                        label = "طالب",
                        valueColor = AppTheme.color.text,
                    )
                    // active course count is derivable locally
                    StatCard(
                        modifier = Modifier.weight(1f),
                        value = "${state.activeCourses.size}",
                        label = "مقرر نشط",
                        valueColor = AppTheme.color.text,
                    )
                }
            }
        }

        // ── Tabs ──────────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
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

        // ── Body ──────────────────────────────────────────────────────────────
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppTheme.color.primary)
                }
            }
            state.error != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = state.error ?: "حدث خطأ",
                        color = AppTheme.color.textSecondary,
                        fontSize = 14.sp,
                    )
                }
            }
            displayedCourses.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "لا توجد مقررات",
                        color = AppTheme.color.textSecondary,
                        fontSize = 14.sp,
                    )
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(displayedCourses) { course ->
                        CourseTeacherCard(
                            course = course,
                            onViewDetails = { viewModel.onCourseClicked(course.id) },
                        )
                    }
                }
            }
        }
    }
}

// ── Sub-components ────────────────────────────────────────────────────────────

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

/**
 * Card that renders only fields present in [CourseDto] returned by
 * GET /api/doctor/dashboard → courses[].
 *
 * Removed UI that had no backend backing:
 *   • "قيد التصحيح" stat (no pending-grading count in API)
 *   • Average-grade badge "85%" (not in CourseDto)
 *   • "معلق" / pending badge (not in CourseDto)
 *   • "واجب" badge (no assignments endpoint)
 *   • Quick-link chips: "المحاضرات", "الواجبات" (no matching endpoints)
 *   • Hardcoded semester string "الفصل الأول 2026"
 */
@Composable
fun CourseTeacherCard(course: CourseDto, onViewDetails: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Title row ────────────────────────────────────────────────────
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
                    // semester & academic_year are nullable — only show when present
                    val semesterLabel = buildSemesterLabel(course.semester, course.academicYear)
                    if (semesterLabel != null) {
                        Text(
                            text = semesterLabel,
                            fontSize = 12.sp,
                            color = AppTheme.color.textSecondary,
                        )
                    }
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

            // ── Info badges – only real server fields ─────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // enrolled_count (nullable) — students registered in this course
                InfoBadge(
                    value = "${course.enrolledCount ?: 0}",
                    label = "طالب",
                )
                // credit_hours – always present (default 3)
                InfoBadge(
                    value = "${course.creditHours}",
                    label = "ساعات",
                )
                // max_students – capacity
                InfoBadge(
                    value = "${course.maxStudents}",
                    label = "الحد الأقصى",
                )
                // department_name if returned
                if (course.departmentName != null) {
                    InfoBadge(
                        value = course.departmentName,
                        label = "القسم",
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── "عرض التفاصيل" button — navigates to course detail ───────────
            // Backed by GET /api/courses/{id}/students which the repository exposes.
            OutlinedButton(
                onClick = onViewDetails,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                border = ButtonDefaults.outlinedButtonBorder,
            ) {
                Text(
                    text = "عرض التفاصيل",
                    fontSize = 13.sp,
                    color = AppTheme.color.primaryDark,
                )
            }
        }
    }
}

@Composable
private fun InfoBadge(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(AppTheme.color.bg)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.text)
        Text(label, fontSize = 10.sp, color = AppTheme.color.textSecondary)
    }
}

/** Returns e.g. "الفصل الثاني 2025" or null if both fields are absent. */
private fun buildSemesterLabel(semester: String?, academicYear: Int?): String? {
    if (semester == null && academicYear == null) return null
    return listOfNotNull(semester, academicYear?.toString()).joinToString(" ")
}
