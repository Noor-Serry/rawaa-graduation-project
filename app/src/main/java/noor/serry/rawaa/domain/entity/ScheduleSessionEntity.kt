package noor.serry.rawaa.domain.entity

data class ScheduleSessionEntity(
    val id: String,
    val courseCode: String,
    val courseName: String,
    val type: SessionType,
    val startTime: String,
    val endTime: String,
    val location: String,
    val instructorName: String,
    val dayIndex: Int,
)

enum class SessionType { LECTURE, LAB }

data class ScheduleSummaryEntity(
    val days: Int,
    val courses: Int,
    val weeklyLectures: Int,
)