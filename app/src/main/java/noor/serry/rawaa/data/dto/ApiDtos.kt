package noor.serry.rawaa.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ─── Generic wrapper ─────────────────────────────────────────────────────────
// Backend always returns { "success": true/false, "message": "...", "data": ... }

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
)

// Paginated list responses have a top-level "pagination" sibling to "data"
@Serializable
data class PaginatedResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: List<T> = emptyList(),
    val pagination: PaginationDto? = null,
)

@Serializable
data class PaginationDto(
    val total: Int,
    @SerialName("per_page") val perPage: Int,
    @SerialName("current_page") val currentPage: Int,
    @SerialName("last_page") val lastPage: Int,
)

// ─── Auth ─────────────────────────────────────────────────────────────────────

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
    @SerialName("university_slug") val universitySlug: String,
)

@Serializable
data class GoogleLoginRequest(
    @SerialName("id_token") val idToken: String,
    @SerialName("university_slug") val universitySlug: String,
    val role: String = "student",
    @SerialName("department_id") val departmentId: Int? = null,
)

// POST /api/auth/login  →  data: { token, token_type, expires_in, user, university }
@Serializable
data class AuthResponseDto(
    val token: String,
    @SerialName("token_type") val tokenType: String = "Bearer",
    @SerialName("expires_in") val expiresIn: Int = 86400,
    val user: UserDto,
    val university: UniversityRefDto? = null,
)

@Serializable
data class UniversityRefDto(
    val id: Int,
    val name: String,
    val slug: String,
    val logo: String? = null,
)

// GET /api/auth/me  →  data: { id, name, email, role, avatar, is_active, profile }
@Serializable
data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val avatar: String? = null,
    @SerialName("is_active") val isActive: Int = 1,
    // nested profile — present for student / doctor / employee roles
    val profile: UserProfileDto? = null,
)

// The "profile" object differs slightly by role but shares common fields
@Serializable
data class UserProfileDto(
    val id: Int? = null,
    val phone: String? = null,
    @SerialName("national_id") val nationalId: String? = null,
    @SerialName("department_id") val departmentId: Int? = null,
    @SerialName("department_name") val departmentName: String? = null,
    // student-specific
    val level: Int? = null,
    val gpa: String? = null,                 // backend returns String e.g. "3.50"
    @SerialName("enrollment_year") val enrollmentYear: Int? = null,
    // employee / doctor specific
    @SerialName("role_title") val roleTitle: String? = null,
    val salary: String? = null,
    @SerialName("hire_date") val hireDate: String? = null,
)

// ─── Dashboard — Student ───────────────────────────────────────────────────────
// GET /api/student/dashboard

@Serializable
data class StudentDashboardDto(
    val student: StudentSummaryDto,
    @SerialName("active_courses") val activeCourses: Int = 0,
    @SerialName("credit_hours") val creditHours: Int = 0,
    val courses: List<StudentCourseDto> = emptyList(),
    val attendance: AttendanceStatsDto? = null,
    val schedule: List<ScheduleSessionDto> = emptyList(),
    @SerialName("upcoming_exams") val upcomingExams: List<ExamDto> = emptyList(),
)

@Serializable
data class StudentSummaryDto(
    val id: Int,
    val name: String,
    val level: Int? = null,
    val gpa: String? = null,                 // "3.50"
    @SerialName("department_name") val departmentName: String? = null,
)

@Serializable
data class AttendanceStatsDto(
    val present: Int = 0,
    val absent: Int = 0,
    val late: Int = 0,
    val excused: Int = 0,
    val total: Int = 0,
    @SerialName("attendance_rate") val attendanceRate: Float = 0f,
)

// Course as it appears inside the student dashboard courses list
@Serializable
data class StudentCourseDto(
    @SerialName("course_id") val courseId: Int,
    @SerialName("course_name") val courseName: String,
    val code: String,
    @SerialName("credit_hours") val creditHours: Int = 3,
    @SerialName("doctor_name") val doctorName: String? = null,
    val status: String? = null,          // "active" / "completed" / "dropped"
    val grade: Float? = null,
)

// ─── Dashboard — Doctor ────────────────────────────────────────────────────────
// GET /api/doctor/dashboard

@Serializable
data class DoctorDashboardDto(
    @SerialName("total_courses") val totalCourses: Int = 0,
    @SerialName("total_students") val totalStudents: Int = 0,
    val courses: List<CourseDto> = emptyList(),
    val schedule: List<ScheduleSessionDto> = emptyList(),
    @SerialName("upcoming_exams") val upcomingExams: List<ExamDto> = emptyList(),
)

// ─── Course ───────────────────────────────────────────────────────────────────
// GET /api/courses  and  /api/doctor/dashboard courses list

@Serializable
data class CourseDto(
    val id: Int,
    val name: String,
    val code: String,
    @SerialName("credit_hours") val creditHours: Int = 3,
    @SerialName("department_id") val departmentId: Int? = null,
    @SerialName("department_name") val departmentName: String? = null,
    @SerialName("doctor_id") val doctorId: Int? = null,
    @SerialName("doctor_name") val doctorName: String? = null,
    val semester: String? = null,
    @SerialName("academic_year") val academicYear: Int? = null,
    @SerialName("max_students") val maxStudents: Int = 0,
    @SerialName("is_active") val isActive: Int = 1,
    // doctor-dashboard variant has enrolled_count instead of a student count
    @SerialName("enrolled_count") val enrolledCount: Int? = null,
)

// ─── Exam ─────────────────────────────────────────────────────────────────────

@Serializable
data class ExamDto(
    val id: Int,
    val title: String,
    val type: String,                        // "midterm" / "final" / "quiz"
    @SerialName("course_name") val courseName: String? = null,
    @SerialName("start_at") val startAt: String? = null,
    @SerialName("end_at") val endAt: String? = null,
    @SerialName("total_marks") val totalMarks: Float? = null,
    @SerialName("duration_min") val durationMin: Int? = null,
    @SerialName("is_published") val isPublished: Int = 0,
)

// ─── Notification ─────────────────────────────────────────────────────────────
// GET /api/notifications

@Serializable
data class NotificationDto(
    val id: Int,
    val type: String,
    val title: String,
    val body: String,
    @SerialName("is_read") val isRead: Int = 0,         // 0 or 1 from backend
    @SerialName("read_at") val readAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
)

// The notifications list response also carries unread_count at top level;
// use a dedicated wrapper for that endpoint.
@Serializable
data class NotificationsResponseDto(
    val success: Boolean,
    @SerialName("unread_count") val unreadCount: Int = 0,
    val data: List<NotificationDto> = emptyList(),
    val pagination: PaginationDto? = null,
)

// ─── Schedule ─────────────────────────────────────────────────────────────────
// GET /api/schedules/my  →  data: [ { id, course_name, code, day, start_time, end_time, room_name, building, type, doctor_name } ]

@Serializable
data class ScheduleSessionDto(
    val id: Int,
    @SerialName("course_name") val courseName: String? = null,
    val code: String? = null,
    val day: String? = null,                 // "sunday" … "saturday"
    @SerialName("start_time") val startTime: String,
    @SerialName("end_time") val endTime: String,
    @SerialName("room_name") val roomName: String? = null,
    val building: String? = null,
    val type: String = "lecture",            // "lecture" / "lab" / "tutorial"
    @SerialName("doctor_name") val doctorName: String? = null,
)

// ─── Student ──────────────────────────────────────────────────────────────────
// GET /api/students  →  data: [ { id, user_id, name, email, avatar, phone, national_id,
//                                  department_id, department_name, level, gpa, enrollment_year, is_active } ]

@Serializable
data class StudentDto(
    val id: Int,
    @SerialName("user_id") val userId: Int? = null,
    val name: String,
    val email: String,
    val avatar: String? = null,
    val phone: String? = null,
    @SerialName("national_id") val nationalId: String? = null,
    @SerialName("department_id") val departmentId: Int? = null,
    @SerialName("department_name") val departmentName: String? = null,
    val level: Int? = null,
    val gpa: String? = null,               // "3.50"
    @SerialName("enrollment_year") val enrollmentYear: Int? = null,
    @SerialName("is_active") val isActive: Int = 1,
)


@Serializable
data class DepartmentDto(
    val id: Int,
    val name: String,
    @SerialName("university_slug") val universitySlug: String? = null,
)
// ─── Profile helpers (reuse UserDto + UserProfileDto from /api/auth/me) ───────
// No separate profile DTOs needed — ProfileRepositoryImpl maps UserDto directly.
