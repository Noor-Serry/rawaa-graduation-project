package noor.serry.rawaa.ui.screens.students_teacher

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import noor.serry.designsystem.components.Icon
import noor.serry.designsystem.components.Text
import noor.serry.designsystem.components.utils.clickAnimation
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.R
import noor.serry.rawaa.ui.navigation.teatcher.TeacherBackStackProvider
import noor.serry.rawaa.ui.navigation.teatcher.TeacherRouteKeys

@Composable
fun StudentsScreen(
    onBack: () -> Unit = {},
    viewModel: StudentsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    HandleEffects(effects = viewModel.effect)

    StudentsContent(state = state, interactionListener = viewModel, onBack = onBack)
}

@Composable
private fun StudentsContent(
    state: StudentsUiState,
    interactionListener: StudentsInteractionListener,
    onBack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().background(AppTheme.color.bgHover)) {
        StudentsHeader(
            totalCount = state.totalCount,
            needsFollowUpCount = state.needsFollowUpCount,
            onBack = onBack,
            // Removed: onSendBulkMessage — no bulk messaging endpoint exists in the backend
        )

        // Search field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(AppTheme.color.bg)
                .border(1.17.dp, AppTheme.color.border, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_info),
                contentDescription = null,
                tint = AppTheme.color.textSecondary,
                modifier = Modifier.size(20.dp)
            )
            BasicTextField(
                value = state.searchQuery,
                onValueChange = interactionListener::onSearchChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = AppTheme.textStyle.body.medium.copy(color = AppTheme.color.text),
                decorationBox = { inner ->
                    if (state.searchQuery.isEmpty()) {
                        Text(text = "بحث باسم الطالب أو البريد...", color = AppTheme.color.textSecondary, style = AppTheme.textStyle.body.medium)
                    }
                    inner()
                }
            )
        }

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "جاري التحميل...", color = AppTheme.color.textSecondary, style = AppTheme.textStyle.body.medium)
            }
            state.displayedStudents.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "لا يوجد طلاب مطابقون", color = AppTheme.color.textSecondary, style = AppTheme.textStyle.body.medium)
            }
            else -> LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.displayedStudents, key = { it.id }) { student ->
                    StudentCard(
                        student = student,
                        onProfileClick = { interactionListener.onViewProfileClick(student.id) },
                        // Removed: onMessageClick — no per-student messaging endpoint exists in the backend
                    )
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun StudentsHeader(
    totalCount: Int,
    needsFollowUpCount: Int,
    onBack: () -> Unit,
    // Removed: onSendBulkMessage — no bulk messaging endpoint exists in the backend
) {
    Box {
        Column(
            modifier = Modifier.fillMaxWidth().height(140.dp)
                .background(brush = verticalGradient(listOf(AppTheme.color.primary, AppTheme.color.primaryLight)))
                .padding(horizontal = 24.dp).padding(top = 48.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                            .background(AppTheme.color.bg.copy(alpha = .15f)).clickAnimation { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(painter = painterResource(R.drawable.ic_arrow_forward), contentDescription = "رجوع", tint = AppTheme.color.bg, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text(text = "الطلاب", color = AppTheme.color.bg, style = AppTheme.textStyle.headline.small)
                        Text(text = "$totalCount طالب  •  $needsFollowUpCount يحتاج متابعة", color = AppTheme.color.bgSecondary, style = AppTheme.textStyle.label.medium)
                    }
                }
                // Removed: "رسالة جماعية" button — no bulk messaging endpoint in UniversityRepository
            }
        }
    }
}

@Composable
private fun StudentCard(
    student: StudentUiModel,
    onProfileClick: () -> Unit,
    // Removed: onMessageClick — no per-student messaging endpoint in UniversityRepository
    modifier: Modifier = Modifier,
) {
    val statusColor = when (student.statusType) {
        StudentStatusType.EXCELLENT       -> Color(0xFF10B981)
        StudentStatusType.GOOD            -> AppTheme.color.primary
        StudentStatusType.NEEDS_FOLLOW_UP -> AppTheme.color.error
    }

    Column(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.bg).border(1.17.dp, AppTheme.color.border, RoundedCornerShape(16.dp)).padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // ── Identity row ─────────────────────────────────────────────────────
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(AppTheme.color.primary.copy(alpha = .15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = student.name.firstOrNull()?.toString() ?: "?",
                    color = AppTheme.color.primary,
                    style = AppTheme.textStyle.headline.small,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = student.name, color = AppTheme.color.text, style = AppTheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold))
                Text(text = student.email, color = AppTheme.color.textSecondary, style = AppTheme.textStyle.label.medium, modifier = Modifier.padding(top = 2.dp))
            }
            Box(
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(statusColor.copy(alpha = .1f)).padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = student.statusLabel, color = statusColor, style = AppTheme.textStyle.label.medium.copy(fontWeight = FontWeight.Bold))
            }
        }

        // ── Real data chips — only fields actually returned by StudentDto ────
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // departmentName: String? — from StudentDto.department_name
            StudentMiniStat(
                label = "القسم",
                value = student.departmentName ?: "—",
                modifier = Modifier.weight(1f),
            )
            // level: Int? — from StudentDto.level
            StudentMiniStat(
                label = "المستوى",
                value = student.level?.toString() ?: "—",
                modifier = Modifier.weight(1f),
            )
            // gpa: String? — from StudentDto.gpa (raw server string, e.g. "3.75")
            StudentMiniStat(
                label = "المعدل",
                value = student.gpa ?: "—",
                modifier = Modifier.weight(1f),
            )
        }

        // Removed second stat row:
        //   الحضور (attendance) — StudentDto has no attendance field; was always "0%"
        //   الدرجة (grade)      — was a synthetic (gpa/4)*100 Int, not a real grade field
        //   الواجبات            — StudentDto has no assignments data; was always "0/0"

        // ── Actions — only "View Profile" has a backend endpoint (GET /api/students/{id}) ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(AppTheme.color.primary)
                .clickAnimation { onProfileClick() }
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "الملف الشخصي", color = AppTheme.color.bg, style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold))
        }
        // Removed: "رسالة" (Message) button — no per-student messaging endpoint in UniversityRepository
    }
}

@Composable
private fun StudentMiniStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.clip(RoundedCornerShape(10.dp)).background(AppTheme.color.bgHover).padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = AppTheme.color.primary, style = AppTheme.textStyle.body.small.copy(fontWeight = FontWeight.Bold))
        Text(text = label, color = AppTheme.color.textSecondary, style = AppTheme.textStyle.label.medium)
    }
}

@Composable
private fun HandleEffects(effects: Flow<StudentsEffect>) {
    val backstack = TeacherBackStackProvider.current
    LaunchedEffect(Unit) {
        effects.collectLatest { effect ->
            when (effect) {
                is StudentsEffect.NavigateToStudentProfile -> {     backstack.add(TeacherRouteKeys.StudentProfile(studentId = effect.studentId.toInt()))
                }
                // Removed: NavigateToSendMessage — no messaging endpoint
                // Removed: NavigateToSendBulkMessage — no bulk messaging endpoint
            }
        }
    }
}
