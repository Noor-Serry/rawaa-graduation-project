package noor.serry.rawaa.ui.screens.grading_teacher

import noor.serry.rawaa.data.dto.CourseDto

/**
 * Maps [CourseDto] (from DoctorDashboardDto.courses) to the UI model
 * used by the grading screen.
 *
 * All fields are present in CourseDto — no invented fields.
 */
fun CourseDto.toCourseGradingUiModel(): CourseGradingUiModel =
    CourseGradingUiModel(
        id             = id,
        name           = name,
        code           = code,
        departmentName = departmentName,
        semester       = semester,
        creditHours    = creditHours,
        enrolledCount  = enrolledCount ?: 0,
        maxStudents    = maxStudents,
        isActive       = isActive == 1,
    )
