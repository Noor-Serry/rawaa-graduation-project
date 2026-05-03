package noor.serry.rawaa.ui.screens.courses_teacher

import noor.serry.rawaa.domain.entity.CourseEntity

fun CourseEntity.toTeacherCourseUiModel() = TeacherCourseUiModel(
    courseId = id,
    courseCode = code,
    courseName = name,
    semester = semester,
    totalStudents = totalStudents,
    totalAssignments = totalAssignments,
    pendingGrades = pendingGrades,
    averageGrade = averageGrade,
    averageProgress = if (averageGrade > 0) averageGrade / 100f else 0f,
)
