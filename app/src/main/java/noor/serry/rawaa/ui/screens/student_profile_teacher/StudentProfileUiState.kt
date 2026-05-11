package noor.serry.rawaa.ui.screens.student_profile_teacher

/**
 * Read-only profile state for a student as viewed by a teacher/admin.
 *
 * Data sources:
 *   • GET /api/students/{id}          → StudentDto
 *   • GET /api/students/{id}/courses  → List<StudentCourseDto>
 *
 * No edit fields — the teacher has no endpoint to modify student data.
 */
data class StudentProfileUiState(
    val isLoading: Boolean = true,
    val error: String? = null,

    // ── Identity — StudentDto ─────────────────────────────────────────────────
    val name: String = "",
    val email: String = "",
    val phone: String? = null,                  // StudentDto.phone
    val nationalId: String? = null,             // StudentDto.national_id
    val avatarUrl: String? = null,              // StudentDto.avatar

    // ── Academic — StudentDto ─────────────────────────────────────────────────
    val departmentName: String? = null,         // StudentDto.department_name
    val level: Int? = null,                     // StudentDto.level
    val gpa: String? = null,                    // StudentDto.gpa  (raw server String e.g. "3.75")
    val enrollmentYear: Int? = null,            // StudentDto.enrollment_year
    val currentCreditHours: Int? = null,        // StudentDto.current_credit_hours
    val isActive: Boolean = true,               // StudentDto.is_active == 1

    // ── Status badge (derived from gpa — same logic as StudentsScreen) ────────
    val statusLabel: String = "",
    val statusType: StudentStatusType = StudentStatusType.GOOD,

    // ── Courses — GET /api/students/{id}/courses?status=active ───────────────
    val courses: List<StudentCourseItem> = emptyList(),
    val coursesLoading: Boolean = false,
) {
    /** Display the national ID if available, otherwise fall back to the user id string. */
    val displayId: String get() = nationalId ?: ""
}

data class StudentCourseItem(
    val courseId: Int,                          // StudentCourseDto.course_id
    val courseName: String,                     // StudentCourseDto.course_name
    val code: String,                           // StudentCourseDto.code
    val creditHours: Int,                       // StudentCourseDto.credit_hours
    val semester: String?,                      // StudentCourseDto.semester
    val doctorName: String?,                    // StudentCourseDto.doctor_name
    val status: String?,                        // StudentCourseDto.status  ("active"|"completed"|…)
    val grade: Float?,                          // StudentCourseDto.grade  — null until graded
)

enum class StudentStatusType { EXCELLENT, GOOD, NEEDS_FOLLOW_UP }
