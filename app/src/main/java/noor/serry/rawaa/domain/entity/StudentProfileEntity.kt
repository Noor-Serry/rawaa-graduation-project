package noor.serry.rawaa.domain.entity

data class StudentProfileEntity(
    val id: String,
    val universityId: String,
    val name: String,
    val college: String,
    val major: String,
    val level: String,
    val enrollmentDate: String,
    val email: String,
    val phone: String,
    val birthDate: String,
    val address: String,
    val studyYears: Int,
    val completedCourses: Int,
    val cgpa: Float,
    val achievementsCount: Int,
    val certificatesCount: Int,
)

data class TeacherProfileEntity(
    val id: String,
    val employeeId: String,
    val name: String,
    val department: String,
    val specialization: String,
    val degree: String,
    val experienceYears: Int,
    val enrollmentDate: String,
    val email: String,
    val phone: String,
    val office: String,
    val officeHours: String,
    val totalStudents: Int,
    val activeCourses: Int,
    val currentCourses: List<CourseRefEntity>,
    val achievements: List<AchievementEntity>,
)

data class CourseRefEntity(
    val name: String,
    val code: String,
    val students: Int,
)

data class AchievementEntity(
    val title: String,
    val year: String,
)