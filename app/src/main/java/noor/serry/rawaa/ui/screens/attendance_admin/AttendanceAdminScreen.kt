package noor.serry.rawaa.ui.screens.attendance_admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.ui.navigation.university_admin.UniversityAdminBackStackProvider
import noor.serry.rawaa.ui.navigation.university_admin.UniversityAdminRouteKeys
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AttendanceAdminScreen(
    viewModel: AttendanceAdminViewModel = koinViewModel(),
) {
    val state    by viewModel.state.collectAsStateWithLifecycle()
    val backStack = UniversityAdminBackStackProvider.current

    // Navigate on one-time effects
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AttendanceAdminEffect.NavigateToStudentAttendance ->{}
                is AttendanceAdminEffect.NavigateToCourseAttendance  ->{}
                else -> { /* show snackbar */ }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header ────────────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Text("الحضور والغياب", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.primary)
                FloatingActionButton(
                    onClick        = viewModel::onMarkAttendanceClicked,
                    containerColor = AppTheme.color.primary,
                    contentColor   = androidx.compose.ui.graphics.Color.White,
                    modifier       = Modifier.size(46.dp),
                ) { Icon(Icons.Default.Add, contentDescription = "تسجيل حضور") }
            }

            // ── Tabs ──────────────────────────────────────────────────────────
            TabRow(
                selectedTabIndex = state.selectedTab.ordinal,
                containerColor   = AppTheme.color.primary,
            ) {
                AttendanceAdminUiState.AttendanceTab.values().forEach { tab ->
                    Tab(
                        selected = state.selectedTab == tab,
                        onClick  = { viewModel.onTabSelected(tab) },
                        text     = {
                            Text(
                                text = when (tab) {
                                    AttendanceAdminUiState.AttendanceTab.STUDENT  -> "الطلاب"
                                    AttendanceAdminUiState.AttendanceTab.COURSE   -> "المقررات"
                                    AttendanceAdminUiState.AttendanceTab.EMPLOYEE -> "الموظفون"
                                }
                            )
                        },
                    )
                }
            }

            // ── Body ──────────────────────────────────────────────────────────
            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppTheme.color.primary)
                }
                state.errorMessage != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.errorMessage!!, color = AppTheme.color.error)
                }
                else -> when (state.selectedTab) {
                    AttendanceAdminUiState.AttendanceTab.STUDENT  -> StudentAttendanceList(state, viewModel)
                    AttendanceAdminUiState.AttendanceTab.COURSE   -> CourseAttendanceList(state, viewModel)
                    AttendanceAdminUiState.AttendanceTab.EMPLOYEE -> EmployeeAttendanceList(state, viewModel)
                }
            }
        }

        // ── Mark attendance sheet ─────────────────────────────────────────────
        if (state.showMarkSheet) MarkAttendanceSheet(state = state, listener = viewModel)
    }
}

// ── Student Tab ───────────────────────────────────────────────────────────────

@Composable
private fun StudentAttendanceList(
    state: AttendanceAdminUiState,
    listener: AttendanceAdminInteractionListener,
) {
    if (state.studentRecords.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("لا توجد سجلات حضور", color = AppTheme.color.textSecondary)
        }
        return
    }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(state.studentRecords, key = { it.id }) { row ->
            AttendanceRowCard(
                name      = row.studentName ?: "طالب #${row.userId}",
                subtitle  = "${row.courseName ?: ""} · ${row.date}",
                status    = row.status,
                onClick   = { listener.onStudentRowClicked(row.userId) },
            )
        }
    }
}

// ── Course Tab ────────────────────────────────────────────────────────────────

@Composable
private fun CourseAttendanceList(
    state: AttendanceAdminUiState,
    listener: AttendanceAdminInteractionListener,
) {
    // Show available courses so admin can tap one to drill into course attendance
    if (state.courses.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("لا توجد مقررات", color = AppTheme.color.textSecondary)
        }
        return
    }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(state.courses, key = { it.id }) { course ->
            AttendanceRowCard(
                name     = course.name,
                subtitle = "عرض سجل حضور المقرر",
                status   = null,
                onClick  = { listener.onCourseRowClicked(course.id) },
            )
        }
    }
}

// ── Employee Tab ──────────────────────────────────────────────────────────────

@Composable
private fun EmployeeAttendanceList(
    state: AttendanceAdminUiState,
    listener: AttendanceAdminInteractionListener,
) {
    Column {
        Button(
            onClick  = listener::onEmployeeCheckIn,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        ) { Text("تسجيل حضور موظف") }

        if (state.employeeRecords.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("لا توجد سجلات", color = AppTheme.color.textSecondary)
            }
            return@Column
        }
        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(state.employeeRecords, key = { it.id }) { row ->
                AttendanceRowCard(
                    name     = row.userName ?: "موظف #${row.userId}",
                    subtitle = "${row.date} · دخول: ${row.checkIn ?: "—"}  خروج: ${row.checkOut ?: "—"}",
                    status   = row.status,
                    onClick  = {},
                )
            }
        }
    }
}

// ── Shared row card ───────────────────────────────────────────────────────────

@Composable
private fun AttendanceRowCard(
    name: String,
    subtitle: String,
    status: String?,
    onClick: () -> Unit,
) {
    Card(
        onClick   = onClick,
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = AppTheme.color.bg),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier  = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AppTheme.color.primary)
                Spacer(Modifier.height(4.dp))
                Text(subtitle, fontSize = 12.sp, color = AppTheme.color.textSecondary)
            }
            if (status != null) {
                StatusBadge(status)
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (bg, text) = when (status.lowercase()) {
        "present" -> AppTheme.color.primary.copy(alpha = 0.12f) to AppTheme.color.primary
        "absent"  -> AppTheme.color.error.copy(alpha = 0.12f)   to AppTheme.color.error
        "late"    -> androidx.compose.ui.graphics.Color(0xFFFFF3CD) to androidx.compose.ui.graphics.Color(0xFF856404)
        else      -> AppTheme.color.textSecondary.copy(alpha = 0.12f) to AppTheme.color.textSecondary
    }
    Surface(shape = RoundedCornerShape(8.dp), color = bg) {
        Text(
            text     = status,
            fontSize = 12.sp,
            color    = text,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

// ── Mark Attendance Sheet ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MarkAttendanceSheet(
    state: AttendanceAdminUiState,
    listener: AttendanceAdminInteractionListener,
) {
    ModalBottomSheet(onDismissRequest = listener::onMarkFormDismissed) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 32.dp),
        ) {
            Text("تسجيل حضور طالب", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.primary)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value         = state.markForm.studentUserId?.toString() ?: "",
                onValueChange = listener::onMarkFormStudentIdChanged,
                label         = { Text("رقم الطالب (User ID)") },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true,
            )
            Spacer(Modifier.height(12.dp))

            // Course picker
            var courseExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = courseExpanded, onExpandedChange = { courseExpanded = it }) {
                OutlinedTextField(
                    value         = state.courses.find { it.id == state.markForm.courseId }?.name ?: "اختر المقرر",
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("المقرر") },
                    trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(courseExpanded) },
                    modifier      = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                )
                ExposedDropdownMenu(expanded = courseExpanded, onDismissRequest = { courseExpanded = false }) {
                    state.courses.forEach { c ->
                        DropdownMenuItem(
                            text    = { Text(c.name) },
                            onClick = { listener.onMarkFormCourseSelected(c.id); courseExpanded = false },
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value         = state.markForm.date,
                onValueChange = listener::onMarkFormDateChanged,
                label         = { Text("التاريخ (YYYY-MM-DD)") },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true,
            )
            Spacer(Modifier.height(12.dp))

            // Status picker
            var statusExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = statusExpanded, onExpandedChange = { statusExpanded = it }) {
                OutlinedTextField(
                    value         = state.markForm.status,
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("الحالة") },
                    trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(statusExpanded) },
                    modifier      = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                )
                ExposedDropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                    AttendanceAdminUiState.statuses.forEach { s ->
                        DropdownMenuItem(text = { Text(s) }, onClick = { listener.onMarkFormStatusSelected(s); statusExpanded = false })
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value         = state.markForm.notes,
                onValueChange = listener::onMarkFormNotesChanged,
                label         = { Text("ملاحظات (اختياري)") },
                modifier      = Modifier.fillMaxWidth(),
            )

            if (state.markError != null) {
                Spacer(Modifier.height(8.dp))
                Text(state.markError, color = AppTheme.color.error, fontSize = 13.sp)
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick  = listener::onMarkFormSubmit,
                enabled  = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isSaving) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                else Text("تسجيل الحضور")
            }
        }
    }
}
