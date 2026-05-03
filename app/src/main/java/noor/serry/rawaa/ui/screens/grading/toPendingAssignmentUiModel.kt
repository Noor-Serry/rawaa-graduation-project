package noor.serry.rawaa.ui.screens.grading

import noor.serry.rawaa.domain.entity.AssignmentEntity

fun AssignmentEntity.toPendingAssignmentUiModel() = PendingAssignmentUiModel(
    id = id,
    title = title,
    courseName = courseName,
    deadline = deadline,
    submittedCount = submittedCount,
    totalStudents = totalStudents,
    completionPercent = completionPercent,
    completionProgress = completionPercent / 100f,
    averageGradingMinutes = averageGradingMinutes,
)

fun AssignmentEntity.toGradedAssignmentUiModel() = GradedAssignmentUiModel(
    id = id,
    title = title,
    courseName = courseName,
    gradedDate = deadline,
    totalStudents = totalStudents,
    averageGrade = averageGrade,
)