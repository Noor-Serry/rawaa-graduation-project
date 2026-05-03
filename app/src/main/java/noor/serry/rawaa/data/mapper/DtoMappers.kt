package noor.serry.rawaa.data.mapper

import noor.serry.rawaa.data.dto.*
import noor.serry.rawaa.domain.entity.*

// ─── Course ──────────────────────────────────────────────────────────────────
// Backend CourseDto (from GET /api/courses) has flat doctor_name / department_name fields.

fun CourseDto.toEntity(): CourseEntity = CourseEntity(
    id = id.toString(),
    code = code,
    name = name,
    instructorName = doctorName ?: "",
    semester = buildSemesterLabel(semester, academicYear),
    // enrolled_count comes from the doctor-dashboard variant; fall back to 0
    totalStudents = enrolledCount ?: 0,
    // API v3 does not expose assignment counts on the course list endpoint
    totalAssignments = 0,
    pendingGrades = 0,
    averageGrade = 0,
    progress = 0f,
    nextSessionTime = "",
    nextSessionLocation = departmentName ?: "",
    level = CourseLevel.INTERMEDIATE,
    durationWeeks = 16,
)

private fun buildSemesterLabel(semester: String?, year: Int?): String {
    if (semester == null && year == null) return ""
    val sem = when (semester?.lowercase()) {
        "fall"   -> "الفصل الأول"
        "spring" -> "الفصل الثاني"
        "summer" -> "الصيف"
        else     -> semester ?: ""
    }
    return if (year != null) "$sem $year" else sem
}

// StudentCourseDto — the course items inside the student dashboard / student-courses endpoint
fun StudentCourseDto.toEntity(): CourseEntity = CourseEntity(
    id = courseId.toString(),
    code = code,
    name = courseName,
    instructorName = doctorName ?: "",
    semester = status ?: "",
    totalStudents = 0,
    totalAssignments = 0,
    pendingGrades = 0,
    averageGrade = grade?.toInt() ?: 0,
    progress = 0f,
    nextSessionTime = "",
    nextSessionLocation = "",
    level = CourseLevel.INTERMEDIATE,
    durationWeeks = 16,
)

// ─── Notification ─────────────────────────────────────────────────────────────

fun NotificationDto.toEntity(): NotificationEntity = NotificationEntity(
    id = id.toString(),
    title = title,
    body = body,
    timeAgo = createdAt ?: "",
    type = type.toNotificationType(),
    isRead = isRead == 1,
)

private fun String.toNotificationType(): NotificationType = when (lowercase()) {
    "grade"        -> NotificationType.GRADE
    "registration" -> NotificationType.ASSIGNMENT  // closest mapping available
    "exam"         -> NotificationType.EXAM
    "announcement" -> NotificationType.ANNOUNCEMENT
    "deadline"     -> NotificationType.DEADLINE
    else           -> NotificationType.ANNOUNCEMENT
}

// ─── Schedule ─────────────────────────────────────────────────────────────────
// Backend fields: course_name, code (as "code"), day, start_time, end_time, room_name, building, type, doctor_name

fun ScheduleSessionDto.toEntity(): ScheduleSessionEntity = ScheduleSessionEntity(
    id = id.toString(),
    courseCode = code ?: "",
    courseName = courseName ?: "",
    type = if (type.lowercase() == "lab") SessionType.LAB else SessionType.LECTURE,
    startTime = startTime,
    endTime = endTime,
    location = buildLocation(roomName, building),
    instructorName = doctorName ?: "",
    dayIndex = day.toDayIndex(),
)

private fun buildLocation(roomName: String?, building: String?): String = when {
    roomName != null && building != null -> "$roomName - $building"
    roomName != null -> roomName
    building != null -> building
    else -> ""
}

private fun String?.toDayIndex(): Int = when (this?.lowercase()) {
    "sunday"    -> 0
    "monday"    -> 1
    "tuesday"   -> 2
    "wednesday" -> 3
    "thursday"  -> 4
    "friday"    -> 5
    "saturday"  -> 6
    else        -> 0
}

// ─── Student ──────────────────────────────────────────────────────────────────
// Backend StudentDto: no attendance / grade / assignment fields at list level.
// Map what we have; UI should avoid showing N/A fields.

fun StudentDto.toEntity(): StudentEntity = StudentEntity(
    id = id.toString(),
    name = name,
    email = email,
    status = deriveStudentStatus(gpa),
    attendance = 0,             // not returned at list level; fetch per-student if needed
    grade = 0,                  // not returned at list level
    assignmentsSubmitted = 0,
    totalAssignments = 0,
    assignmentProgress = 0f,
)

private fun deriveStudentStatus(gpa: String?): StudentStatus {
    val g = gpa?.toFloatOrNull() ?: return StudentStatus.GOOD
    return when {
        g >= 3.5f -> StudentStatus.EXCELLENT
        g >= 2.0f -> StudentStatus.GOOD
        else      -> StudentStatus.NEEDS_FOLLOW_UP
    }
}

// ─── Dashboard — Student ──────────────────────────────────────────────────────

fun StudentDashboardDto.toEntity(): StudentDashboardEntity = StudentDashboardEntity(
    studentName = student.name,
    cgpa = student.gpa?.toFloatOrNull() ?: 0f,
    pendingAssignments = upcomingExams.size,     // use upcoming exams as proxy
    activeCourses = activeCourses,
    todaySessions = schedule.map { it.toEntity() },
    upcomingAssignments = upcomingExams.map { it.toAssignmentEntity() },
    courseProgress = courses.map {
        CourseProgressEntity(
            courseName = it.courseName,
            // We don't have a progress float from the API; derive from grade if present
            progress = if (it.grade != null) it.grade / 100f else 0f,
        )
    },
)

// Adapt ExamDto to AssignmentEntity so the existing UI keeps working
fun ExamDto.toAssignmentEntity(): AssignmentEntity = AssignmentEntity(
    id = id.toString(),
    title = title,
    courseName = courseName ?: "",
    courseCode = type,                   // use exam type as a label
    points = totalMarks?.toInt() ?: 0,
    deadline = startAt ?: "",
    submittedCount = 0,
    totalStudents = 0,
    averageGradingMinutes = durationMin ?: 0,
    isGraded = false,
    completionPercent = 0,
    averageGrade = 0,
)

// ─── Dashboard — Doctor ────────────────────────────────────────────────────────

fun DoctorDashboardDto.toEntity(): TeacherDashboardEntity = TeacherDashboardEntity(
    teacherName = "",                       // not returned at dashboard root level
    pendingTasks = upcomingExams.size,
    totalStudents = totalStudents,
    activeCourses = totalCourses,
    todaySessions = schedule.map { it.toEntity() },
    pendingAssignments = upcomingExams.map { it.toAssignmentEntity() },
    courses = courses.map { it.toEntity() },
    weeklySubmissionRate = 0,
    weeklyAttendanceRate = 0,
)

// ─── Profile — Student ────────────────────────────────────────────────────────
// GET /api/auth/me → UserDto { id, name, email, role, avatar, is_active, profile: UserProfileDto }

fun UserDto.toStudentProfileEntity(): StudentProfileEntity = StudentProfileEntity(
    id = id.toString(),
    universityId = profile?.id?.toString() ?: id.toString(),
    name = name,
    college = profile?.departmentName ?: "",
    major = profile?.departmentName ?: "",
    level = "المستوى ${profile?.level ?: ""}",
    enrollmentDate = profile?.enrollmentYear?.toString() ?: "",
    email = email,
    phone = profile?.phone ?: "",
    birthDate = "",
    address = "",
    studyYears = 4,
    completedCourses = 0,
    cgpa = profile?.gpa?.toFloatOrNull() ?: 0f,
    achievementsCount = 0,
    certificatesCount = 0,
)

fun UserDto.toTeacherProfileEntity(): TeacherProfileEntity = TeacherProfileEntity(
    id = id.toString(),
    employeeId = profile?.id?.toString() ?: id.toString(),
    name = name,
    department = profile?.departmentName ?: "",
    specialization = profile?.departmentName ?: "",
    degree = profile?.roleTitle ?: "",
    experienceYears = 0,
    enrollmentDate = profile?.hireDate ?: "",
    email = email,
    phone = profile?.phone ?: "",
    office = "",
    officeHours = "",
    totalStudents = 0,
    activeCourses = 0,
    currentCourses = emptyList(),
    achievements = emptyList(),
)
