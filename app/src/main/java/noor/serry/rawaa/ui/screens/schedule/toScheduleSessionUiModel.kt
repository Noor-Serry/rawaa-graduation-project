package noor.serry.rawaa.ui.screens.schedule//package noor.serry.rawaa.ui.screens.schedule
//
//import noor.serry.rawaa.domain.entity.ScheduleSessionEntity
//import noor.serry.rawaa.domain.entity.ScheduleSummaryEntity
//import noor.serry.rawaa.domain.entity.SessionType
//
//fun ScheduleSessionEntity.toScheduleSessionUiModel() = ScheduleSessionUiModel(
//    id = id,
//    courseCode = courseCode,
//    courseName = courseName,
//    sessionTypeLabel = if (type == SessionType.LECTURE) "محاضرة" else "عملي",
//    sessionTypeColor = if (type == SessionType.LECTURE) SessionColorType.LECTURE else SessionColorType.LAB,
//    startTime = startTime,
//    endTime = endTime,
//    timeRange = "$startTime - $endTime",
//    location = location,
//    instructorName = instructorName,
//    dayIndex = dayIndex,
//)
//
//fun ScheduleSummaryEntity.toScheduleUiState(sessions: List<ScheduleSessionUiModel>) = ScheduleUiState(
//    isLoading = false,
//    totalDays = days,
//    totalCourses = courses,
//    weeklyLectures = weeklyLectures,
//    allSessions = sessions,
//    selectedDayIndex = 1,
//)