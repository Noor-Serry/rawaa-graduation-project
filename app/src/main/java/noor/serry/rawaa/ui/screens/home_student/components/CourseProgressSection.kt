package noor.serry.rawaa.ui.screens.home_student.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.design.AppTheme

data class CourseProgressItem(
    val courseName: String,
    val progressPercent: Int
)

@Composable
fun CourseProgressBar(
    item: CourseProgressItem,
    modifier: Modifier = Modifier
) {
    var started by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (started) item.progressPercent / 100f else 0f,
        animationSpec = tween(durationMillis = 800)
    )
    LaunchedEffect(Unit) { started = true }

    Column(modifier = modifier.fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(AppTheme.color.bg)
        .border(1.17.dp, AppTheme.color.border, RoundedCornerShape(16.dp))
        .padding(17.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.courseName,
                color = AppTheme.color.primaryDark,
                style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold),
            )

            Text(
                text = "${item.progressPercent}%",
                color = AppTheme.color.primary,
                style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
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
    }
}

@Composable
fun CourseProgressSection(
    items: List<CourseProgressItem>,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeader(
            title = "تقدم المقررات",
            onActionClick = onViewAll
        )
        Column(
            modifier = Modifier.padding(top = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items.forEach { item ->
                CourseProgressBar(item = item)
            }
        }
    }
}
