package noor.serry.rawaa.ui.screens.home_teacher

import noor.serry.rawaa.domain.entity.AssignmentEntity
import noor.serry.rawaa.domain.entity.CourseEntity
import noor.serry.rawaa.domain.entity.ScheduleSessionEntity
import noor.serry.rawaa.domain.entity.TeacherDashboardEntity

fun TeacherDashboardEntity.toHomeTeacherUiState() = HomeTeacherUiState(
    isLoading = false,
    teacherName = teacherName,
    pendingTasks = pendingTasks,
    totalStudents = totalStudents,
    activeCourses = activeCourses,
    todaySessions = todaySessions.map { it.toTeacherSessionUiModel() },
    pendingAssignments = pendingAssignments.map { it.toTeacherPendingAssignmentUiModel() },
    courses = courses.map { it.toTeacherCourseSummaryUiModel() },
    weeklySubmissionRate = weeklySubmissionRate,
    weeklyAttendanceRate = weeklyAttendanceRate,
)

fun ScheduleSessionEntity.toTeacherSessionUiModel() = TeacherSessionUiModel(
    id = id,
    courseName = courseName,
    time = "$startTime - $endTime",
    location = location,
    studentsCount = 0,
)

fun AssignmentEntity.toTeacherPendingAssignmentUiModel() = TeacherPendingAssignmentUiModel(
    id = id,
    title = title,
    courseName = courseName,
    pendingCount = submittedCount,
    deadline = deadline,
)

fun CourseEntity.toTeacherCourseSummaryUiModel() = TeacherCourseSummaryUiModel(
    id = id,
    name = name,
    averageGrade = averageGrade,
    totalAssignments = totalAssignments,
    totalStudents = totalStudents,
    averageProgress = averageGrade / 100f,
)