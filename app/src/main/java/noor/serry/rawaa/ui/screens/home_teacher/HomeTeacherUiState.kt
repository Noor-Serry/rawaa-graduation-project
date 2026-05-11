package noor.serry.rawaa.ui.screens.home_teacher

import noor.serry.rawaa.data.dto.CourseDto
import noor.serry.rawaa.data.dto.ScheduleSessionDto
import noor.serry.rawaa.data.dto.UpcomingExamDto

data class HomeTeacherUiState(
    val isLoading: Boolean = true,
    val doctorName: String = "",
    val totalStudents: Int = 0,
    val totalCourses: Int = 0,
    // NOTE: pendingGrading removed — DoctorDashboardDto has no such field.
    //       The backend does not return a pending-grading count.
    val todaySchedule: List<ScheduleSessionDto> = emptyList(),
    val courses: List<CourseDto> = emptyList(),
    val upcomingExams: List<UpcomingExamDto> = emptyList(),
    val error: String? = null,
)