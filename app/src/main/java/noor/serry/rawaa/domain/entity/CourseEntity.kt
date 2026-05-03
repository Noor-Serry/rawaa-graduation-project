package noor.serry.rawaa.domain.entity

data class CourseEntity(
    val id: String,
    val code: String,
    val name: String,
    val instructorName: String,
    val semester: String,
    val totalStudents: Int,
    val totalAssignments: Int,
    val pendingGrades: Int,
    val averageGrade: Int,
    val progress: Float,
    val nextSessionTime: String,
    val nextSessionLocation: String,
    val level: CourseLevel,
    val durationWeeks: Int,
)
 
enum class CourseLevel { BEGINNER, INTERMEDIATE, ADVANCED }