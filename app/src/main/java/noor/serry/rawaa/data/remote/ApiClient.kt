package noor.serry.rawaa.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import noor.serry.rawaa.data.dto.*
import noor.serry.rawaa.data.local.TokenDataStore

/**
 * Thin wrapper around [HttpClient] that covers every endpoint used by the app.
 *
 * All shapes match the API reference exactly:
 *   Base URL : https://abdallah-elrefai.com/university-api-v3
 *   Auth     : Bearer token injected via [TokenDataStore]
 */
class ApiClient(
    private val client: HttpClient,
    private val tokenStore: TokenDataStore,
    private val baseUrl: String,
) {
    suspend fun getDepartments(): ApiResponse<List<DepartmentDto>> =
        client.get("$baseUrl/api/departments") {
            contentType(ContentType.Application.Json)
        }.body()
    // ── helpers ───────────────────────────────────────────────────────────────

    private suspend fun token(): String = tokenStore.getToken()
        ?: error("No auth token stored. User must log in first.")

    private fun bearer(token: String) = "Bearer $token"

    // ── Auth ──────────────────────────────────────────────────────────────────

    /** POST /api/auth/login */
    suspend fun login(request: LoginRequest): ApiResponse<AuthResponseDto> =
        client.post("$baseUrl/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    /** POST /api/auth/google */
    suspend fun loginWithGoogle(request: GoogleLoginRequest): ApiResponse<AuthResponseDto> =
        client.post("$baseUrl/api/auth/google") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    /**
     * POST /api/auth/register — student variant.
     *
     * No Authorization header — this is an unauthenticated endpoint.
     * On success the server returns 201 with the same AuthResponseDto shape as login.
     */
    suspend fun registerStudent(request: StudentRegisterRequest): ApiResponse<AuthResponseDto> =
        client.post("$baseUrl/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    /**
     * POST /api/auth/register — doctor variant.
     *
     * No Authorization header — this is an unauthenticated endpoint.
     * On success the server returns 201 with the same AuthResponseDto shape as login.
     */
    suspend fun registerDoctor(request: DoctorRegisterRequest): ApiResponse<AuthResponseDto> =
        client.post("$baseUrl/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    /** POST /api/auth/logout */
    suspend fun logout(): ApiResponse<Unit> {
        val t = token()
        return client.post("$baseUrl/api/auth/logout") {
            header("Authorization", bearer(t))
        }.body()
    }

    /**
     * GET /api/auth/me
     * Returns the authenticated user + their role-specific profile nested under "profile".
     */
    suspend fun getMe(): ApiResponse<UserDto> {
        val t = token()
        return client.get("$baseUrl/api/auth/me") {
            header("Authorization", bearer(t))
        }.body()
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────

    /** GET /api/student/dashboard */
    suspend fun getStudentDashboard(): ApiResponse<StudentDashboardDto> {
        val t = token()
        return client.get("$baseUrl/api/student/dashboard") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** GET /api/doctor/dashboard */
    suspend fun getDoctorDashboard(): ApiResponse<DoctorDashboardDto> {
        val t = token()
        return client.get("$baseUrl/api/doctor/dashboard") {
            header("Authorization", bearer(t))
        }.body()
    }

    // ── Courses ───────────────────────────────────────────────────────────────

    /**
     * GET /api/courses
     * Supports optional filtering: department_id, doctor_id, semester, is_active, search.
     */
    suspend fun getAllCourses(
        page: Int = 1,
        perPage: Int = 100,
        isActive: Int = 1,
    ): PaginatedResponse<CourseDto> {
        val t = token()
        return client.get("$baseUrl/api/courses?page=$page&per_page=$perPage&is_active=$isActive") {
            header("Authorization", bearer(t))
        }.body()
    }

    /**
     * GET /api/students/{id}/courses
     * Returns the courses a specific student is enrolled in.
     * status: active / dropped / completed
     */
    suspend fun getStudentCourses(
        studentId: Int,
        status: String = "active",
    ): ApiResponse<List<StudentCourseDto>> {
        val t = token()
        return client.get("$baseUrl/api/students/$studentId/courses?status=$status") {
            header("Authorization", bearer(t))
        }.body()
    }

    // ── Registrations ─────────────────────────────────────────────────────────

    /** POST /api/registrations — enrol a student in a course */
    suspend fun registerCourse(studentId: Int, courseId: Int): ApiResponse<Unit> {
        val t = token()
        return client.post("$baseUrl/api/registrations") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(mapOf("student_id" to studentId, "course_id" to courseId))
        }.body()
    }

    /** DELETE /api/registrations/drop — drop (un-enrol) a student from a course */
    suspend fun dropCourse(studentId: Int, courseId: Int): ApiResponse<Unit> {
        val t = token()
        return client.delete("$baseUrl/api/registrations/drop") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(mapOf("student_id" to studentId, "course_id" to courseId))
        }.body()
    }

    /** PUT /api/registrations/grade — record a grade for a student in a course */
    suspend fun updateGrade(studentId: Int, courseId: Int, grade: Float): ApiResponse<Unit> {
        val t = token()
        return client.put("$baseUrl/api/registrations/grade") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(mapOf("student_id" to studentId, "course_id" to courseId, "grade" to grade))
        }.body()
    }

    // ── Notifications ─────────────────────────────────────────────────────────

    /**
     * GET /api/notifications
     * Response shape is unique: { success, unread_count, data: [...], pagination: {...} }
     */
    suspend fun getNotifications(
        page: Int = 1,
        perPage: Int = 50,
    ): NotificationsResponseDto {
        val t = token()
        return client.get("$baseUrl/api/notifications?page=$page&per_page=$perPage") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** PUT /api/notifications/{id}/read */
    suspend fun markNotificationAsRead(id: Int): ApiResponse<Unit> {
        val t = token()
        return client.put("$baseUrl/api/notifications/$id/read") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** PUT /api/notifications/read-all */
    suspend fun markAllNotificationsAsRead(): ApiResponse<Unit> {
        val t = token()
        return client.put("$baseUrl/api/notifications/read-all") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** DELETE /api/notifications/{id} */
    suspend fun deleteNotification(id: Int): ApiResponse<Unit> {
        val t = token()
        return client.delete("$baseUrl/api/notifications/$id") {
            header("Authorization", bearer(t))
        }.body()
    }

    // ── Schedule ──────────────────────────────────────────────────────────────

    /** GET /api/schedules/my — weekly schedule for the authenticated student or doctor */
    suspend fun getMySchedule(): ApiResponse<List<ScheduleSessionDto>> {
        val t = token()
        return client.get("$baseUrl/api/schedules/my") {
            header("Authorization", bearer(t))
        }.body()
    }

    // ── Students ──────────────────────────────────────────────────────────────

    /** GET /api/students — paginated list (admin / doctor / employee) */
    suspend fun getStudents(
        page: Int = 1,
        perPage: Int = 100,
        departmentId: Int? = null,
        level: Int? = null,
        search: String? = null,
    ): PaginatedResponse<StudentDto> {
        val t = token()
        val query = buildString {
            append("page=$page&per_page=$perPage")
            if (departmentId != null) append("&department_id=$departmentId")
            if (level != null)        append("&level=$level")
            if (search != null)       append("&search=$search")
        }
        return client.get("$baseUrl/api/students?$query") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** GET /api/courses/{id}/students — students enrolled in a specific course */
    suspend fun getCourseStudents(courseId: Int): PaginatedResponse<StudentDto> {
        val t = token()
        return client.get("$baseUrl/api/courses/$courseId/students") {
            header("Authorization", bearer(t))
        }.body()
    }

    // ── Attendance ────────────────────────────────────────────────────────────

    /**
     * POST /api/attendance/students/bulk
     * Records attendance for a whole class in one call.
     */
    suspend fun bulkMarkAttendance(
        courseId: Int,
        date: String,
        records: List<Map<String, Any>>,
    ): ApiResponse<Unit> {
        val t = token()
        return client.post("$baseUrl/api/attendance/students/bulk") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(mapOf("course_id" to courseId, "date" to date, "records" to records))
        }.body()
    }

    /**
     * GET /api/attendance/students/{userId}
     * Returns the attendance history + statistics for a specific student.
     */
    suspend fun getStudentAttendance(
        userId: Int,
        courseId: Int? = null,
        page: Int = 1,
    ): ApiResponse<List<AttendanceRecordDto>> {
        val t = token()
        val query = buildString {
            append("page=$page")
            if (courseId != null) append("&course_id=$courseId")
        }
        return client.get("$baseUrl/api/attendance/students/$userId?$query") {
            header("Authorization", bearer(t))
        }.body()
    }

    // ── Exams ─────────────────────────────────────────────────────────────────

    /** GET /api/exams — list of exams (optionally filtered by course_id / is_published) */
    suspend fun getExams(
        courseId: Int? = null,
        isPublished: Int? = null,
        page: Int = 1,
    ): PaginatedResponse<ExamDto> {
        val t = token()
        val query = buildString {
            append("page=$page")
            if (courseId != null)    append("&course_id=$courseId")
            if (isPublished != null) append("&is_published=$isPublished")
        }
        return client.get("$baseUrl/api/exams?$query") {
            header("Authorization", bearer(t))
        }.body()
    }
}

// ─── Attendance record DTO (not in the shared file to keep it self-contained) ─

@kotlinx.serialization.Serializable
data class AttendanceRecordDto(
    val id: Int,
    @kotlinx.serialization.SerialName("course_name") val courseName: String? = null,
    val date: String,
    val status: String,   // present / absent / late / excused
    val notes: String? = null,
)
