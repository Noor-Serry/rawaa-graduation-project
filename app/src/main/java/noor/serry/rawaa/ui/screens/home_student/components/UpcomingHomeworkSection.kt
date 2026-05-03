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


@Composable
fun UpcomingHomeworkCard(
    item: HomeStudentUiState.HomeworkItem,
    modifier: Modifier = Modifier
) {
    val badgeBg = when (item.deadlineType) {
        HomeStudentUiState.DeadlineType.TOMORROW -> AppTheme.color.secondaryHover
        else -> {
            Color(0xFFDBEAFE)
        }
    }
    val badgeTextColor = when (item.deadlineType) {
        HomeStudentUiState.DeadlineType.TOMORROW -> Color(0xFFF59E0B)
        else -> {
            Color(0xFF3B82F6)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg)
            .border(1.17.dp, AppTheme.color.border, RoundedCornerShape(16.dp))
            .padding(17.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(badgeBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_document),
                contentDescription = null,
                tint = badgeTextColor,
                modifier = Modifier.size(20.dp)
            )
        }

        // Right: text content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                color = AppTheme.color.text,
                style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = item.courseName,
                color = AppTheme.color.textSecondary,
                style = AppTheme.textStyle.body.small.copy(
                    fontWeight =
                        FontWeight.Normal
                ),
                modifier = Modifier.padding(top = 2.dp)
            )
            // Deadline badge
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeBg)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = item.deadline,
                    color = badgeTextColor,
                    style = AppTheme.textStyle.label.small.copy(fontWeight = FontWeight.Medium),
                )
            }
        }

        Text(
            text = "ابدأ الآن",
            color = AppTheme.color.primary,
            style = AppTheme.textStyle.body.small,
        )
    }
}

@Composable
fun UpcomingHomeworkSection(
    items: List<HomeStudentUiState.HomeworkItem>,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeader(
            title = "الواجبات القادمة",
            onActionClick = onViewAll
        )
        Column(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items.forEach { item ->
                UpcomingHomeworkCard(item = item)
            }
        }
    }
}

// Extension to fix the enum access
val HomeStudentUiState.HomeworkItem.deadlineType: HomeStudentUiState.DeadlineType get() = this.deadlineColor
