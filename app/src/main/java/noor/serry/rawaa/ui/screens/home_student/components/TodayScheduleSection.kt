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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.screens.home_student.HomeStudentUiState


@Composable
fun TodayScheduleCard(
    item: HomeStudentUiState.ScheduleItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg)
            .border(1.17.dp, AppTheme.color.border, RoundedCornerShape(16.dp))
            .padding(17.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Clock icon on left (for RTL, appears on left)
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(
                    RoundedCornerShape(
                        12.dp
                    )
                )
                .background(AppTheme.color.borderFocus.copy(alpha = .1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_clock),
                contentDescription = null,
                tint = AppTheme.color.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Yellow calendar icon box
            Column {
                Text(
                    text = item.courseName,
                    color = AppTheme.color.text,
                    style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = item.professorName,
                    color = AppTheme.color.textSecondary,
                    style = AppTheme.textStyle.body.small.copy(
                        fontWeight = FontWeight.Normal
                    ),
                )
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_clock),
                        contentDescription = null,
                        tint = AppTheme.color.primary,
                        modifier = Modifier.size(12.dp)
                    )

                    Text(
                        text = "${item.time}",
                        color = AppTheme.color.textSecondary,
                        style = AppTheme.textStyle.label.medium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Text(
                        text = "${item.location}",
                        color = AppTheme.color.textSecondary,
                        style = AppTheme.textStyle.label.medium,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    AppTheme
                        .color.secondary
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_calendar),
                contentDescription = null,
                tint = AppTheme.color.primary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun TodayScheduleSection(
    items: List<HomeStudentUiState.ScheduleItem>,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeader(
            title = "جدول اليوم",
            onActionClick = onViewAll
        )
        Column(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items.forEach { item ->
                TodayScheduleCard(item = item)
            }
        }
    }
}
