package noor.serry.rawaa.ui.screens.courses_student

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.components.utils.ignoreHorizontalParentPadding
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R

/**
 * Card for an enrolled course.
 *
 * Removed action buttons:
 *   • "محاضرات" (Lectures)  — no /lectures endpoint on the server
 *   • "واجبات"  (Homework)  — no /homework endpoint on the server
 *   • "المواد"  (Materials) — no /materials endpoint on the server
 *
 * Removed metadata rows:
 *   • studentCount    — StudentCourseDto has no enrolled_count field
 *   • nextSessionTime — no endpoint returns the next session time per enrolled course
 *
 * Kept metadata rows (backed by StudentCourseDto):
 *   • creditHours — StudentCourseDto.credit_hours
 *   • semester    — StudentCourseDto.semester
 */
@Composable
fun EnrolledCourseCard(
    item: EnrolledCourseItem,
    modifier: Modifier = Modifier,
    // Removed: onLecturesClick, onHomeworkClick, onMaterialsClick — no server endpoints
) {
    var started by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (started) item.progressPercent / 100f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "progress"
    )
    LaunchedEffect(Unit) { started = true }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg)
            .border(1.17.dp, AppTheme.color.border, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // ── Course icon + name + code ─────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (item.isYellowIcon) Color(0x33FACC15)
                        else AppTheme.color.primary.copy(alpha = .125f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_book),
                    contentDescription = null,
                    tint = if (item.isYellowIcon) Color(0xFFF59E0B) else AppTheme.color.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.courseName,
                    color = AppTheme.color.primaryDark,
                    style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = item.professorName,
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal),
                )
            }
            Text(
                text = item.courseCode,
                color = AppTheme.color.textSecondary,
                style = AppTheme.textStyle.label.medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(AppTheme.color.bgHover, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // ── Progress bar ──────────────────────────────────────────
        Column(modifier = Modifier.padding(top = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "التقدم",
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.label.medium,
                )
                Text(
                    text = "${item.progressPercent}%",
                    color = AppTheme.color.primary,
                    style = AppTheme.textStyle.label.medium.copy(fontWeight = FontWeight.Bold),
                )
            }
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .ignoreHorizontalParentPadding(16.dp)
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(AppTheme.color.bgHover)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(AppTheme.color.primary)
                )
            }
        }

        // ── Metadata row — credit hours + semester ────────────────
        // Both fields come from StudentCourseDto; nextSessionTime and
        // studentCount are absent from StudentCourseDto so they are removed.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // StudentCourseDto.credit_hours
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_book),
                    contentDescription = null,
                    tint = AppTheme.color.textSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${item.creditHours} ساعة",
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal)
                )
            }
            // StudentCourseDto.semester
            if (item.semester.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_calendar),
                        contentDescription = null,
                        tint = AppTheme.color.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = item.semester,
                        color = AppTheme.color.textSecondary,
                        style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal)
                    )
                }
            }
        }
    }
}
