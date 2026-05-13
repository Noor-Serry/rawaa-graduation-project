package noor.serry.rawaa.ui.screens.exams_admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.data.dto.ExamDto
import noor.serry.rawaa.data.dto.QuestionDto
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.navigation.university_admin.UniversityAdminBackStackProvider
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

// ── State ─────────────────────────────────────────────────────────────────────

data class ExamDetailState(
    val exam: ExamDto? = null,
    val questions: List<QuestionDto> = emptyList(),
    val availableQuestions: List<QuestionDto> = emptyList(),   // question bank to add from
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val showAddQuestionsSheet: Boolean = false,
    val selectedQuestionIds: Set<Int> = emptySet(),
    val isProcessing: Boolean = false,
)

// ── ViewModel ─────────────────────────────────────────────────────────────────

class ExamDetailViewModel(
    private val examId: Int,
    private val repository: UniversityRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ExamDetailState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<String>()
    val effects = _effects.asSharedFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            try {
                val examResp = repository.getExam(examId)
                val exam     = examResp.data
                val questions = exam?.questions ?: emptyList()
                _state.value = ExamDetailState(
                    isLoading = false,
                    exam      = exam,
                    questions = questions,
                )
            } catch (e: Exception) {
                _state.value = ExamDetailState(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun onPublish() {
        viewModelScope.launch {
            try {
                repository.publishExam(examId)
                _state.value = _state.value.copy(exam = _state.value.exam?.copy(isPublished = 1))
                _effects.emit("تم نشر الامتحان بنجاح")
            } catch (e: Exception) {
                _effects.emit(e.message ?: "فشل النشر")
            }
        }
    }

    fun onAddQuestionsClicked() {
        viewModelScope.launch {
            try {
                val resp = repository.getQuestions()
                _state.value = _state.value.copy(
                    showAddQuestionsSheet = true,
                    availableQuestions   = resp.data ?: emptyList(),
                    selectedQuestionIds  = emptySet(),
                )
            } catch (e: Exception) {
                _effects.emit(e.message ?: "فشل تحميل الأسئلة")
            }
        }
    }

    fun onQuestionToggled(questionId: Int) {
        val current = _state.value.selectedQuestionIds
        _state.value = _state.value.copy(
            selectedQuestionIds = if (questionId in current) current - questionId else current + questionId
        )
    }

    fun onAddSelectedQuestions() {
        val ids = _state.value.selectedQuestionIds
        if (ids.isEmpty()) return
        _state.value = _state.value.copy(isProcessing = true)
        viewModelScope.launch {
            ids.forEach { qid ->
                try { repository.addQuestionToExam(examId, qid) } catch (_: Exception) {}
            }
            _state.value = _state.value.copy(isProcessing = false, showAddQuestionsSheet = false)
            load()
        }
    }

    fun onRemoveQuestion(questionId: Int) {
        viewModelScope.launch {
            try {
                repository.removeQuestionFromExam(examId, questionId)
                _state.value = _state.value.copy(
                    questions = _state.value.questions.filter { it.id != questionId }
                )
                _effects.emit("تم حذف السؤال")
            } catch (e: Exception) {
                _effects.emit(e.message ?: "فشل الحذف")
            }
        }
    }

    fun onDismissSheet() { _state.value = _state.value.copy(showAddQuestionsSheet = false) }
}

// ── Screen ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamDetailScreen(
    examId: Int,
    viewModel: ExamDetailViewModel = koinViewModel(parameters = { parametersOf(examId) }),
) {
    val state     by viewModel.state.collectAsStateWithLifecycle()
    val backStack  = UniversityAdminBackStackProvider.current

    Scaffold(
        topBar = {
            TopAppBar(
                title          = { Text(state.exam?.title ?: "تفاصيل الامتحان") },
                navigationIcon = {
                    IconButton(onClick = { backStack.removeLastOrNull() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    if (state.exam?.isPublished == 0) {
                        TextButton(onClick = viewModel::onPublish) { Text("نشر") }
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = viewModel::onAddQuestionsClicked,
                containerColor = AppTheme.color.primary,
            ) { Icon(Icons.Default.Add, contentDescription = "إضافة أسئلة") }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppTheme.color.primary)
                }
                state.errorMessage != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.errorMessage!!, color = AppTheme.color.error)
                }
                else -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Exam info header
                    state.exam?.let { exam ->
                        item {
                            ExamInfoCard(exam)
                            Spacer(Modifier.height(8.dp))
                            Text("الأسئلة (${state.questions.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.text)
                        }
                    }

                    if (state.questions.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("لم يُضف أي سؤال بعد", color = AppTheme.color.textSecondary)
                            }
                        }
                    }

                    items(state.questions, key = { it.id }) { question ->
                        QuestionRow(question = question, onRemove = { viewModel.onRemoveQuestion(question.id) })
                    }
                }
            }

            // Add questions sheet
            if (state.showAddQuestionsSheet) {
                AddQuestionsSheet(state = state, viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun ExamInfoCard(exam: ExamDto) {
    Card(
        shape   = RoundedCornerShape(16.dp),
        colors  = CardDefaults.cardColors(containerColor = AppTheme.color.bgSecondary),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(exam.courseName ?: "—", fontSize = 14.sp, color = AppTheme.color.textSecondary)
            Text("${exam.type} · ${exam.totalMarks?.toInt() ?: "—"} درجة · ${exam.durationMin ?: "—"} دقيقة", fontSize = 13.sp, color = AppTheme.color.textSecondary)
            if (exam.startAt != null) Text("${exam.startAt} ← ${exam.endAt ?: ""}", fontSize = 12.sp, color = AppTheme.color.textSecondary)
            Spacer(Modifier.height(4.dp))
            Surface(shape = RoundedCornerShape(8.dp), color = if (exam.isPublished == 1) AppTheme.color.primary.copy(0.1f) else AppTheme.color.textSecondary.copy(0.1f)) {
                Text(
                    text     = if (exam.isPublished == 1) "منشور" else "مسودة",
                    fontSize = 12.sp,
                    color    = if (exam.isPublished == 1) AppTheme.color.primary else AppTheme.color.textSecondary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                )
            }
        }
    }
}

@Composable
private fun QuestionRow(question: QuestionDto, onRemove: () -> Unit) {
    Card(
        shape   = RoundedCornerShape(12.dp),
        colors  = CardDefaults.cardColors(containerColor = AppTheme.color.bgSecondary),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier  = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier            = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(question.text, fontSize = 14.sp, color = AppTheme.color.text, maxLines = 2)
                Spacer(Modifier.height(4.dp))
                Text("${question.type} · ${question.difficulty} · ${question.marks} درجة", fontSize = 11.sp, color = AppTheme.color.textSecondary)
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "حذف", tint = AppTheme.color.error, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddQuestionsSheet(state: ExamDetailState, viewModel: ExamDetailViewModel) {
    ModalBottomSheet(onDismissRequest = viewModel::onDismissSheet) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            Text("اختر أسئلة من البنك", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.text)
            Spacer(Modifier.height(12.dp))

            if (state.availableQuestions.isEmpty()) {
                Text("لا توجد أسئلة في البنك", color = AppTheme.color.textSecondary)
            } else {
                val alreadyAdded = state.questions.map { it.id }.toSet()
                LazyColumn(modifier = Modifier.heightIn(max = 360.dp)) {
                    items(state.availableQuestions.filter { it.id !in alreadyAdded }, key = { it.id }) { q ->
                        Row(
                            modifier            = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment   = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked         = q.id in state.selectedQuestionIds,
                                onCheckedChange = { viewModel.onQuestionToggled(q.id) },
                            )
                            Spacer(Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(q.text, fontSize = 13.sp, color = AppTheme.color.text, maxLines = 2)
                                Text("${q.type} · ${q.difficulty} · ${q.marks} درجة", fontSize = 11.sp, color = AppTheme.color.textSecondary)
                            }
                        }
                        HorizontalDivider(color = AppTheme.color.textSecondary.copy(alpha = 0.1f))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick  = viewModel::onAddSelectedQuestions,
                enabled  = state.selectedQuestionIds.isNotEmpty() && !state.isProcessing,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isProcessing) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                else Text("إضافة ${state.selectedQuestionIds.size} سؤال")
            }
        }
    }
}
