package noor.serry.rawaa.ui.screens.home_student.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R



@Composable
fun HomeHeaderSection(
    studentName: String,
    gpa: String,
    homeworkCount: Int,
    activeCoursesCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = verticalGradient(
                    colors = listOf(
                        AppTheme.color.primary,
                        AppTheme.color.primaryLight
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(24.dp)
    ) {
        // Greeting Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "مرحباً، $studentName!",
                    color = AppTheme.color.bg,
                    style = AppTheme.textStyle.headline.small,
                )
                Text(
                    text = "لنبدأ يوماً دراسياً مثمراً",
                    color = AppTheme.color.bg.copy(alpha = 0.8f),
                    style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Normal),
                )
            }
            // Yellow icon box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppTheme.color.secondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_trending_up), // use your trending/chart icon
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Stats Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HeaderStatCard(
                value = gpa,
                label = "المعدل\nالتراكمي",
                modifier = Modifier.weight(1f)
            )
            HeaderStatCard(
                value = homeworkCount.toString(),
                label = "واجب قادم",
                modifier = Modifier.weight(1f)
            )
            HeaderStatCard(
                value = activeCoursesCount.toString(),
                label = "مقرر نشط",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun HeaderStatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg.copy(alpha = .1f))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = AppTheme.color.bg,
            style = AppTheme.textStyle.headline.small,
        )
        Text(
            text = label,
            color = AppTheme.color.bg.copy(alpha = 0.8f),
            style = AppTheme.textStyle.label.medium,
            modifier = Modifier.padding(top = 4.dp),
            minLines = 2
        )
    }
}
