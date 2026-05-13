package noor.serry.rawaa.data.repository

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
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import noor.serry.rawaa.data.dto.*
import noor.serry.rawaa.data.local.TokenDataStore

/**
 * Single repository that exposes every endpoint defined in api.php.
 *
 * Endpoints are grouped to mirror the backend controllers:
 *   • SuperAdmin      – /api/super/...
 *   • Auth            – /api/auth/...
 *   • Admin           – /api/admin/...
 *   • Students        – /api/students/...  + /api/student/dashboard
 *   • Employees       – /api/employees/... + /api/doctor/dashboard
 *   • Departments     – /api/departments/...
 *   • Courses         – /api/courses/...
 *   • Registrations   – /api/registrations/...
 *   • Attendance      – /api/attendance/...
 *   • Schedules       – /api/schedules/...
 *   • Questions       – /api/questions/...
 *   • Exams           – /api/exams/...
 *   • Notifications   – /api/notifications/...
 */
class UniversityRepository(
    private val client: HttpClient,
    private val tokenStore: TokenDataStore,
    private val baseUrl: String,
) {

    // ─────────────────────────────────────────────────────────────────────────
    // Internal helpers
    // ─────────────────────────────────────────────────────────────────────────

    private suspend fun token(): String =
        tokenStore.getToken() ?: error("No auth token – user must log in first.")

    private fun bearer(t: String) = "Bearer $t"

    // ─────────────────────────────────────────────────────────────────────────
    // SuperAdmin  (/api/super/...)
    // ─────────────────────────────────────────────────────────────────────────

    /** POST /api/super/login */
    suspend fun superAdminLogin(
        email: String,
        password: String,
    ): ApiResponse<SuperAdminAuthResponseDto> =
        client.post("$baseUrl/api/super/login") {
            contentType(ContentType.Application.Json)
            setBody(SuperAdminLoginRequest(email, password))
        }.body()

    /** GET /api/super/stats */
    suspend fun getSuperAdminStats(): ApiResponse<PlatformStatsDto> {
        val t = token()
        return client.get("$baseUrl/api/super/stats") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** GET /api/super/universities?page=&per_page=&plan=&is_active=&search= */
    suspend fun getSuperAdminUniversities(
        page: Int = 1,
        perPage: Int = 20,
        plan: String? = null,
        isActive: Int? = null,
        search: String? = null,
    ): PaginatedResponse<UniversityDto> {
        val t = token()
        val q = buildQuery(
            "page" to page,
            "per_page" to perPage,
            "plan" to plan,
            "is_active" to isActive,
            "search" to search,
        )
        return client.get("$baseUrl/api/super/universities?$q") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** GET /api/super/universities/{id} */
    suspend fun getSuperAdminUniversity(id: Int): ApiResponse<UniversityDto> {
        val t = token()
        return client.get("$baseUrl/api/super/universities/$id") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** POST /api/super/universities */
    suspend fun createUniversity(
        request: CreateUniversityRequest,
    ): ApiResponse<UniversityDto> {
        val t = token()
        return client.post("$baseUrl/api/super/universities") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /** PUT /api/super/universities/{id} */
    suspend fun updateUniversity(
        id: Int,
        request: CreateUniversityRequest,
    ): ApiResponse<UniversityDto> {
        val t = token()
        return client.put("$baseUrl/api/super/universities/$id") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /** PUT /api/super/universities/{id}/activate */
    suspend fun activateUniversity(id: Int): ApiResponse<Unit> {
        val t = token()
        return client.put("$baseUrl/api/super/universities/$id/activate") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** PUT /api/super/universities/{id}/deactivate */
    suspend fun deactivateUniversity(id: Int): ApiResponse<Unit> {
        val t = token()
        return client.put("$baseUrl/api/super/universities/$id/deactivate") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** PUT /api/super/universities/{id}/plan */
    suspend fun changeUniversityPlan(
        id: Int,
        request: ChangePlanRequest,
    ): ApiResponse<Unit> {
        val t = token()
        return client.put("$baseUrl/api/super/universities/$id/plan") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /** GET /api/super/universities/{id}/admins */
    suspend fun getUniversityAdmins(id: Int): ApiResponse<List<UniversityAdminDto>> {
        val t = token()
        return client.get("$baseUrl/api/super/universities/$id/admins") {
            header("Authorization", bearer(t))
        }.body()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Auth  (/api/auth/...)
    // ─────────────────────────────────────────────────────────────────────────

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

    /** POST /api/auth/register  (student) */
    suspend fun registerStudent(request: StudentRegisterRequest): ApiResponse<AuthResponseDto> =
        client.post("$baseUrl/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    /** POST /api/auth/register  (doctor / employee) */
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

    /** GET /api/auth/me */
    suspend fun getMe(): ApiResponse<UserDto> {
        val t = token()
        return client.get("$baseUrl/api/auth/me") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** PUT /api/auth/change-password */
    suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        newPasswordConfirmation: String,
    ): ApiResponse<Unit> {
        val t = token()
        return client.put("$baseUrl/api/auth/change-password") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "current_password"          to currentPassword,
                    "new_password"              to newPassword,
                    "new_password_confirmation" to newPasswordConfirmation,
                )
            )
        }.body()
    }

    /** PUT /api/auth/profile  (name / avatar) */
    suspend fun updateProfile(
        name: String? = null,
        avatar: String? = null,
    ): ApiResponse<UserDto> {
        val t = token()
        val body = buildMap<String, String> {
            if (name   != null) put("name",   name)
            if (avatar != null) put("avatar", avatar)
        }
        return client.put("$baseUrl/api/auth/profile") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Admin Dashboard  (/api/admin/...)
    // ─────────────────────────────────────────────────────────────────────────

    /** GET /api/admin/dashboard */
    suspend fun getAdminDashboard(): ApiResponse<AdminDashboardDto> {
        val t = token()
        return client.get("$baseUrl/api/admin/dashboard") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** GET /api/admin/reports/attendance */
    suspend fun getAttendanceReport(): ApiResponse<List<AttendanceReportRowDto>> {
        val t = token()
        return client.get("$baseUrl/api/admin/reports/attendance") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** GET /api/admin/reports/grades */
    suspend fun getGradesReport(): ApiResponse<List<GradesReportRowDto>> {
        val t = token()
        return client.get("$baseUrl/api/admin/reports/grades") {
            header("Authorization", bearer(t))
        }.body()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Student Dashboard  (/api/student/dashboard)
    // ─────────────────────────────────────────────────────────────────────────

    /** GET /api/student/dashboard */
    suspend fun getStudentDashboard(): ApiResponse<StudentDashboardDto> {
        val t = token()
        return client.get("$baseUrl/api/student/dashboard") {
            header("Authorization", bearer(t))
        }.body()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Doctor Dashboard  (/api/doctor/dashboard)
    // ─────────────────────────────────────────────────────────────────────────

    /** GET /api/doctor/dashboard */
    suspend fun getDoctorDashboard(): ApiResponse<DoctorDashboardDto> {
        val t = token()
        return client.get("$baseUrl/api/doctor/dashboard") {
            header("Authorization", bearer(t))
        }.body()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Departments  (/api/departments/...)
    // ─────────────────────────────────────────────────────────────────────────

    /** GET /api/departments */
    suspend fun getDepartments(): ApiResponse<List<DepartmentDto>> {
        val t = token()
        return client.get("$baseUrl/api/departments") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** GET /api/departments/{id} */
    suspend fun getDepartment(id: Int): ApiResponse<DepartmentDto> {
        val t = token()
        return client.get("$baseUrl/api/departments/$id") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** POST /api/departments */
    suspend fun createDepartment(name: String): ApiResponse<DepartmentDto> {
        val t = token()
        return client.post("$baseUrl/api/departments") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(mapOf("name" to name))
        }.body()
    }

    /** PUT /api/departments/{id} */
    suspend fun updateDepartment(id: Int, name: String): ApiResponse<DepartmentDto> {
        val t = token()
        return client.put("$baseUrl/api/departments/$id") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(mapOf("name" to name))
        }.body()
    }

    /** DELETE /api/departments/{id} */
    suspend fun deleteDepartment(id: Int): ApiResponse<Unit> {
        val t = token()
        return client.delete("$baseUrl/api/departments/$id") {
            header("Authorization", bearer(t))
        }.body()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Students  (/api/students/...)
    // ─────────────────────────────────────────────────────────────────────────

    /** GET /api/students?page=&per_page=&department_id=&level=&search= */
    suspend fun getStudents(
        page: Int = 1,
        perPage: Int = 20,
        departmentId: Int? = null,
        level: Int? = null,
        search: String? = null,
    ): PaginatedResponse<StudentDto> {
        val t = token()
        val q = buildQuery(
            "page" to page,
            "per_page" to perPage,
            "department_id" to departmentId,
            "level" to level,
            "search" to search,
        )
        return client.get("$baseUrl/api/students?$q") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** GET /api/students/{id} */
    suspend fun getStudent(id: Int): ApiResponse<StudentDto> {
        val t = token()
        return client.get("$baseUrl/api/students/$id") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** POST /api/students */
    suspend fun createStudent(request: StudentRegisterRequest): ApiResponse<StudentDto> {
        val t = token()
        return client.post("$baseUrl/api/students") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /** PUT /api/students/{id} */
    suspend fun updateStudent(
        id: Int,
        phone: String? = null,
        departmentId: Int? = null,
        level: Int? = null,
        gpa: Float? = null,
        name: String? = null,
        email: String? = null,
        isActive : Boolean? = null
    ): ApiResponse<StudentDto> {
        val t = token()
        val body = buildJsonObject {
            if (phone        != null) put("phone",         phone)
            if (departmentId != null) put("department_id", departmentId)
            if (level        != null) put("level",         level)
            if (gpa          != null) put("gpa",           gpa)
            if (name         != null) put("name",          name)
            if (email        != null) put("email",         email)
            if (isActive     != null) put("is_active",     if (isActive) 1 else 0)
        }
        return client.put("$baseUrl/api/students/$id") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
    }

    // ── updateEmployee ────────────────────────────────────────────────────────────
    suspend fun updateEmployee(
        id: Int,
        name: String? = null,
        email: String? = null,
        phone: String? = null,
        roleTitle: String? = null,
        salary: Double? = null,
        departmentId: Int? = null,
        hireDate: String? = null,
        isActive : Boolean? = null
    ): ApiResponse<EmployeeDto> {
        val t = token()
        val body = buildJsonObject {
            if (name         != null) put("name",          name)
            if (email        != null) put("email",         email)
            if (phone        != null) put("phone",         phone)
            if (roleTitle    != null) put("role_title",    roleTitle)
            if (salary       != null) put("salary",        salary)
            if (departmentId != null) put("department_id", departmentId)
            if (hireDate     != null) put("hire_date",     hireDate)
            if (isActive     != null) put("is_active",     if (isActive) 1 else 0)

        }
        return client.put("$baseUrl/api/employees/$id") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
    }

    /** DELETE /api/students/{id} */
    suspend fun deleteStudent(id: Int): ApiResponse<Unit> {
        val t = token()
        return client.delete("$baseUrl/api/students/$id") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** GET /api/students/{id}/courses?status= */
    suspend fun getStudentCourses(
        studentId: Int,
        status: String = "active",
    ): ApiResponse<List<StudentCourseDto>> {
        val t = token()
        return client.get("$baseUrl/api/students/$studentId/courses?status=$status") {
            header("Authorization", bearer(t))
        }.body()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Employees  (/api/employees/...)
    // ─────────────────────────────────────────────────────────────────────────

    /** GET /api/employees?page=&per_page=&department_id=&role=&search= */
    suspend fun getEmployees(
        page: Int = 1,
        perPage: Int = 20,
        departmentId: Int? = null,
        role: String? = null,
        search: String? = null,
    ): PaginatedResponse<EmployeeDto> {
        val t = token()
        val q = buildQuery(
            "page" to page,
            "per_page" to perPage,
            "department_id" to departmentId,
            "role" to role,
            "search" to search,
        )
        return client.get("$baseUrl/api/employees?$q") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** GET /api/employees/{id} */
    suspend fun getEmployee(id: Int): ApiResponse<EmployeeDto> {
        val t = token()
        return client.get("$baseUrl/api/employees/$id") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** POST /api/employees */
    suspend fun createEmployee(request: DoctorRegisterRequest): ApiResponse<EmployeeDto> {
        val t = token()
        return client.post("$baseUrl/api/employees") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /** DELETE /api/employees/{id} */
    suspend fun deleteEmployee(id: Int): ApiResponse<Unit> {
        val t = token()
        return client.delete("$baseUrl/api/employees/$id") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** PUT /api/employees/{id}/activate */
    suspend fun activateEmployee(id: Int): ApiResponse<Unit> {
        val t = token()
        return client.put("$baseUrl/api/employees/$id/activate") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** PUT /api/employees/{id}/deactivate */
    suspend fun deactivateEmployee(id: Int): ApiResponse<Unit> {
        val t = token()
        return client.put("$baseUrl/api/employees/$id/deactivate") {
            header("Authorization", bearer(t))
        }.body()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Courses  (/api/courses/...)
    // ─────────────────────────────────────────────────────────────────────────

    /** GET /api/courses?page=&per_page=&department_id=&doctor_id=&semester=&is_active=&search= */
    suspend fun getCourses(
        page: Int = 1,
        perPage: Int = 20,
        departmentId: Int? = null,
        doctorId: Int? = null,
        semester: String? = null,
        isActive: Int? = null,
        search: String? = null,
    ): PaginatedResponse<CourseDto> {
        val t = token()
        val q = buildQuery(
            "page" to page,
            "per_page" to perPage,
            "department_id" to departmentId,
            "doctor_id" to doctorId,
            "semester" to semester,
            "is_active" to isActive,
            "search" to search,
        )
        return client.get("$baseUrl/api/courses?$q") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** GET /api/courses/{id} */
    suspend fun getCourse(id: Int): ApiResponse<CourseDto> {
        val t = token()
        return client.get("$baseUrl/api/courses/$id") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** POST /api/courses */
    suspend fun createCourse(request: CourseRequest): ApiResponse<CourseDto> {
        val t = token()
        return client.post("$baseUrl/api/courses") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /** PUT /api/courses/{id} */
    suspend fun updateCourse(id: Int, request: CourseRequest): ApiResponse<CourseDto> {
        val t = token()
        return client.put("$baseUrl/api/courses/$id") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /** DELETE /api/courses/{id} */
    suspend fun deleteCourse(id: Int): ApiResponse<Unit> {
        val t = token()
        return client.delete("$baseUrl/api/courses/$id") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** PUT /api/courses/{id}/assign-doctor */
    suspend fun assignDoctorToCourse(
        courseId: Int,
        doctorId: Int?,
    ): ApiResponse<CourseDto> {
        val t = token()
        return client.put("$baseUrl/api/courses/$courseId/assign-doctor") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(AssignDoctorRequest(doctorId))
        }.body()
    }

    /** GET /api/courses/{id}/students */
    suspend fun getCourseStudents(courseId: Int): ApiResponse<List<CourseStudentDto>> {
        val t = token()
        return client.get("$baseUrl/api/courses/$courseId/students") {
            header("Authorization", bearer(t))
        }.body()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Registrations  (/api/registrations/...)
    // ─────────────────────────────────────────────────────────────────────────

    /** POST /api/registrations */
    suspend fun registerStudentToCourse(
        studentId: Int,
        courseId: Int,
    ): ApiResponse<CourseRegistrationResponseDto> {
        val t = token()
        return client.post("$baseUrl/api/registrations") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(CourseRegistrationRequest(studentId, courseId))
        }.body()
    }

    /** DELETE /api/registrations/drop */
    suspend fun dropCourse(
        studentId: Int,
        courseId: Int,
    ): ApiResponse<Unit> {
        val t = token()
        return client.delete("$baseUrl/api/registrations/drop") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(CourseRegistrationRequest(studentId, courseId))
        }.body()
    }

    /** PUT /api/registrations/grade */
    suspend fun updateGrade(
        studentId: Int,
        courseId: Int,
        grade: Float,
    ): ApiResponse<Unit> {
        val t = token()
        return client.put("$baseUrl/api/registrations/grade") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(UpdateGradeRequest(studentId, courseId, grade))
        }.body()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Attendance – Students  (/api/attendance/students/...)
    // ─────────────────────────────────────────────────────────────────────────

    /** POST /api/attendance/students */
    suspend fun markStudentAttendance(
        request: MarkAttendanceRequest,
    ): ApiResponse<AttendanceRecordDto> {
        val t = token()
        return client.post("$baseUrl/api/attendance/students") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /** POST /api/attendance/students/bulk */
    suspend fun markStudentAttendanceBulk(
        request: BulkAttendanceRequest,
    ): ApiResponse<BulkAttendanceResponseDto> {
        val t = token()
        return client.post("$baseUrl/api/attendance/students/bulk") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /**
     * GET /api/attendance/students/{userId}
     * Query params: page, per_page, date_from, date_to, status, course_id
     */
    suspend fun getStudentAttendanceHistory(
        userId: Int,
        page: Int = 1,
        perPage: Int = 30,
        dateFrom: String? = null,
        dateTo: String? = null,
        status: String? = null,
        courseId: Int? = null,
    ): AttendanceResponseDto {
        val t = token()
        val q = buildQuery(
            "page" to page,
            "per_page" to perPage,
            "date_from" to dateFrom,
            "date_to" to dateTo,
            "status" to status,
            "course_id" to courseId,
        )
        return client.get("$baseUrl/api/attendance/students/$userId?$q") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** GET /api/attendance/courses/{courseId}?date= */
    suspend fun getCourseAttendance(
        courseId: Int,
        date: String? = null,
    ): ApiResponse<List<AttendanceRecordDto>> {
        val t = token()
        val q = if (date != null) "?date=$date" else ""
        return client.get("$baseUrl/api/attendance/courses/$courseId$q") {
            header("Authorization", bearer(t))
        }.body()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Attendance – Employees  (/api/attendance/employees/...)
    // ─────────────────────────────────────────────────────────────────────────

    /** POST /api/attendance/employees/check-in */
    suspend fun employeeCheckIn(notes: String? = null): ApiResponse<AttendanceRecordDto> {
        val t = token()
        return client.post("$baseUrl/api/attendance/employees/check-in") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(CheckInOutRequest(notes))
        }.body()
    }

    /** POST /api/attendance/employees/check-out */
    suspend fun employeeCheckOut(notes: String? = null): ApiResponse<AttendanceRecordDto> {
        val t = token()
        return client.post("$baseUrl/api/attendance/employees/check-out") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(CheckInOutRequest(notes))
        }.body()
    }

    /**
     * GET /api/attendance/employees/{userId}
     * Query params: page, per_page, date_from, date_to, status
     */
    suspend fun getEmployeeAttendanceHistory(
        userId: Int,
        page: Int = 1,
        perPage: Int = 30,
        dateFrom: String? = null,
        dateTo: String? = null,
        status: String? = null,
    ): AttendanceResponseDto {
        val t = token()
        val q = buildQuery(
            "page" to page,
            "per_page" to perPage,
            "date_from" to dateFrom,
            "date_to" to dateTo,
            "status" to status,
        )
        return client.get("$baseUrl/api/attendance/employees/$userId?$q") {
            header("Authorization", bearer(t))
        }.body()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Schedules  (/api/schedules/...)
    // ─────────────────────────────────────────────────────────────────────────

    /** GET /api/schedules/my  (student or doctor) */
    suspend fun getMySchedule(): ApiResponse<List<ScheduleSessionDto>> {
        val t = token()
        return client.get("$baseUrl/api/schedules/my") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** GET /api/schedules/courses/{id} */
    suspend fun getCourseSchedule(courseId: Int): ApiResponse<List<ScheduleSessionDto>> {
        val t = token()
        return client.get("$baseUrl/api/schedules/courses/$courseId") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** POST /api/schedules */
    suspend fun createSchedule(request: ScheduleRequest): ApiResponse<ScheduleSessionDto> {
        val t = token()
        return client.post("$baseUrl/api/schedules") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /** PUT /api/schedules/{id} */
    suspend fun updateSchedule(
        id: Int,
        request: ScheduleRequest,
    ): ApiResponse<ScheduleSessionDto> {
        val t = token()
        return client.put("$baseUrl/api/schedules/$id") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /** DELETE /api/schedules/{id} */
    suspend fun deleteSchedule(id: Int): ApiResponse<Unit> {
        val t = token()
        return client.delete("$baseUrl/api/schedules/$id") {
            header("Authorization", bearer(t))
        }.body()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Questions  (/api/questions/...)
    // ─────────────────────────────────────────────────────────────────────────

    /** GET /api/questions?page=&per_page=&course_id=&type=&difficulty= */
    suspend fun getQuestions(
        page: Int = 1,
        perPage: Int = 20,
        courseId: Int? = null,
        type: String? = null,
        difficulty: String? = null,
    ): PaginatedResponse<QuestionDto> {
        val t = token()
        val q = buildQuery(
            "page" to page,
            "per_page" to perPage,
            "course_id" to courseId,
            "type" to type,
            "difficulty" to difficulty,
        )
        return client.get("$baseUrl/api/questions?$q") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** GET /api/questions/{id} */
    suspend fun getQuestion(id: Int): ApiResponse<QuestionDto> {
        val t = token()
        return client.get("$baseUrl/api/questions/$id") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** POST /api/questions */
    suspend fun createQuestion(request: QuestionRequest): ApiResponse<QuestionDto> {
        val t = token()
        return client.post("$baseUrl/api/questions") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /** PUT /api/questions/{id} */
    suspend fun updateQuestion(
        id: Int,
        request: QuestionRequest,
    ): ApiResponse<QuestionDto> {
        val t = token()
        return client.put("$baseUrl/api/questions/$id") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /** DELETE /api/questions/{id} */
    suspend fun deleteQuestion(id: Int): ApiResponse<Unit> {
        val t = token()
        return client.delete("$baseUrl/api/questions/$id") {
            header("Authorization", bearer(t))
        }.body()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Exams  (/api/exams/...)
    // ─────────────────────────────────────────────────────────────────────────

    /** GET /api/exams?page=&per_page=&course_id=&is_published= */
    suspend fun getExams(
        page: Int = 1,
        perPage: Int = 20,
        courseId: Int? = null,
        isPublished: Int? = null,
    ): PaginatedResponse<ExamDto> {
        val t = token()
        val q = buildQuery(
            "page" to page,
            "per_page" to perPage,
            "course_id" to courseId,
            "is_published" to isPublished,
        )
        return client.get("$baseUrl/api/exams?$q") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** GET /api/exams/{id}  — includes questions list */
    suspend fun getExam(id: Int): ApiResponse<ExamDto> {
        val t = token()
        return client.get("$baseUrl/api/exams/$id") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** POST /api/exams */
    suspend fun createExam(request: ExamRequest): ApiResponse<ExamDto> {
        val t = token()
        return client.post("$baseUrl/api/exams") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    /** POST /api/exams/{id}/questions */
    suspend fun addQuestionToExam(
        examId: Int,
        questionId: Int,
        order: Int = 1,
    ): ApiResponse<Unit> {
        val t = token()
        return client.post("$baseUrl/api/exams/$examId/questions") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(mapOf("question_id" to questionId, "order" to order))
        }.body()
    }

    /** DELETE /api/exams/{id}/questions/{qid} */
    suspend fun removeQuestionFromExam(
        examId: Int,
        questionId: Int,
    ): ApiResponse<Unit> {
        val t = token()
        return client.delete("$baseUrl/api/exams/$examId/questions/$questionId") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** PUT /api/exams/{id}/publish */
    suspend fun publishExam(examId: Int): ApiResponse<Unit> {
        val t = token()
        return client.put("$baseUrl/api/exams/$examId/publish") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** POST /api/exams/{id}/submit */
    suspend fun submitExam(
        examId: Int,
        answers: List<ExamAnswerRequest>,
    ): ApiResponse<ExamSubmitResponseDto> {
        val t = token()
        return client.post("$baseUrl/api/exams/$examId/submit") {
            header("Authorization", bearer(t))
            contentType(ContentType.Application.Json)
            setBody(ExamSubmitRequest(answers))
        }.body()
    }

    /** GET /api/exams/{id}/results/{submissionId} */
    suspend fun getExamResults(
        examId: Int,
        submissionId: Int,
    ): ApiResponse<ExamSubmissionDto> {
        val t = token()
        return client.get("$baseUrl/api/exams/$examId/results/$submissionId") {
            header("Authorization", bearer(t))
        }.body()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Notifications  (/api/notifications/...)
    // ─────────────────────────────────────────────────────────────────────────

    /** GET /api/notifications?page=&per_page= */
    suspend fun getNotifications(
        page: Int = 1,
        perPage: Int = 20,
    ): NotificationsResponseDto {
        val t = token()
        return client.get("$baseUrl/api/notifications?page=$page&per_page=$perPage") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** PUT /api/notifications/{id}/read */
    suspend fun markNotificationRead(id: Int): ApiResponse<Unit> {
        val t = token()
        return client.put("$baseUrl/api/notifications/$id/read") {
            header("Authorization", bearer(t))
        }.body()
    }

    /** PUT /api/notifications/read-all */
    suspend fun markAllNotificationsRead(): ApiResponse<Unit> {
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

    // ─────────────────────────────────────────────────────────────────────────
    // Query-string builder
    // ─────────────────────────────────────────────────────────────────────────

    private fun buildQuery(vararg pairs: Pair<String, Any?>): String =
        pairs
            .filter { it.second != null }
            .joinToString("&") { "${it.first}=${it.second}" }
}
