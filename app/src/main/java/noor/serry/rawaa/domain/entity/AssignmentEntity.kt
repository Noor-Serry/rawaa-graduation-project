package noor.serry.rawaa.domain.entity

data class AssignmentEntity(
    val id: String,
    val title: String,
    val courseName: String,
    val courseCode: String,
    val points: Int,
    val deadline: String,
    val submittedCount: Int,
    val totalStudents: Int,
    val averageGradingMinutes: Int,
    val isGraded: Boolean,
    val completionPercent: Int,
    val averageGrade: Int,
)