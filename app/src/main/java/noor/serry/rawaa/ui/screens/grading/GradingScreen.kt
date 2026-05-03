package noor.serry.rawaa.ui.screens.grading

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import noor.serry.designsystem.components.BaseButton
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R

@Composable
fun GradingScreen(
    onBack: () -> Unit = {},
    viewModel: GradingViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    HandleEffects(effects = viewModel.effect)

    GradingContent(state = state, interactionListener = viewModel, onBack = onBack)
}

@Composable
private fun GradingContent(
    state: GradingUiState,
    interactionListener: GradingInteractionListener,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().background(AppTheme.color.bgHover)
    ) {
        GradingHeader(
            totalPending = state.totalPendingCount,
            totalGraded = state.totalGradedCount,
            onBack = onBack,
        )

        GradingTabRow(
            selectedTab = state.selectedTab,
            pendingCount = state.totalPendingCount,
            gradedCount = state.totalGradedCount,
            onTabSelected = interactionListener::onTabSelected,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        if (state.selectedTab == GradingTab.PENDING) {
            if (state.displayedPending.isEmpty()) {
                EmptyGradingState(message = "لا توجد واجبات قيد الانتظار")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.displayedPending, key = { it.id }) { item ->
                        PendingAssignmentCard(item = item, interactionListener = interactionListener)
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        } else {
            if (state.displayedGraded.isEmpty()) {
                EmptyGradingState(message = "لا توجد واجبات مصححة")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.displayedGraded, key = { it.id }) { item ->
                        GradedAssignmentCard(item = item)
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun GradingHeader(
    totalPending: Int,
    totalGraded: Int,
    onBack: () -> Unit,
) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth().height(160.dp)
                .background(brush = verticalGradient(listOf(AppTheme.color.primary, AppTheme.color.primaryLight)))
                .padding(horizontal = 24.dp).padding(top = 48.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                        .background(AppTheme.color.bg.copy(alpha = .15f))
                        .clickAnimation { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painter = painterResource(R.drawable.ic_arrow_forward), contentDescription = "رجوع", tint = AppTheme.color.bg, modifier = Modifier.size(18.dp))
                }
                Text(text = "الدرجات والامتحانات", color = AppTheme.color.bg, style = AppTheme.textStyle.headline.small)
            }
        }
    }
}

@Composable
private fun GradingTabRow(
    selectedTab: GradingTab,
    pendingCount: Int,
    gradedCount: Int,
    onTabSelected: (GradingTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = AppTheme.color.border
    Row(
        modifier = modifier.fillMaxWidth()
            .background(AppTheme.color.bg, RoundedCornerShape(16.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GradingTab.entries.forEach { tab ->
            val isSelected = selectedTab == tab
            val bgColor by animateColorAsState(if (isSelected) AppTheme.color.primary else AppTheme.color.bg, tween(700))
            val textColor by animateColorAsState(if (isSelected) AppTheme.color.bg else AppTheme.color.textSecondary, tween(700))
            val label = when (tab) {
                GradingTab.PENDING -> "قيد الانتظار ($pendingCount)"
                GradingTab.GRADED -> "تم التصحيح ($gradedCount)"
            }
            Box(
                modifier = Modifier.weight(1f).background(bgColor, RoundedCornerShape(12.dp))
                    .clickAnimation { onTabSelected(tab) }.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = label, color = textColor, style = AppTheme.textStyle.body.small)
            }
        }
    }
}

@Composable
private fun PendingAssignmentCard(
    item: PendingAssignmentUiModel,
    interactionListener: GradingInteractionListener,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg).border(1.17.dp, AppTheme.color.border, RoundedCornerShape(16.dp)).padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.title, color = AppTheme.color.text, style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold))
                Text(text = item.courseName, color = AppTheme.color.textSecondary, style = AppTheme.textStyle.label.medium, modifier = Modifier.padding(top = 2.dp))
            }
            Box(
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(AppTheme.color.error.copy(alpha = .1f)).padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = "آخر موعد: ${item.deadline}", color = AppTheme.color.error, style = AppTheme.textStyle.label.medium)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "${item.submittedCount}/${item.totalStudents} طالب سلّم", color = AppTheme.color.textSecondary, style = AppTheme.textStyle.label.medium)
            Text(text = "${item.completionPercent}%", color = AppTheme.color.primary, style = AppTheme.textStyle.label.medium.copy(fontWeight = FontWeight.Bold))
        }

        // Progress bar
        Box(
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(AppTheme.color.borderFocus.copy(alpha = .2f))
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(item.completionProgress).height(6.dp).background(AppTheme.color.primary, RoundedCornerShape(3.dp))
            )
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                    .border(1.17.dp, AppTheme.color.border, RoundedCornerShape(12.dp))
                    .clickAnimation { interactionListener.onViewDetailsClick(item.id) }.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "التفاصيل", color = AppTheme.color.primaryDark, style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold))
            }
            Box(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                    .background(AppTheme.color.primary)
                    .clickAnimation { interactionListener.onStartGradingClick(item.id) }.padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "بدء التصحيح", color = AppTheme.color.bg, style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
private fun GradedAssignmentCard(item: GradedAssignmentUiModel, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg).border(1.17.dp, AppTheme.color.border, RoundedCornerShape(16.dp)).padding(17.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.title, color = AppTheme.color.text, style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold))
            Text(text = item.courseName, color = AppTheme.color.textSecondary, style = AppTheme.textStyle.label.medium, modifier = Modifier.padding(top = 2.dp))
            Text(text = "${item.totalStudents} طالب  •  ${item.gradedDate}", color = AppTheme.color.textSecondary, style = AppTheme.textStyle.label.medium, modifier = Modifier.padding(top = 4.dp))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "${item.averageGrade}%", color = AppTheme.color.primary, style = AppTheme.textStyle.headline.small)
            Text(text = "متوسط", color = AppTheme.color.textSecondary, style = AppTheme.textStyle.label.medium)
        }
    }
}

@Composable
private fun EmptyGradingState(message: String) {
    Box(modifier = Modifier.fillMaxSize().padding(vertical = 48.dp), contentAlignment = Alignment.Center) {
        Text(text = message, color = AppTheme.color.textSecondary, style = AppTheme.textStyle.body.medium)
    }
}

@Composable
private fun HandleEffects(effects: Flow<GradingEffect>) {
    LaunchedEffect(Unit) {
        effects.collectLatest { effect ->
            when (effect) {
                is GradingEffect.NavigateToGradeAssignment   -> { /* TODO */ }
                is GradingEffect.NavigateToAssignmentDetails -> { /* TODO */ }
                is GradingEffect.NavigateToAssignmentStats   -> { /* TODO */ }
            }
        }
    }
}
