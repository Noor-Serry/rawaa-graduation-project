package noor.serry.rawaa.ui.screens.home_student

import noor.serry.rawaa.domain.entity.AssignmentEntity
import noor.serry.rawaa.domain.entity.CourseProgressEntity
import noor.serry.rawaa.domain.entity.ScheduleSessionEntity
import noor.serry.rawaa.domain.entity.SessionType
import noor.serry.rawaa.domain.entity.StudentDashboardEntity

//fun StudentDashboardEntity.toHomeStudentUiState() = HomeStudentUiState(
//    isLoading = false,
//    studentName = studentName,
//    cgpa = cgpa,
//    pendingAssignments = pendingAssignments,
//    activeCourses = activeCourses,
//    todaySessions = todaySessions.map { it.toSessionUiModel() },
//    upcomingAssignments = upcomingAssignments.map { it.toAssignmentUiModel() },
//    courseProgress = courseProgress.map { it.toCourseProgressUiModel() },
//)
//
//fun ScheduleSessionEntity.toSessionUiModel() = SessionUiModel(
//    id = id,
//    courseName = courseName,
//    instructorName = instructorName,
//    time = "$startTime - $endTime",
//    location = location,
//    isLecture = type == SessionType.LECTURE,
//)
//
//fun AssignmentEntity.toAssignmentUiModel() = AssignmentUiModel(
//    id = id,
//    title = title,
//    courseName = courseName,
//    deadline = deadline,
//)
//
//fun CourseProgressEntity.toCourseProgressUiModel() = CourseProgressUiModel(
//    courseName = courseName,
//    progress = progress,
//    progressPercent = (progress * 100).toInt(),
//)