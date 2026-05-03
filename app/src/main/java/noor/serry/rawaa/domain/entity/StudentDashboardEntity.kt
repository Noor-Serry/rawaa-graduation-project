package noor.serry.rawaa.domain.entity

data class StudentDashboardEntity(
    val studentName: String,
    val cgpa: Float,
    val pendingAssignments: Int,
    val activeCourses: Int,
    val todaySessions: List<ScheduleSessionEntity>,
    val upcomingAssignments: List<AssignmentEntity>,
    val courseProgress: List<CourseProgressEntity>,
)

data class TeacherDashboardEntity(
    val teacherName: String,
    val pendingTasks: Int,
    val totalStudents: Int,
    val activeCourses: Int,
    val todaySessions: List<ScheduleSessionEntity>,
    val pendingAssignments: List<AssignmentEntity>,
    val courses: List<CourseEntity>,
    val weeklySubmissionRate: Int,
    val weeklyAttendanceRate: Int,
)

data class CourseProgressEntity(
    val courseName: String,
    val progress: Float,
)