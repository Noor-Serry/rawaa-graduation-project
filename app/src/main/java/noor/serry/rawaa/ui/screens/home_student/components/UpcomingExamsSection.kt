package noor.serry.rawaa.ui.screens.home_student.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.screens.home_student.HomeStudentUiState

/**
 * Displays upcoming exams from [StudentDashboardDto.upcomingExams].
 * Replaces UpcomingHomeworkSection which was removed because the server
 * has no homework endpoint — homeworkItems were always emptyList().
 */
@Composable
fun UpcomingExamsSection(
    items: List<HomeStudentUiState.ExamItem>,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) return

    Column(modifier = modifier) {
        SectionHeader(
            title = "الاختبارات القادمة",
            onActionClick = {},
        )
        Column(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items.forEach { exam ->
                UpcomingExamCard(item = exam)
            }
        }
    }
}

@Composable
private fun UpcomingExamCard(
    item: HomeStudentUiState.ExamItem,
    modifier: Modifier = Modifier,
) {
    val (badgeBg, badgeText) = when (item.type) {
        "midterm"    -> Color(0xFFFEE2E2) to Color(0xFF991B1B)
        "final"      -> Color(0xFFDBEAFE) to Color(0xFF1E40AF)
        "quiz"       -> Color(0xFFFEF3C7) to Color(0xFF92400E)
        else         -> Color(0xFFF0FDF4) to Color(0xFF166534)
    }
    val typeLabel = when (item.type) {
        "midterm"    -> "منتصف الفصل"
        "final"      -> "نهائي"
        "quiz"       -> "اختبار قصير"
        "assignment" -> "واجب"
        else         -> item.type
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg)
            .border(1.17.dp, AppTheme.color.border, RoundedCornerShape(16.dp))
            .padding(17.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(badgeBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_document),
                contentDescription = null,
                tint = badgeText,
                modifier = Modifier.size(20.dp),
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            // ExamDto.title
            Text(
                text = item.title,
                color = AppTheme.color.text,
                style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold),
            )
            // ExamDto.course_name + code
            Text(
                text = "${item.courseName} · ${item.courseCode}",
                color = AppTheme.color.textSecondary,
                style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
                modifier = Modifier.padding(top = 2.dp),
            )
            // ExamDto.start_at
            if (item.startAt.isNotBlank()) {
                Text(
                    text = item.startAt,
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.label.medium,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            // ExamDto.type badge
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeBg)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = typeLabel,
                    color = badgeText,
                    style = AppTheme.textStyle.label.small.copy(fontWeight = FontWeight.Medium),
                )
            }
        }

        // ExamDto.total_marks
        item.totalMarks?.let { marks ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = marks.toInt().toString(),
                    color = AppTheme.color.primaryDark,
                    style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = "درجة",
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.label.small,
                )
            }
        }
    }
}
