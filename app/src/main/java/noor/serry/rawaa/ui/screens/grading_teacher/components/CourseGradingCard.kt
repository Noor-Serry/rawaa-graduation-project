package noor.serry.rawaa.ui.screens.grading_teacher.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.screens.grading_teacher.CourseGradingUiModel

/**
 * Card for a single course on the grading screen.
 *
 * Displays only fields present in [CourseDto] (via [CourseGradingUiModel]):
 *   • name, code             — course identity
 *   • departmentName         — nullable, hidden when null
 *   • semester               — nullable, hidden when null
 *   • creditHours            — CourseDto.creditHours
 *   • enrolledCount          — CourseDto.enrolledCount ?: 0
 *   • maxStudents            — CourseDto.maxStudents
 *   • enrolmentProgress      — client-computed enrolledCount / maxStudents
 *
 * No action buttons — there is no backend endpoint for per-course
 * submission grading from this list screen.
 */
@Composable
fun CourseGradingCard(
    course: CourseGradingUiModel,
    modifier: Modifier = Modifier,
) {
    val colors    = AppTheme.color
    val textStyle = AppTheme.textStyle

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.bg)
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {

        // ── Header: icon + name + code ────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.Top,
        ) {
            // Icon badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.warningBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter            = painterResource(R.drawable.ic_assignment),
                    contentDescription = null,
                    tint               = colors.warning,
                    modifier           = Modifier.size(22.dp),
                )
            }

            // Name + code — RTL: aligned to end
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text  = course.name,
                    style = textStyle.body.large,
                    color = colors.text,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text  = course.code,
                    style = textStyle.label.medium,
                    color = colors.textSecondary,
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // ── Meta row: department · semester · credit hours ────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Text(
                text  = "${course.creditHours} ساعة",
                style = textStyle.label.medium,
                color = colors.textSecondary,
            )

            // Semester — only when server returns it
            if (course.semester != null) {
                Spacer(Modifier.width(12.dp))
                Icon(
                    painter            = painterResource(R.drawable.ic_clock),
                    contentDescription = null,
                    tint               = colors.textSecondary,
                    modifier           = Modifier.size(14.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text  = course.semester,
                    style = textStyle.label.medium,
                    color = colors.textSecondary,
                )
            }

            // Department — only when server returns it
            if (course.departmentName != null) {
                Spacer(Modifier.width(12.dp))
                Text(
                    text  = course.departmentName,
                    style = textStyle.label.medium,
                    color = colors.textSecondary,
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = colors.border, thickness = 1.dp)
        Spacer(Modifier.height(12.dp))

        // ── Enrolment progress ────────────────────────────────────────
        EnrolmentProgressBar(
            enrolledCount = course.enrolledCount,
            maxStudents   = course.maxStudents,
            progress      = course.enrolmentProgress,
        )
    }
}
