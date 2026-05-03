package noor.serry.rawaa.domain.entity

data class StudentEntity(
    val id: String,
    val name: String,
    val email: String,
    val status: StudentStatus,
    val attendance: Int,
    val grade: Int,
    val assignmentsSubmitted: Int,
    val totalAssignments: Int,
    val assignmentProgress: Float,
)

enum class StudentStatus { EXCELLENT, GOOD, NEEDS_FOLLOW_UP }