package noor.serry.rawaa.ui.screens.students

import noor.serry.rawaa.domain.entity.StudentEntity
import noor.serry.rawaa.domain.entity.StudentStatus

fun StudentEntity.toStudentUiModel() = StudentUiModel(
    id = id,
    name = name,
    email = email,
    statusLabel = status.toLabel(),
    statusType = status.toType(),
    attendance = attendance,
    grade = grade,
    assignmentsSubmitted = assignmentsSubmitted,
    totalAssignments = totalAssignments,
    assignmentProgress = assignmentProgress,
    assignmentProgressPercent = (assignmentProgress * 100).toInt(),
    isTrendingUp = status != StudentStatus.NEEDS_FOLLOW_UP,
)

private fun StudentStatus.toLabel() = when (this) {
    StudentStatus.EXCELLENT -> "ممتاز"
    StudentStatus.GOOD -> "جيد"
    StudentStatus.NEEDS_FOLLOW_UP -> "يحتاج متابعة"
}

private fun StudentStatus.toType() = when (this) {
    StudentStatus.EXCELLENT -> StudentStatusType.EXCELLENT
    StudentStatus.GOOD -> StudentStatusType.GOOD
    StudentStatus.NEEDS_FOLLOW_UP -> StudentStatusType.NEEDS_FOLLOW_UP
}