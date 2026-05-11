package noor.serry.rawaa.data.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull

// ─────────────────────────────────────────────────────────────────────────────
// Flexible serializers
//
// The PHP backend is inconsistent:
//   • AuthController::buildAuthResponse() casts is_active with (bool) → JSON true/false
//   • Every other endpoint uses raw SELECT * rows → PDO returns "0"/"1" strings →
//     json_encode turns those into JSON integers 0 / 1
//
// FlexibleBoolSerializer accepts true/false AND 0/1 from JSON and always
// serialises back as a JSON boolean.
//
// FlexibleIntAsBoolSerializer does the same but exposes the value as Int (0/1)
// so existing code that compares == 1 keeps working.
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Deserialises JSON `true`, `false`, `1`, `0` → Kotlin `Boolean`.
 * Used on UserDto.isActive which the login endpoint sends as a real boolean.
 */
import kotlinx.serialization.Serializer


@Serializer(forClass = Boolean::class)
object FlexibleBoolSerializer : KSerializer<Boolean> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("FlexibleBoolean", PrimitiveKind.BOOLEAN)

    override fun deserialize(decoder: Decoder): Boolean {
        val jsonDecoder = decoder as? JsonDecoder
            ?: error("Can be deserialized only by JSON")

        val element = jsonDecoder.decodeJsonElement()

        return when (element) {
            is JsonPrimitive -> {
                when {
                    element.isString -> {
                        when (element.content.lowercase()) {
                            "true", "1" -> true
                            "false", "0" -> false
                            else -> false
                        }
                    }

                    element.booleanOrNull != null -> {
                        element.boolean
                    }

                    element.intOrNull != null -> {
                        element.int == 1
                    }

                    else -> false
                }
            }

            else -> false
        }
    }

    override fun serialize(encoder: Encoder, value: Boolean) {
        encoder.encodeBoolean(value)
    }
}

/**
 * Same as FlexibleBoolSerializer but exposes value as Int (0 or 1).
 * Used on every other is_* field that the API normally sends as 0/1 but
 * might occasionally send as true/false.
 */
object FlexibleIntAsBoolSerializer : KSerializer<Int> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("FlexibleIntAsBool", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Int {
        val jsonDecoder = decoder as? JsonDecoder
            ?: return decoder.decodeInt()
        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonPrimitive -> when {
                element.intOrNull    != null -> element.int
                element.booleanOrNull != null -> if (element.boolean) 1 else 0
                else -> if (element.content.trim().lowercase() !in setOf("0","false","")) 1 else 0
            }
            else -> 0
        }
    }

    override fun serialize(encoder: Encoder, value: Int) = encoder.encodeInt(value)
}

// ─────────────────────────────────────────────────────────────────────────────
// Generic wrappers
// Backend always returns { "success": true/false, "message": "...", "data": ... }
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
)

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

// ─────────────────────────────────────────────────────────────────────────────
// Auth — POST /api/auth/login  |  POST /api/auth/google  |  POST /api/auth/register
// ─────────────────────────────────────────────────────────────────────────────

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

@Serializable
data class AuthResponseDto(
    val token: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int,
    val user: UserDto,
    val university: UniversityRefDto,
)

@Serializable
data class UniversityRefDto(
    val id: Int,
    val name: String,
    val slug: String,
    val logo: String? = null,
)

// GET /api/auth/me  |  embedded in POST /api/auth/login response
//
// IMPORTANT: is_active is sent as JSON boolean true/false by the login endpoint
// (AuthController casts with (bool)) but as integer 0/1 by /auth/me and all
// other endpoints that return raw SELECT * rows.  FlexibleBoolSerializer
// handles both forms transparently.
@Serializable
data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,                         // "student" | "doctor" | "employee" | "admin"
    val avatar: String? = null,
    @SerialName("is_active")
    @Serializable(with = FlexibleBoolSerializer::class)
    val isActive: Boolean = true,
    @SerialName("last_login_at") val lastLoginAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    val profile: UserProfileDto? = null,
)

@Serializable
data class UserProfileDto(
    val id: Int? = null,
    val phone: String? = null,
    @SerialName("national_id") val nationalId: String? = null,
    @SerialName("department_id") val departmentId: Int? = null,
    @SerialName("department_name") val departmentName: String? = null,
    val level: Int? = null,
    val gpa: String? = null,
    @SerialName("enrollment_year") val enrollmentYear: Int? = null,
    @SerialName("role_title") val roleTitle: String? = null,
    val salary: String? = null,
    @SerialName("hire_date") val hireDate: String? = null,
)

// ─────────────────────────────────────────────────────────────────────────────
// Register requests
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class StudentRegisterRequest(
    @SerialName("university_slug") val universitySlug: String,
    val name: String,
    val email: String,
    val password: String,
    val role: String = "student",
    val phone: String? = null,
    @SerialName("national_id") val nationalId: String? = null,
    @SerialName("department_id") val departmentId: Int,
    val level: Int = 1,
    @SerialName("enrollment_year") val enrollmentYear: Int,
)

@Serializable
data class DoctorRegisterRequest(
    @SerialName("university_slug") val universitySlug: String,
    val name: String,
    val email: String,
    val password: String,
    val role: String,                         // "doctor" | "employee"
    val phone: String? = null,
    @SerialName("role_title") val roleTitle: String,
    val salary: Double = 0.0,
    @SerialName("department_id") val departmentId: Int? = null,
)

// ─────────────────────────────────────────────────────────────────────────────
// Department
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class DepartmentDto(
    val id: Int,
    @SerialName("university_id") val universityId: Int? = null,
    val name: String,
    @SerialName("student_count") val studentCount: Int = 0,
    @SerialName("course_count") val courseCount: Int = 0,
    @SerialName("employee_count") val employeeCount: Int = 0,
    @SerialName("created_at") val createdAt: String? = null,
)

// ─────────────────────────────────────────────────────────────────────────────
// Course
// ─────────────────────────────────────────────────────────────────────────────

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
    @SerialName("max_students") val maxStudents: Int = 50,
    // Raw SELECT * → 0/1 integer; FlexibleIntAsBoolSerializer also accepts true/false
    @SerialName("is_active")
    @Serializable(with = FlexibleIntAsBoolSerializer::class)
    val isActive: Int = 1,
    @SerialName("enrolled_count") val enrolledCount: Int? = null,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class CourseRequest(
    val name: String,
    val code: String,
    @SerialName("credit_hours") val creditHours: Int = 3,
    @SerialName("department_id") val departmentId: Int,
    @SerialName("doctor_id") val doctorId: Int? = null,
    val semester: String,
    @SerialName("academic_year") val academicYear: Int,
    @SerialName("max_students") val maxStudents: Int = 50,
)

@Serializable
data class AssignDoctorRequest(
    @SerialName("doctor_id") val doctorId: Int?,
)

// ─────────────────────────────────────────────────────────────────────────────
// Student
// ─────────────────────────────────────────────────────────────────────────────

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
    val gpa: String? = null,
    @SerialName("enrollment_year") val enrollmentYear: Int? = null,
    @SerialName("is_active")
    @Serializable(with = FlexibleIntAsBoolSerializer::class)
    val isActive: Int = 1,
    @SerialName("last_login_at") val lastLoginAt: String? = null,
    @SerialName("current_credit_hours") val currentCreditHours: Int? = null,
    @SerialName("created_at") val createdAt: String? = null,
)

// ─────────────────────────────────────────────────────────────────────────────
// Student Dashboard
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class StudentDashboardDto(
    val student: StudentDto,
    @SerialName("active_courses") val activeCourses: Int = 0,
    @SerialName("credit_hours") val creditHours: Int = 0,
    val gpa: String? = null,
    val courses: List<StudentCourseDto> = emptyList(),
    val attendance: AttendanceStatsDto? = null,
    val schedule: List<ScheduleSessionDto> = emptyList(),
    @SerialName("upcoming_exams") val upcomingExams: List<UpcomingExamDto> = emptyList(),
)

@Serializable
data class StudentCourseDto(
    val id: Int? = null,
    @SerialName("course_id") val courseId: Int,
    @SerialName("course_name") val courseName: String,
    val code: String,
    @SerialName("credit_hours") val creditHours: Int = 3,
    val semester: String? = null,
    @SerialName("doctor_name") val doctorName: String? = null,
    @SerialName("department_name") val departmentName: String? = null,
    val status: String? = null,
    val grade: Float? = null,
    @SerialName("registered_at") val registeredAt: String? = null,
)

// ─────────────────────────────────────────────────────────────────────────────
// Doctor Dashboard
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class DoctorDashboardDto(
    @SerialName("total_courses") val totalCourses: Int = 0,
    @SerialName("total_students") val totalStudents: Int = 0,
    val courses: List<CourseDto> = emptyList(),
    val schedule: List<ScheduleSessionDto> = emptyList(),
    @SerialName("upcoming_exams") val upcomingExams: List<UpcomingExamDto> = emptyList(),
)

// ─────────────────────────────────────────────────────────────────────────────
// Employee
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class EmployeeDto(
    val id: Int,
    @SerialName("user_id") val userId: Int,
    val name: String,
    val email: String,
    val avatar: String? = null,
    val role: String,                         // "doctor" | "employee"
    @SerialName("is_active")
    @Serializable(with = FlexibleIntAsBoolSerializer::class)
    val isActive: Int = 1,
    val phone: String? = null,
    @SerialName("national_id") val nationalId: String? = null,
    @SerialName("role_title") val roleTitle: String,
    val salary: String? = null,
    @SerialName("department_id") val departmentId: Int? = null,
    @SerialName("department_name") val departmentName: String? = null,
    @SerialName("hire_date") val hireDate: String? = null,
    @SerialName("university_id") val universityId: Int? = null,
)

// ─────────────────────────────────────────────────────────────────────────────
// Schedule
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class ScheduleSessionDto(
    val id: Int,
    @SerialName("course_id") val courseId: Int? = null,
    @SerialName("course_name") val courseName: String? = null,
    val code: String? = null,
    @SerialName("credit_hours") val creditHours: Int? = null,
    val day: String? = null,
    @SerialName("start_time") val startTime: String,
    @SerialName("end_time") val endTime: String,
    @SerialName("room_id") val roomId: Int? = null,
    @SerialName("room_name") val roomName: String? = null,
    val type: String = "lecture",
    @SerialName("doctor_name") val doctorName: String? = null,
    val enrolled: Int? = null,
)

@Serializable
data class ScheduleRequest(
    @SerialName("course_id") val courseId: Int,
    @SerialName("room_id") val roomId: Int? = null,
    val day: String,
    @SerialName("start_time") val startTime: String,
    @SerialName("end_time") val endTime: String,
    val type: String = "lecture",
)

// ─────────────────────────────────────────────────────────────────────────────
// Exam
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class ExamDto(
    val id: Int,
    @SerialName("course_id") val courseId: Int? = null,
    @SerialName("course_name") val courseName: String? = null,
    @SerialName("course_code") val courseCode: String? = null,
    val title: String,
    val type: String,                         // "midterm" | "final" | "quiz" | "assignment"
    @SerialName("total_marks") val totalMarks: Float? = null,
    @SerialName("duration_min") val durationMin: Int? = null,
    @SerialName("start_at") val startAt: String? = null,
    @SerialName("end_at") val endAt: String? = null,
    @SerialName("is_published")
    @Serializable(with = FlexibleIntAsBoolSerializer::class)
    val isPublished: Int = 0,
    val questions: List<QuestionDto>? = null,
)

@Serializable
data class UpcomingExamDto(
    val id: Int,
    val title: String,
    val type: String,
    @SerialName("course_name") val courseName: String? = null,
    val code: String? = null,
    @SerialName("start_at") val startAt: String? = null,
    @SerialName("end_at") val endAt: String? = null,
    @SerialName("total_marks") val totalMarks: Float? = null,
)

@Serializable
data class ExamRequest(
    @SerialName("course_id") val courseId: Int,
    val title: String,
    val type: String,
    @SerialName("total_marks") val totalMarks: Float,
    @SerialName("duration_min") val durationMin: Int,
    @SerialName("start_at") val startAt: String,
    @SerialName("end_at") val endAt: String,
)

@Serializable
data class ExamSubmitRequest(
    val answers: List<ExamAnswerRequest>,
)

@Serializable
data class ExamAnswerRequest(
    @SerialName("question_id") val questionId: Int,
    val answer: String,
)

@Serializable
data class ExamSubmitResponseDto(
    @SerialName("submission_id") val submissionId: Int,
    val score: Float,
    @SerialName("total_marks") val totalMarks: Float,
    val note: String? = null,
)

@Serializable
data class ExamSubmissionDto(
    val id: Int,
    @SerialName("exam_id") val examId: Int,
    @SerialName("student_id") val studentId: Int,
    @SerialName("student_name") val studentName: String? = null,
    val score: Float? = null,
    @SerialName("is_graded")
    @Serializable(with = FlexibleIntAsBoolSerializer::class)
    val isGraded: Int = 0,
    @SerialName("created_at") val createdAt: String? = null,
    val answers: List<ExamAnswerResultDto>? = null,
)

@Serializable
data class ExamAnswerResultDto(
    val id: Int,
    @SerialName("question_id") val questionId: Int,
    @SerialName("question_text") val questionText: String? = null,
    val type: String? = null,
    val marks: Float? = null,
    val answer: String,
    @SerialName("is_correct")
    @Serializable(with = FlexibleIntAsBoolSerializer::class)
    val isCorrect: Int? = null,
    @SerialName("marks_awarded") val marksAwarded: Float? = null,
)

// ─────────────────────────────────────────────────────────────────────────────
// Question
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class QuestionDto(
    val id: Int,
    @SerialName("course_id") val courseId: Int? = null,
    @SerialName("course_name") val courseName: String? = null,
    @SerialName("created_by") val createdBy: Int? = null,
    @SerialName("created_by_name") val createdByName: String? = null,
    val type: String,
    val difficulty: String,
    val text: String,
    @SerialName("option_a") val optionA: String? = null,
    @SerialName("option_b") val optionB: String? = null,
    @SerialName("option_c") val optionC: String? = null,
    @SerialName("option_d") val optionD: String? = null,
    @SerialName("correct_answer") val correctAnswer: String? = null,
    val marks: Float = 1f,
    @SerialName("order_no") val orderNo: Int? = null,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class QuestionRequest(
    @SerialName("course_id") val courseId: Int,
    val type: String,
    val difficulty: String,
    val text: String,
    @SerialName("option_a") val optionA: String? = null,
    @SerialName("option_b") val optionB: String? = null,
    @SerialName("option_c") val optionC: String? = null,
    @SerialName("option_d") val optionD: String? = null,
    @SerialName("correct_answer") val correctAnswer: String? = null,
    val marks: Float = 1f,
)

// ─────────────────────────────────────────────────────────────────────────────
// Notification
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class NotificationDto(
    val id: Int,
    @SerialName("user_id") val userId: Int? = null,
    val type: String,
    val title: String,
    val body: String,
    val data: String? = null,
    @SerialName("is_read")
    @Serializable(with = FlexibleIntAsBoolSerializer::class)
    val isRead: Int = 0,
    @SerialName("read_at") val readAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class NotificationsResponseDto(
    val success: Boolean,
    @SerialName("unread_count") val unreadCount: Int = 0,
    val data: List<NotificationDto> = emptyList(),
    val pagination: PaginationDto? = null,
)

// ─────────────────────────────────────────────────────────────────────────────
// Attendance
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class AttendanceRecordDto(
    val id: Int,
    @SerialName("user_id") val userId: Int,
    @SerialName("user_type") val userType: String,
    @SerialName("user_name") val userName: String? = null,
    @SerialName("course_id") val courseId: Int? = null,
    @SerialName("course_name") val courseName: String? = null,
    val date: String,
    val status: String,
    @SerialName("check_in") val checkIn: String? = null,
    @SerialName("check_out") val checkOut: String? = null,
    val notes: String? = null,
    val level: Int? = null,
    @SerialName("student_name") val studentName: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
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

@Serializable
data class AttendanceResponseDto(
    val success: Boolean,
    val data: List<AttendanceRecordDto> = emptyList(),
    val statistics: AttendanceStatsDto? = null,
    val pagination: PaginationDto? = null,
)

@Serializable
data class MarkAttendanceRequest(
    @SerialName("student_user_id") val studentUserId: Int,
    @SerialName("course_id") val courseId: Int,
    val date: String,
    val status: String,
    val notes: String? = null,
)

@Serializable
data class BulkAttendanceRequest(
    @SerialName("course_id") val courseId: Int,
    val date: String,
    val records: List<BulkAttendanceRecord>,
)

@Serializable
data class BulkAttendanceRecord(
    @SerialName("student_user_id") val studentUserId: Int,
    val status: String,
    val notes: String? = null,
)

@Serializable
data class BulkAttendanceResponseDto(
    val saved: Int,
    val errors: List<String> = emptyList(),
)

@Serializable
data class CheckInOutRequest(
    val notes: String? = null,
)

// ─────────────────────────────────────────────────────────────────────────────
// Registration
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class CourseRegistrationRequest(
    @SerialName("student_id") val studentId: Int,
    @SerialName("course_id") val courseId: Int,
)

@Serializable
data class CourseRegistrationResponseDto(
    @SerialName("student_id") val studentId: Int,
    @SerialName("course_id") val courseId: Int,
    @SerialName("course_name") val courseName: String? = null,
    @SerialName("total_credit_hours") val totalCreditHours: Int? = null,
)

@Serializable
data class UpdateGradeRequest(
    @SerialName("student_id") val studentId: Int,
    @SerialName("course_id") val courseId: Int,
    val grade: Float,
)

@Serializable
data class CourseStudentDto(
    val id: Int,
    @SerialName("student_id") val studentId: Int,
    @SerialName("course_id") val courseId: Int,
    val name: String,
    val email: String,
    val level: Int? = null,
    val gpa: String? = null,
    val status: String? = null,
    val grade: Float? = null,
    @SerialName("registered_at") val registeredAt: String? = null,
)

// ─────────────────────────────────────────────────────────────────────────────
// Admin Dashboard
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class AdminDashboardDto(
    val university: AdminUniversityRefDto,
    val stats: AdminStatsDto,
    @SerialName("monthly_registrations") val monthlyRegistrations: List<MonthlyRegistrationDto> = emptyList(),
    @SerialName("gpa_distribution") val gpaDistribution: GpaDistributionDto? = null,
    @SerialName("recent_registrations") val recentRegistrations: List<RecentRegistrationDto> = emptyList(),
)

@Serializable
data class AdminUniversityRefDto(
    val name: String,
    val plan: String,
    val logo: String? = null,
)

@Serializable
data class AdminStatsDto(
    @SerialName("total_students") val totalStudents: Int = 0,
    @SerialName("total_doctors") val totalDoctors: Int = 0,
    @SerialName("total_employees") val totalEmployees: Int = 0,
    @SerialName("total_departments") val totalDepartments: Int = 0,
    @SerialName("active_courses") val activeCourses: Int = 0,
    @SerialName("active_registrations") val activeRegistrations: Int = 0,
    @SerialName("upcoming_exams") val upcomingExams: Int = 0,
)

@Serializable
data class MonthlyRegistrationDto(
    val month: String,
    val count: Int,
)

@Serializable
data class GpaDistributionDto(
    val excellent: Int = 0,
    @SerialName("very_good") val veryGood: Int = 0,
    val good: Int = 0,
    val pass: Int = 0,
)

@Serializable
data class RecentRegistrationDto(
    @SerialName("registered_at") val registeredAt: String,
    @SerialName("student_name") val studentName: String,
    @SerialName("course_name") val courseName: String,
    val code: String,
)

// ─────────────────────────────────────────────────────────────────────────────
// Admin Reports
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class AttendanceReportRowDto(
    @SerialName("student_name") val studentName: String,
    val level: Int? = null,
    val dept: String? = null,
    @SerialName("total_sessions") val totalSessions: Int = 0,
    val present: Int = 0,
    val absent: Int = 0,
    val rate: Float = 0f,
)

@Serializable
data class GradesReportRowDto(
    @SerialName("course_name") val courseName: String,
    val code: String,
    val enrolled: Int = 0,
    @SerialName("avg_grade") val avgGrade: Float? = null,
    @SerialName("max_grade") val maxGrade: Float? = null,
    @SerialName("min_grade") val minGrade: Float? = null,
    val passed: Int = 0,
    val failed: Int = 0,
)

// ─────────────────────────────────────────────────────────────────────────────
// SuperAdmin
// ─────────────────────────────────────────────────────────────────────────────

@Serializable
data class SuperAdminLoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class SuperAdminAuthResponseDto(
    val token: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int,
    val admin: SuperAdminDto,
)

@Serializable
data class SuperAdminDto(
    val id: Int,
    val name: String,
    val email: String,
)

@Serializable
data class PlatformStatsDto(
    @SerialName("total_universities") val totalUniversities: Int = 0,
    @SerialName("active_universities") val activeUniversities: Int = 0,
    @SerialName("total_students") val totalStudents: Int = 0,
    @SerialName("total_doctors") val totalDoctors: Int = 0,
    @SerialName("total_employees") val totalEmployees: Int = 0,
    @SerialName("plan_trial") val planTrial: Int = 0,
    @SerialName("plan_basic") val planBasic: Int = 0,
    @SerialName("plan_pro") val planPro: Int = 0,
    @SerialName("plan_enterprise") val planEnterprise: Int = 0,
)

@Serializable
data class UniversityDto(
    val id: Int,
    val name: String,
    @SerialName("name_en") val nameEn: String? = null,
    val slug: String,
    val logo: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val country: String? = null,
    val plan: String? = null,
    @SerialName("plan_expires_at") val planExpiresAt: String? = null,
    @SerialName("max_students") val maxStudents: Int = 0,
    @SerialName("max_staff") val maxStaff: Int = 0,
    // Raw SELECT * → 0/1; SuperAdminController does not cast to bool
    @SerialName("is_active")
    @Serializable(with = FlexibleIntAsBoolSerializer::class)
    val isActive: Int = 1,
    @SerialName("created_at") val createdAt: String? = null,
    val stats: UniversityStatsDto? = null,
    @SerialName("student_count") val studentCount: Int? = null,
    @SerialName("doctor_count") val doctorCount: Int? = null,
    @SerialName("employee_count") val employeeCount: Int? = null,
    @SerialName("department_count") val departmentCount: Int? = null,
    @SerialName("course_count") val courseCount: Int? = null,
)

@Serializable
data class UniversityStatsDto(
    @SerialName("total_students") val totalStudents: Int = 0,
    @SerialName("total_doctors") val totalDoctors: Int = 0,
    @SerialName("total_employees") val totalEmployees: Int = 0,
    @SerialName("total_departments") val totalDepartments: Int = 0,
    @SerialName("active_courses") val activeCourses: Int = 0,
    @SerialName("active_registrations") val activeRegistrations: Int = 0,
)

@Serializable
data class CreateUniversityRequest(
    val name: String,
    @SerialName("name_en") val nameEn: String? = null,
    val slug: String,
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val country: String? = null,
    val plan: String = "trial",
    @SerialName("plan_expires_at") val planExpiresAt: String? = null,
    @SerialName("max_students") val maxStudents: Int = 500,
    @SerialName("max_staff") val maxStaff: Int = 50,
    @SerialName("admin_name") val adminName: String,
    @SerialName("admin_email") val adminEmail: String,
    @SerialName("admin_password") val adminPassword: String,
)

@Serializable
data class ChangePlanRequest(
    val plan: String,
    @SerialName("plan_expires_at") val planExpiresAt: String? = null,
    @SerialName("max_students") val maxStudents: Int? = null,
    @SerialName("max_staff") val maxStaff: Int? = null,
)

// GET /api/super/universities/{id}/admins — raw SELECT row, is_active is Int
@Serializable
data class UniversityAdminDto(
    val id: Int,
    val name: String,
    val email: String,
    @SerialName("is_active")
    @Serializable(with = FlexibleIntAsBoolSerializer::class)
    val isActive: Int = 1,
    @SerialName("last_login_at") val lastLoginAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
)