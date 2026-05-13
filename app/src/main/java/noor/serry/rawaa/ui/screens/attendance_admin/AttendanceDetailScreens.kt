package noor.serry.rawaa.ui.screens.attendance_admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import noor.serry.designsystem.design.AppTheme
import noor.serry.rawaa.data.dto.AttendanceRecordDto
import noor.serry.rawaa.data.dto.AttendanceStatsDto
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.navigation.university_admin.UniversityAdminBackStackProvider
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

// ─────────────────────────────────────────────────────────────────────────────
// Student Attendance Detail
// ─────────────────────────────────────────────────────────────────────────────

data class StudentAttendanceDetailState(
    val records: List<AttendanceRecordDto> = emptyList(),
    val stats: AttendanceStatsDto? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

class StudentAttendanceDetailViewModel(
    private val studentUserId: Int,
    private val repository: UniversityRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(StudentAttendanceDetailState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                // Correct param name: userId (not uid)
                val resp = repository.getStudentAttendanceHistory(
                    userId  = studentUserId,
                    page    = 1,
                    perPage = 100,
                )
                _state.value = StudentAttendanceDetailState(
                    isLoading = false,
                    records   = resp.data,
                    stats     = resp.statistics,
                )
            } catch (e: Exception) {
                _state.value = StudentAttendanceDetailState(isLoading = false, errorMessage = e.message)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentAttendanceDetailScreen(
    studentUserId: Int,
    viewModel: StudentAttendanceDetailViewModel = koinViewModel(parameters = { parametersOf(studentUserId) }),
) {
    val state     by viewModel.state.collectAsStateWithLifecycle()
    val backStack  = UniversityAdminBackStackProvider.current

    Scaffold(
        topBar = {
            TopAppBar(
                title           = { Text("سجل حضور الطالب") },
                navigationIcon  = {
                    IconButton(onClick = { backStack.removeLastOrNull() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppTheme.color.primary)
                }
                state.errorMessage != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.errorMessage!!, color = AppTheme.color.error)
                }
                else -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.stats?.let { stats ->
                        item {
                            AttendanceStatsCard(stats)
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                    items(state.records, key = { it.id }) { record ->
                        AttendanceDetailRow(record)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Course Attendance Detail
// ─────────────────────────────────────────────────────────────────────────────

data class CourseAttendanceDetailState(
    val records: List<AttendanceRecordDto> = emptyList(),
    val stats: AttendanceStatsDto? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

class CourseAttendanceDetailViewModel(
    private val courseId: Int,
    private val repository: UniversityRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CourseAttendanceDetailState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                // getCourseAttendance(courseId, date?) returns ApiResponse<List<AttendanceRecordDto>>
                // — no statistics field; stats is not available from this endpoint
                val resp = repository.getCourseAttendance(courseId = courseId)
                _state.value = CourseAttendanceDetailState(
                    isLoading = false,
                    records   = resp.data ?: emptyList(),
                    stats     = null,
                )
            } catch (e: Exception) {
                _state.value = CourseAttendanceDetailState(isLoading = false, errorMessage = e.message)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseAttendanceDetailScreen(
    courseId: Int,
    viewModel: CourseAttendanceDetailViewModel = koinViewModel(parameters = { parametersOf(courseId) }),
) {
    val state     by viewModel.state.collectAsStateWithLifecycle()
    val backStack  = UniversityAdminBackStackProvider.current

    Scaffold(
        topBar = {
            TopAppBar(
                title           = { Text("حضور المقرر") },
                navigationIcon  = {
                    IconButton(onClick = { backStack.removeLastOrNull() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppTheme.color.primary)
                }
                state.errorMessage != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.errorMessage!!, color = AppTheme.color.error)
                }
                else -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.stats?.let { stats ->
                        item { AttendanceStatsCard(stats); Spacer(Modifier.height(8.dp)) }
                    }
                    items(state.records, key = { it.id }) { record -> AttendanceDetailRow(record) }
                }
            }
        }
    }
}

// ── Shared Composables ────────────────────────────────────────────────────────

@Composable
private fun AttendanceStatsCard(stats: AttendanceStatsDto) {
    Card(
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = AppTheme.color.primary.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            StatItem("حاضر", stats.present.toString())
            StatItem("غائب", stats.absent.toString())
            StatItem("متأخر", stats.late.toString())
            StatItem("نسبة", "${stats.attendanceRate.toInt()}%")
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.primary)
        Text(label, fontSize = 12.sp, color = AppTheme.color.textSecondary)
    }
}

@Composable
private fun AttendanceDetailRow(record: AttendanceRecordDto) {
    Card(
        shape     = RoundedCornerShape(12.dp),
        // 'surface' does not exist in design system — use bgSecondary
        colors    = CardDefaults.cardColors(containerColor = AppTheme.color.bgSecondary),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier  = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Column {
                // 'textPrimary' does not exist in design system — use 'text'
                Text(record.studentName ?: record.userName ?: "—", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = AppTheme.color.text)
                Text(record.date, fontSize = 12.sp, color = AppTheme.color.textSecondary)
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when (record.status) {
                    "present" -> AppTheme.color.primary.copy(alpha = 0.12f)
                    "absent"  -> AppTheme.color.error.copy(alpha = 0.12f)
                    else      -> AppTheme.color.textSecondary.copy(alpha = 0.1f)
                },
            ) {
                Text(
                    text     = record.status,
                    fontSize = 12.sp,
                    color    = when (record.status) {
                        "present" -> AppTheme.color.primary
                        "absent"  -> AppTheme.color.error
                        else      -> AppTheme.color.textSecondary
                    },
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
        }
    }
}
