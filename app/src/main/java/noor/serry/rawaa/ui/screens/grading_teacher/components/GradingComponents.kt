package noor.serry.rawaa.ui.screens.grading_teacher.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import noor.serry.designsystem.design.AppTheme

/**
 * Summary card.
 *
 * Used for:
 *   • Total students  — DoctorDashboardDto.totalStudents
 *   • Total courses   — DoctorDashboardDto.totalCourses
 */
@Composable
fun GradingSummaryCard(
    count: Int,
    label: String,
    subLabel: String,
    iconResId: Int,
    iconTint: Color,
    iconBg: Color,
    countColor: Color,
    modifier: Modifier = Modifier,
) {
    val colors    = AppTheme.color
    val textStyle = AppTheme.textStyle

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.bg)
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier            = Modifier.fillMaxWidth(),
        ) {
            // Icon + label row
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text  = label,
                    style = textStyle.label.medium,
                    color = colors.textSecondary,
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter            = painterResource(iconResId),
                        contentDescription = null,
                        tint               = iconTint,
                        modifier           = Modifier.size(18.dp),
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text  = count.toString(),
                style = textStyle.headline.small,
                color = countColor,
            )

            Text(
                text  = subLabel,
                style = textStyle.label.medium,
                color = colors.textSecondary,
            )
        }
    }
}

/**
 * Enrolment progress bar.
 *
 * @param enrolledCount  CourseGradingUiModel.enrolledCount  (CourseDto.enrolledCount)
 * @param maxStudents    CourseGradingUiModel.maxStudents     (CourseDto.maxStudents)
 * @param progress       CourseGradingUiModel.enrolmentProgress (client-computed 0..1)
 */
@Composable
fun EnrolmentProgressBar(
    enrolledCount: Int,
    maxStudents: Int,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val colors    = AppTheme.color
    val textStyle = AppTheme.textStyle
    val pct       = (progress * 100).toInt()

    Column(modifier = modifier) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Text(
                text  = "$pct%",
                style = textStyle.label.large,
                color = colors.warning,
            )
            Text(
                text      = "$enrolledCount من $maxStudents طالب مسجّل",
                style     = textStyle.label.medium,
                color     = colors.textSecondary,
                textAlign = TextAlign.End,
            )
        }

        Spacer(Modifier.height(6.dp))

        // Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(colors.bgDisabled)
        ) {
            // Fill
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(colors.warning)
            )
        }
    }
}
