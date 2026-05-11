package noor.serry.rawaa.ui.screens.grading

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun GradingScreen(
    viewModel: GradingViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val displayedItems = if (state.selectedTab == GradingTab.PENDING)
        state.pendingAssignments else state.gradedAssignments

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.bg),
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppTheme.color.primaryDark)
                .padding(horizontal = 20.dp, vertical = 24.dp),
        ) {
            Column {
                Text(
                    text = "التصحيح والتقييم",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "تصحيح وتقييم أعمال الطلاب",
                    color = Color.White.copy(0.75f),
                    fontSize = 13.sp,
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    GradingStatCard(
                        modifier = Modifier.weight(1f),
                        iconRes = R.drawable.badge,
                        label = "تم التصحيح",
                        count = state.gradedCount,
                        subLabel = "واجب فحصه",
                        iconColor = Color(0xFF4CAF50),
                    )
                    GradingStatCard(
                        modifier = Modifier.weight(1f),
                        iconRes = R.drawable.ic_book,
                        label = "قيد التصحيح",
                        count = state.pendingCount,
                        subLabel = "واجب معلق",
                        iconColor = AppTheme.color.secondary,
                    )
                }
            }
        }

        // Search bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicTextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(
                        fontSize = 13.sp,
                        color = AppTheme.color.text,
                    ),
                    decorationBox = { inner ->
                        if (state.searchQuery.isEmpty()) {
                            Text("بحث عن واجب...", fontSize = 13.sp, color = AppTheme.color.textSecondary)
                        }
                        inner()
                    },
                )
                Icon(
                    painter = painterResource(R.drawable.badge),
                    contentDescription = null,
                    tint = AppTheme.color.textSecondary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        // Filter
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(Modifier.weight(1f))
                Icon(
                    painter = painterResource(R.drawable.badge),
                    contentDescription = "تصفية",
                    tint = AppTheme.color.textSecondary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            GradingTabButton(
                label = "معلقة (${state.pendingAssignments.size * 45})",
                isSelected = state.selectedTab == GradingTab.PENDING,
                onClick = { viewModel.selectTab(GradingTab.PENDING) },
                modifier = Modifier.weight(1f),
            )
            GradingTabButton(
                label = "فصحصة (${state.gradedAssignments.size * 215})",
                isSelected = state.selectedTab == GradingTab.GRADED,
                onClick = { viewModel.selectTab(GradingTab.GRADED) },
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(8.dp))

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppTheme.color.primary)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(displayedItems) { item ->
                    if (item.isGraded) {
                        GradedAssignmentCard(item = item)
                    } else {
                        PendingAssignmentCard(
                            item = item,
                            onStartGrading = { viewModel.onStartGrading(item.courseId) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GradingStatCard(
    modifier: Modifier,
    iconRes: Int,
    label: String,
    count: Int,
    subLabel: String,
    iconColor: Color,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp),
            )
            Column {
                Text(
                    text = "$count",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = iconColor,
                )
                Text(subLabel, fontSize = 11.sp, color = AppTheme.color.textSecondary)
            }
        }
    }
}

@Composable
private fun GradingTabButton(label: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) AppTheme.color.primaryDark else Color.White)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else AppTheme.color.textSecondary,
        )
    }
}

@Composable
private fun PendingAssignmentCard(item: GradingItem, onStartGrading: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.assignmentTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = AppTheme.color.text,
                    )
                    Text(
                        text = item.courseName,
                        fontSize = 12.sp,
                        color = AppTheme.color.textSecondary,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 2.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_book),
                            contentDescription = null,
                            tint = AppTheme.color.textSecondary,
                            modifier = Modifier.size(12.dp),
                        )
                        Text(
                            text = "${item.deadlineDaysAgo}  •  ${item.totalPoints} نقطة",
                            fontSize = 11.sp,
                            color = AppTheme.color.textSecondary,
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppTheme.color.secondary.copy(0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_book),
                        contentDescription = null,
                        tint = AppTheme.color.secondary,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Submission progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${item.submittedCount} من ${item.totalStudents} طالب سلّم",
                    fontSize = 12.sp,
                    color = AppTheme.color.textSecondary,
                )
                Text(
                    text = "${item.submittedPercent}%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.color.secondary,
                )
            }

            Spacer(Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { item.submittedPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = AppTheme.color.secondary,
                trackColor = AppTheme.color.bg,
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = "متوسط وقت التصحيح  ${item.avgGradingMinutes} دقيقة",
                fontSize = 11.sp,
                color = AppTheme.color.textSecondary,
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onStartGrading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppTheme.color.primaryDark),
            ) {
                Icon(
                    painter = painterResource(R.drawable.badge),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "ابدأ التصحيح (${item.submittedCount})",
                    color = Color.White,
                    fontSize = 13.sp,
                )
            }
        }
    }
}

@Composable
private fun GradedAssignmentCard(item: GradingItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.assignmentTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = AppTheme.color.text,
                    )
                    Text(
                        text = item.courseName,
                        fontSize = 12.sp,
                        color = AppTheme.color.textSecondary,
                    )
                    Text(
                        text = "${item.deadlineDaysAgo}  •  ${item.totalPoints} نقطة",
                        fontSize = 11.sp,
                        color = AppTheme.color.textSecondary,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF4CAF50).copy(0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.badge),
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                GradedStatItem(value = "100%", label = "اكتمال")
                GradedStatItem(value = "${item.avgGrade ?: 0}%", label = "المعدل", highlight = true)
                GradedStatItem(value = "${item.totalStudents}", label = "طالب")
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = ButtonDefaults.outlinedButtonBorder(),
                ) {
                    Text("عرض التفاصيل", fontSize = 12.sp, color = AppTheme.color.text)
                }
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text("الإحصائيات", fontSize = 12.sp, color = AppTheme.color.text)
                }
            }
        }
    }
}

@Composable
private fun GradedStatItem(value: String, label: String, highlight: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (highlight) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8F5E9))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
            }
        } else {
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.text)
        }
        Text(label, fontSize = 11.sp, color = AppTheme.color.textSecondary)
    }
}
