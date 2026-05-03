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

@Composable
fun EnrolledCourseCard(
    item: EnrolledCourseItem,
    onOpenCourse: () -> Unit,
    onLecturesClick: () -> Unit,
    onHomeworkClick: () -> Unit,
    onMaterialsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var started by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (started) item.progressPercent / 100f else 0f,
        animationSpec = tween(durationMillis = 800)
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
        // Course code + name + icon row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppTheme.color.primary.copy(alpha = .125f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_book),
                    contentDescription = null,
                    tint = AppTheme.color.primary,
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

        // Progress label row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "التقدم",
                color = AppTheme.color.textSecondary,
                style = AppTheme.textStyle.label.large.copy(fontWeight = FontWeight.Normal),
            )

            Text(
                text = "${item.progressPercent}%",
                color = AppTheme.color.primary,
                style = AppTheme.textStyle.label.large.copy(fontWeight = FontWeight.Bold),
            )
        }

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(8.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(AppTheme.color.primary)
            )
        }


        // ── Meta row: students + next session + open link ─────────
        Row(
            modifier = Modifier.padding(top = 17.dp)
                .ignoreHorizontalParentPadding(16.dp)
                .border(1.17.dp, AppTheme.color.border)
                .fillMaxWidth()
                .background(AppTheme.color.bgHover)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Students
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_person),
                    contentDescription = null,
                    tint = AppTheme.color.textSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${item.studentCount}",
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal)
                )
            }
            // Next session time
            Row(modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_clock),
                    contentDescription = null,
                    tint = AppTheme.color.textSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = item.nextSessionTime,
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Normal)
                )
            }
            // Open course link
            Row(
                modifier = Modifier.clickAnimation { onOpenCourse() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "فتح المقرر",
                    color = AppTheme.color.primary,
                    style = AppTheme.textStyle.body.small,
                )
                Icon(
                    painter = painterResource(R.drawable.chevronleft),
                    contentDescription = null,
                    tint = AppTheme.color.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // ── Action buttons row ────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CourseActionButton(
                label = "محاضرات",
                iconRes = R.drawable.play,
                onClick = onLecturesClick,
                modifier = Modifier.weight(1f)
            )
            CourseActionButton(
                label = "واجبات",
                iconRes = R.drawable.ic_document,
                onClick = onHomeworkClick,
                modifier = Modifier.weight(1f)
            )
            CourseActionButton(
                label = "المواد",
                iconRes = R.drawable.download,
                onClick = onMaterialsClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CourseActionButton(
    label: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickAnimation { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = label,
            tint = AppTheme.color.primaryDark,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            color = AppTheme.color.textSecondary,
            style = AppTheme.textStyle.label.medium,
        )
    }
}
