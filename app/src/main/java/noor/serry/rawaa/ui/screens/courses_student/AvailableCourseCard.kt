package noor.serry.rawaa.ui.screens.courses_student

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
import noor.serry.designsystem.components.BaseButton
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R

@Composable
fun AvailableCourseCard(
    item: AvailableCourseItem,
    onEnrollClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg)
            .border(1.17.dp, AppTheme.color.border, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
                Text(
                    text = item.courseCode,
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.label.medium,
                )
            }
         LevelBadge(item.level)
        }


        // Meta row: students + weeks
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_person),
                    contentDescription = null,
                    tint = AppTheme.color.textSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "${item.studentCount} طالب",
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.label.medium,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_clock),
                    contentDescription = null,
                    tint = AppTheme.color.textSecondary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // Enroll button
        BaseButton(
            "التسجيل في المقرر",
            onClick = {onEnrollClick() },
            roundedCornerSize = 12.dp
        )
    }
}

@Composable
private fun LevelBadge(
    level: CourseLevel,
    modifier: Modifier = Modifier,
) {
    val (label, bgColor, textColor) = when (level) {
        CourseLevel.ADVANCED -> Triple(
            "متقدم",
            Color(0xFFFEF3C7),
            Color(0xFFF59E0B)
        )
        CourseLevel.INTERMEDIATE -> Triple(
            "متوسط",
            Color(0xFFDBEAFE),
            Color(0xFF3B82F6)
        )
        CourseLevel.BEGINNER -> Triple(
            "مبتدئ",
            Color(0xFFDCFCE7),
            Color(0xFF22C55E)
        )
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            style = AppTheme.textStyle.label.medium,
        )
    }
}
