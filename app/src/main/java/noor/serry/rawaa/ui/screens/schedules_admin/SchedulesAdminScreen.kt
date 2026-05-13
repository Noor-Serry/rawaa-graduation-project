package noor.serry.rawaa.ui.screens.schedules_admin

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import noor.serry.designsystem.design.AppTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SchedulesAdminScreen(
    viewModel: SchedulesAdminViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header ────────────────────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Text("الجدول الدراسي", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.primary)
                FloatingActionButton(
                    onClick        = viewModel::onAddSessionClicked,
                    containerColor = AppTheme.color.primary,
                    contentColor   = androidx.compose.ui.graphics.Color.White,
                    modifier       = Modifier.size(46.dp),
                ) { Icon(Icons.Default.Add, contentDescription = "إضافة جلسة") }
            }

            // ── Course filter chips ───────────────────────────────────────────
            if (state.courses.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = state.selectedCourseId == null,
                        onClick  = { viewModel.onCourseFilterSelected(null) },
                        label    = { Text("الكل") },
                    )
                    state.courses.forEach { course ->
                        FilterChip(
                            selected = state.selectedCourseId == course.id,
                            onClick  = { viewModel.onCourseFilterSelected(course.id) },
                            label    = { Text(course.name) },
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // ── Body ──────────────────────────────────────────────────────────
            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppTheme.color.primary)
                }
                state.errorMessage != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.errorMessage!!, color = AppTheme.color.error)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = viewModel::load) { Text("إعادة المحاولة") }
                    }
                }
                state.filteredSessions.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("لا توجد جلسات", color = AppTheme.color.textSecondary)
                }
                else -> LazyColumn(
                    contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(state.filteredSessions, key = { it.id }) { session ->
                        ScheduleSessionCard(
                            session  = session,
                            onEdit   = { viewModel.onEditSessionClicked(session) },
                            onDelete = { viewModel.onDeleteSessionClicked(session.id) },
                        )
                    }
                }
            }
        }

        // ── Delete dialog ─────────────────────────────────────────────────────
        if (state.pendingDeleteId != null) {
            AlertDialog(
                onDismissRequest = viewModel::onDeleteDismissed,
                title   = { Text("تأكيد الحذف") },
                text    = { Text("هل تريد حذف هذه الجلسة؟") },
                confirmButton = {
                    TextButton(onClick = viewModel::onDeleteConfirmed) { Text("حذف", color = AppTheme.color.error) }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::onDeleteDismissed) { Text("إلغاء") }
                },
            )
        }

        // ── Form sheet ────────────────────────────────────────────────────────
        if (state.showFormSheet) ScheduleFormSheet(state = state, listener = viewModel)
    }
}

// ── Session Card ──────────────────────────────────────────────────────────────

@Composable
private fun ScheduleSessionCard(
    session: SchedulesAdminUiState.ScheduleItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = AppTheme.color.bg),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier  = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = session.courseName ?: "مقرر غير محدد",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = AppTheme.color.primary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = "${session.day ?: ""} · ${session.startTime} – ${session.endTime}",
                    fontSize = 13.sp,
                    color = AppTheme.color.textSecondary,
                )
                if (session.roomName != null) {
                    Text(text = "القاعة: ${session.roomName}", fontSize = 12.sp, color = AppTheme.color.textSecondary)
                }
                Text(text = session.type, fontSize = 12.sp, color = AppTheme.color.primary)
            }
            Row {
                IconButton(onClick = onEdit)   { Icon(Icons.Default.Edit,   null, tint = AppTheme.color.primary) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = AppTheme.color.error) }
            }
        }
    }
}

// ── Schedule Form Sheet ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleFormSheet(
    state: SchedulesAdminUiState,
    listener: SchedulesAdminInteractionListener,
) {
    ModalBottomSheet(onDismissRequest = listener::onFormDismissed) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 32.dp),
        ) {
            Text(
                text       = if (state.isEditing) "تعديل الجلسة" else "إضافة جلسة جديدة",
                fontSize   = 18.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.primary,
            )
            Spacer(Modifier.height(16.dp))

            // Course picker
            var courseExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = courseExpanded, onExpandedChange = { courseExpanded = it }) {
                OutlinedTextField(
                    value         = state.courses.find { it.id == state.form.courseId }?.name ?: "اختر المقرر",
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("المقرر") },
                    trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = courseExpanded) },
                    modifier      = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    isError       = state.formError != null,
                )
                ExposedDropdownMenu(expanded = courseExpanded, onDismissRequest = { courseExpanded = false }) {
                    state.courses.forEach { course ->
                        DropdownMenuItem(
                            text    = { Text(course.name) },
                            onClick = { listener.onFormCourseSelected(course.id); courseExpanded = false },
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Day picker
            var dayExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = dayExpanded, onExpandedChange = { dayExpanded = it }) {
                OutlinedTextField(
                    value        = state.form.day,
                    onValueChange = {},
                    readOnly     = true,
                    label        = { Text("اليوم") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded) },
                    modifier     = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                )
                ExposedDropdownMenu(expanded = dayExpanded, onDismissRequest = { dayExpanded = false }) {
                    SchedulesAdminUiState.days.forEach { day ->
                        DropdownMenuItem(
                            text    = { Text(day) },
                            onClick = { listener.onFormDaySelected(day); dayExpanded = false },
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value         = state.form.startTime,
                    onValueChange = listener::onFormStartTimeChanged,
                    label         = { Text("وقت البداية") },
                    placeholder   = { Text("08:00") },
                    modifier      = Modifier.weight(1f),
                    singleLine    = true,
                )
                OutlinedTextField(
                    value         = state.form.endTime,
                    onValueChange = listener::onFormEndTimeChanged,
                    label         = { Text("وقت النهاية") },
                    placeholder   = { Text("10:00") },
                    modifier      = Modifier.weight(1f),
                    singleLine    = true,
                )
            }

            Spacer(Modifier.height(12.dp))

            // Type picker
            var typeExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = it }) {
                OutlinedTextField(
                    value        = state.form.type,
                    onValueChange = {},
                    readOnly     = true,
                    label        = { Text("النوع") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                    modifier     = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                )
                ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                    SchedulesAdminUiState.types.forEach { type ->
                        DropdownMenuItem(
                            text    = { Text(type) },
                            onClick = { listener.onFormTypeSelected(type); typeExpanded = false },
                        )
                    }
                }
            }

            if (state.formError != null) {
                Spacer(Modifier.height(8.dp))
                Text(state.formError, color = AppTheme.color.error, fontSize = 13.sp)
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick  = listener::onFormSubmit,
                enabled  = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isSaving) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                else Text(if (state.isEditing) "حفظ التعديلات" else "إضافة الجلسة")
            }
        }
    }
}
