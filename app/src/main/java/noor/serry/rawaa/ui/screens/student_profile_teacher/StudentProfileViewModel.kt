package noor.serry.rawaa.ui.screens.student_profile_teacher

import noor.serry.rawaa.data.dto.StudentCourseDto
import noor.serry.rawaa.data.dto.StudentDto
import noor.serry.rawaa.data.repository.UniversityRepository
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class StudentProfileViewModel(
    private val studentId: Int,
    private val repository: UniversityRepository,
    private val dispatchers: DispatcherProvider,
) : BaseViewModel<StudentProfileUiState, StudentProfileEffect>(
    initialState = StudentProfileUiState(isLoading = true),
    dispatcherProvider = dispatchers,
) {

    init { load() }

    fun load() {
        updateState { it.copy(isLoading = true, error = null) }
        tryToExecute(
            action = {
                // 1. Fetch student record — GET /api/students/{id}
                val student = repository.getStudent(studentId).data

                // 2. Fetch active courses — GET /api/students/{id}/courses?status=active
                val courses = repository.getStudentCourses(studentId, status = "active").data
                    ?: emptyList()

                Pair(student, courses)
            },
            onSuccess = { (student, courses) ->
                if (student != null) {
                    updateState { student.toStudentProfileUiState(courses) }
                } else {
                    updateState { it.copy(isLoading = false, error = "لم يتم العثور على الطالب") }
                }
            },
            onError = { e ->
                updateState { it.copy(isLoading = false, error = e.message) }
                sendNewEffect(StudentProfileEffect.ShowError(e.message ?: "حدث خطأ أثناء التحميل"))
            },
            dispatcher = dispatchers.IO,
        )
    }

    fun onBackClick() {
        sendNewEffect(StudentProfileEffect.NavigateBack)
    }
}

// ── Mapper ────────────────────────────────────────────────────────────────────

private fun StudentDto.toStudentProfileUiState(
    courses: List<StudentCourseDto>,
): StudentProfileUiState {
    val gpaFloat = gpa?.toFloatOrNull() ?: 0f
    val statusType = when {
        gpaFloat >= 3.5f -> StudentStatusType.EXCELLENT
        gpaFloat >= 2.0f -> StudentStatusType.GOOD
        else             -> StudentStatusType.NEEDS_FOLLOW_UP
    }
    val statusLabel = when (statusType) {
        StudentStatusType.EXCELLENT       -> "ممتاز"
        StudentStatusType.GOOD            -> "جيد"
        StudentStatusType.NEEDS_FOLLOW_UP -> "يحتاج متابعة"
    }
    return StudentProfileUiState(
        isLoading          = false,
        name               = name,
        email              = email,
        phone              = phone,
        nationalId         = nationalId,
        avatarUrl          = avatar,
        departmentName     = departmentName,
        level              = level,
        gpa                = gpa,
        enrollmentYear     = enrollmentYear,
        currentCreditHours = currentCreditHours,
        isActive           = isActive == 1,
        statusLabel        = statusLabel,
        statusType         = statusType,
        courses            = courses.map { it.toCourseItem() },
    )
}

private fun StudentCourseDto.toCourseItem() = StudentCourseItem(
    courseId    = courseId,
    courseName  = courseName,
    code        = code,
    creditHours = creditHours,
    semester    = semester,
    doctorName  = doctorName,
    status      = status,
    grade       = grade,
)
