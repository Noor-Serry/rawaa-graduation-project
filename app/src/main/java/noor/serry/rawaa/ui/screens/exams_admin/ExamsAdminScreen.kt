package noor.serry.rawaa.ui.screens.exams_admin

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.ui.navigation.university_admin.UniversityAdminBackStackProvider
import noor.serry.rawaa.ui.navigation.university_admin.UniversityAdminRouteKeys
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ExamsAdminScreen(
    viewModel: ExamsAdminViewModel = koinViewModel(),
) {
    val state     by viewModel.state.collectAsStateWithLifecycle()
    val backStack  = UniversityAdminBackStackProvider.current

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            if (effect is ExamsAdminEffect.NavigateToExamDetail) {
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            Row(
                modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Text("الامتحانات", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.text)
                FloatingActionButton(
                    onClick        = viewModel::onAddExamClicked,
                    containerColor = AppTheme.color.primary,
                    contentColor   = Color.White,
                    modifier       = Modifier.size(46.dp),
                ) { Icon(Icons.Default.Add, contentDescription = "إضافة امتحان") }
            }

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
                state.exams.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("لا توجد امتحانات", color = AppTheme.color.textSecondary)
                }
                else -> LazyColumn(
                    contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(state.exams, key = { it.id }) { exam ->
                        ExamCard(
                            exam      = exam,
                            onClick   = { viewModel.onExamClicked(exam.id) },
                            onPublish = { viewModel.onPublishExam(exam.id) },
                        )
                    }
                }
            }
        }

        if (state.showCreateSheet) CreateExamSheet(state = state, listener = viewModel)
    }
}

@Composable
private fun ExamCard(
    exam: ExamsAdminUiState.ExamItem,
    onClick: () -> Unit,
    onPublish: () -> Unit,
) {
    Card(
        onClick   = onClick,
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = AppTheme.color.bgSecondary),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier  = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Text(exam.title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = AppTheme.color.text, modifier = Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (exam.isPublished) AppTheme.color.primary.copy(alpha = 0.1f) else AppTheme.color.textSecondary.copy(alpha = 0.1f),
                ) {
                    Text(
                        text     = if (exam.isPublished) "منشور" else "مسودة",
                        fontSize = 11.sp,
                        color    = if (exam.isPublished) AppTheme.color.primary else AppTheme.color.textSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(exam.courseName ?: "—", fontSize = 13.sp, color = AppTheme.color.textSecondary)
            Text("${exam.type} · ${exam.totalMarks?.toInt() ?: "—"} درجة · ${exam.durationMin ?: "—"} دقيقة", fontSize = 12.sp, color = AppTheme.color.textSecondary)
            if (exam.startAt != null) {
                Text("${exam.startAt} ← ${exam.endAt ?: ""}", fontSize = 11.sp, color = AppTheme.color.textSecondary)
            }
            if (!exam.isPublished) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick  = onPublish,
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("نشر الامتحان") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateExamSheet(
    state: ExamsAdminUiState,
    listener: ExamsAdminInteractionListener,
) {
    ModalBottomSheet(onDismissRequest = listener::onFormDismissed) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 32.dp),
        ) {
            Text("إنشاء امتحان جديد", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.text)
            Spacer(Modifier.height(16.dp))

            // Course
            var courseExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = courseExpanded, onExpandedChange = { courseExpanded = it }) {
                OutlinedTextField(
                    value        = state.courses.find { it.id == state.createForm.courseId }?.name ?: "اختر المقرر",
                    onValueChange = {},
                    readOnly     = true,
                    label        = { Text("المقرر") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(courseExpanded) },
                    modifier     = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                )
                ExposedDropdownMenu(expanded = courseExpanded, onDismissRequest = { courseExpanded = false }) {
                    state.courses.forEach { c ->
                        DropdownMenuItem(text = { Text(c.name) }, onClick = { listener.onFormCourseSelected(c.id); courseExpanded = false })
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(value = state.createForm.title, onValueChange = listener::onFormTitleChanged, label = { Text("عنوان الامتحان") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(12.dp))

            // Type
            var typeExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = it }) {
                OutlinedTextField(
                    value        = state.createForm.type,
                    onValueChange = {},
                    readOnly     = true,
                    label        = { Text("النوع") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(typeExpanded) },
                    modifier     = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                )
                ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                    ExamsAdminUiState.examTypes.forEach { t ->
                        DropdownMenuItem(text = { Text(t) }, onClick = { listener.onFormTypeSelected(t); typeExpanded = false })
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = state.createForm.totalMarks, onValueChange = listener::onFormTotalMarksChanged, label = { Text("الدرجة الكلية") }, modifier = Modifier.weight(1f), singleLine = true)
                OutlinedTextField(value = state.createForm.durationMin, onValueChange = listener::onFormDurationChanged, label = { Text("المدة (دقيقة)") }, modifier = Modifier.weight(1f), singleLine = true)
            }
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(value = state.createForm.startAt, onValueChange = listener::onFormStartAtChanged, label = { Text("وقت البداية") }, placeholder = { Text("2025-01-15 09:00:00") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = state.createForm.endAt, onValueChange = listener::onFormEndAtChanged, label = { Text("وقت النهاية") }, placeholder = { Text("2025-01-15 11:00:00") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            if (state.formError != null) { Spacer(Modifier.height(8.dp)); Text(state.formError, color = AppTheme.color.error, fontSize = 13.sp) }

            Spacer(Modifier.height(20.dp))
            Button(onClick = listener::onFormSubmit, enabled = !state.isSaving, modifier = Modifier.fillMaxWidth()) {
                if (state.isSaving) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp) else Text("إنشاء الامتحان")
            }
        }
    }
}
