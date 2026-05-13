package noor.serry.rawaa.ui.screens.exams_admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.data.dto.QuestionDto
import noor.serry.rawaa.data.dto.QuestionRequest
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.navigation.university_admin.UniversityAdminBackStackProvider
import org.koin.compose.viewmodel.koinViewModel

// ── State ─────────────────────────────────────────────────────────────────────

data class QuestionsAdminState(
    val questions: List<QuestionDto> = emptyList(),
    val courses: List<Pair<Int, String>> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val showSheet: Boolean = false,
    val editing: QuestionDto? = null,
    val form: QuestionForm = QuestionForm(),
    val isSaving: Boolean = false,
    val formError: String? = null,
    val pendingDeleteId: Int? = null,
) {
    data class QuestionForm(
        val courseId: Int? = null,
        val type: String = "mcq",
        val difficulty: String = "medium",
        val text: String = "",
        val optionA: String = "",
        val optionB: String = "",
        val optionC: String = "",
        val optionD: String = "",
        val correctAnswer: String = "",
        val marks: String = "1",
    )

    val isEditing: Boolean get() = editing != null
    companion object {
        val questionTypes  = listOf("mcq", "true_false", "short_answer", "essay")
        val difficulties   = listOf("easy", "medium", "hard")
    }
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

class QuestionsAdminViewModel(
    private val repository: UniversityRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(QuestionsAdminState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val questions = repository.getQuestions()
                val courses   = repository.getCourses(isActive = 1)
                _state.value = _state.value.copy(
                    isLoading = false,
                    questions = questions.data ?: emptyList(),
                    courses   = courses.data.map { it.id to it.name },
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun onAddClicked() { _state.value = _state.value.copy(showSheet = true, editing = null, form = QuestionsAdminState.QuestionForm(), formError = null) }

    fun onEditClicked(q: QuestionDto) {
        _state.value = _state.value.copy(
            showSheet = true,
            editing   = q,
            form      = QuestionsAdminState.QuestionForm(
                courseId      = q.courseId,
                type          = q.type,
                difficulty    = q.difficulty,
                text          = q.text,
                optionA       = q.optionA ?: "",
                optionB       = q.optionB ?: "",
                optionC       = q.optionC ?: "",
                optionD       = q.optionD ?: "",
                correctAnswer = q.correctAnswer ?: "",
                marks         = q.marks.toString(),
            ),
            formError = null,
        )
    }

    fun onDeleteClicked(id: Int)   { _state.value = _state.value.copy(pendingDeleteId = id) }
    fun onDeleteDismissed()         { _state.value = _state.value.copy(pendingDeleteId = null) }
    fun onDeleteConfirmed() {
        val id = _state.value.pendingDeleteId ?: return
        _state.value = _state.value.copy(pendingDeleteId = null)
        viewModelScope.launch {
            try {
                repository.deleteQuestion(id)
                _state.value = _state.value.copy(questions = _state.value.questions.filter { it.id != id })
            } catch (_: Exception) {}
        }
    }

    fun onDismissSheet() { _state.value = _state.value.copy(showSheet = false, formError = null) }

    fun onFormField(update: QuestionsAdminState.QuestionForm.() -> QuestionsAdminState.QuestionForm) {
        _state.value = _state.value.copy(form = _state.value.form.update(), formError = null)
    }

    fun onFormSubmit() {
        val form = _state.value.form
        if (form.courseId == null) { _state.value = _state.value.copy(formError = "يرجى اختيار المقرر"); return }
        if (form.text.isBlank())   { _state.value = _state.value.copy(formError = "نص السؤال مطلوب"); return }

        val request = QuestionRequest(
            courseId      = form.courseId,
            type          = form.type,
            difficulty    = form.difficulty,
            text          = form.text,
            optionA       = form.optionA.ifBlank { null },
            optionB       = form.optionB.ifBlank { null },
            optionC       = form.optionC.ifBlank { null },
            optionD       = form.optionD.ifBlank { null },
            correctAnswer = form.correctAnswer.ifBlank { null },
            marks         = form.marks.toFloatOrNull() ?: 1f,
        )
        _state.value = _state.value.copy(isSaving = true, formError = null)
        viewModelScope.launch {
            try {
                val editing = _state.value.editing
                if (editing == null) {
                    val resp = repository.createQuestion(request)
                    val newQ = resp.data!!
                    _state.value = _state.value.copy(isSaving = false, showSheet = false, questions = listOf(newQ) + _state.value.questions)
                } else {
                    val resp = repository.updateQuestion(editing.id, request)
                    val updated = resp.data!!
                    _state.value = _state.value.copy(isSaving = false, showSheet = false,
                        questions = _state.value.questions.map { if (it.id == updated.id) updated else it })
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isSaving = false, formError = e.message)
            }
        }
    }
}

// ── Screen ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionsAdminScreen(
    viewModel: QuestionsAdminViewModel = koinViewModel(),
) {
    val state     by viewModel.state.collectAsStateWithLifecycle()
    val backStack  = UniversityAdminBackStackProvider.current

    Scaffold(
        topBar = {
            TopAppBar(
                title          = { Text("بنك الأسئلة") },
                navigationIcon = {
                    IconButton(onClick = { backStack.removeLastOrNull() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::onAddClicked, containerColor = AppTheme.color.primary) {
                Icon(Icons.Default.Add, contentDescription = "إضافة سؤال", tint = Color.White)
            }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = AppTheme.color.primary) }
                state.errorMessage != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.errorMessage!!, color = AppTheme.color.error) }
                state.questions.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("لا توجد أسئلة", color = AppTheme.color.textSecondary) }
                else -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(state.questions, key = { it.id }) { q ->
                        QuestionBankCard(q = q, onEdit = { viewModel.onEditClicked(q) }, onDelete = { viewModel.onDeleteClicked(q.id) })
                    }
                }
            }

            // Delete dialog
            if (state.pendingDeleteId != null) {
                AlertDialog(
                    onDismissRequest = viewModel::onDeleteDismissed,
                    title   = { Text("تأكيد الحذف") },
                    text    = { Text("هل تريد حذف هذا السؤال؟") },
                    confirmButton = { TextButton(onClick = viewModel::onDeleteConfirmed) { Text("حذف", color = AppTheme.color.error) } },
                    dismissButton = { TextButton(onClick = viewModel::onDeleteDismissed) { Text("إلغاء") } },
                )
            }

            if (state.showSheet) QuestionFormSheet(state = state, viewModel = viewModel)
        }
    }
}

@Composable
private fun QuestionBankCard(q: QuestionDto, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = AppTheme.color.bg), elevation = CardDefaults.cardElevation(1.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(q.text, fontSize = 14.sp, color = AppTheme.color.primary, maxLines = 2)
                Spacer(Modifier.height(4.dp))
                Text("${q.type} · ${q.difficulty} · ${q.marks} درجة", fontSize = 11.sp, color = AppTheme.color.textSecondary)
            }
            Row {
                IconButton(onClick = onEdit)   { Icon(Icons.Default.Edit,   null, tint = AppTheme.color.primary, modifier = Modifier.size(20.dp)) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = AppTheme.color.error,   modifier = Modifier.size(20.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuestionFormSheet(state: QuestionsAdminState, viewModel: QuestionsAdminViewModel) {
    val form = state.form
    ModalBottomSheet(onDismissRequest = viewModel::onDismissSheet) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            Text(if (state.isEditing) "تعديل السؤال" else "سؤال جديد", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.primary)
            Spacer(Modifier.height(12.dp))

            // Course
            var courseExp by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = courseExp, onExpandedChange = { courseExp = it }) {
                OutlinedTextField(value = state.courses.find { it.first == form.courseId }?.second ?: "اختر المقرر", onValueChange = {}, readOnly = true, label = { Text("المقرر") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(courseExp) }, modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable))
                ExposedDropdownMenu(expanded = courseExp, onDismissRequest = { courseExp = false }) {
                    state.courses.forEach { (id, name) -> DropdownMenuItem(text = { Text(name) }, onClick = { viewModel.onFormField { copy(courseId = id) }; courseExp = false }) }
                }
            }
            Spacer(Modifier.height(10.dp))

            // Type
            var typeExp by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = typeExp, onExpandedChange = { typeExp = it }) {
                OutlinedTextField(value = form.type, onValueChange = {}, readOnly = true, label = { Text("النوع") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(typeExp) }, modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable))
                ExposedDropdownMenu(expanded = typeExp, onDismissRequest = { typeExp = false }) {
                    QuestionsAdminState.questionTypes.forEach { t -> DropdownMenuItem(text = { Text(t) }, onClick = { viewModel.onFormField { copy(type = t) }; typeExp = false }) }
                }
            }
            Spacer(Modifier.height(10.dp))

            // Difficulty
            var diffExp by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = diffExp, onExpandedChange = { diffExp = it }) {
                OutlinedTextField(value = form.difficulty, onValueChange = {}, readOnly = true, label = { Text("المستوى") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(diffExp) }, modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable))
                ExposedDropdownMenu(expanded = diffExp, onDismissRequest = { diffExp = false }) {
                    QuestionsAdminState.difficulties.forEach { d -> DropdownMenuItem(text = { Text(d) }, onClick = { viewModel.onFormField { copy(difficulty = d) }; diffExp = false }) }
                }
            }
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(value = form.text, onValueChange = { viewModel.onFormField { copy(text = it) } }, label = { Text("نص السؤال") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            Spacer(Modifier.height(10.dp))

            if (form.type == "mcq") {
                OutlinedTextField(value = form.optionA, onValueChange = { viewModel.onFormField { copy(optionA = it) } }, label = { Text("خيار أ") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = form.optionB, onValueChange = { viewModel.onFormField { copy(optionB = it) } }, label = { Text("خيار ب") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = form.optionC, onValueChange = { viewModel.onFormField { copy(optionC = it) } }, label = { Text("خيار ج") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = form.optionD, onValueChange = { viewModel.onFormField { copy(optionD = it) } }, label = { Text("خيار د") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(Modifier.height(8.dp))
            }

            OutlinedTextField(value = form.correctAnswer, onValueChange = { viewModel.onFormField { copy(correctAnswer = it) } }, label = { Text("الإجابة الصحيحة") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = form.marks, onValueChange = { viewModel.onFormField { copy(marks = it) } }, label = { Text("الدرجة") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            if (state.formError != null) { Spacer(Modifier.height(8.dp)); Text(state.formError, color = AppTheme.color.error, fontSize = 13.sp) }
            Spacer(Modifier.height(16.dp))
            Button(onClick = viewModel::onFormSubmit, enabled = !state.isSaving, modifier = Modifier.fillMaxWidth()) {
                if (state.isSaving) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                else Text(if (state.isEditing) "حفظ التعديلات" else "إضافة السؤال")
            }
        }
    }
}
