package noor.serry.rawaa.ui.screens.profile_teacher

import noor.serry.rawaa.domain.entity.AchievementEntity
import noor.serry.rawaa.domain.entity.CourseRefEntity
import noor.serry.rawaa.domain.entity.TeacherProfileEntity

fun TeacherProfileEntity.toProfileTeacherUiState() = ProfileTeacherUiState(
    isLoading = false,
    employeeId = employeeId,
    name = name,
    department = department,
    specialization = specialization,
    degree = degree,
    experienceYears = experienceYears,
    enrollmentDate = enrollmentDate,
    email = email,
    phone = phone,
    office = office,
    officeHours = officeHours,
    totalStudents = totalStudents,
    activeCourses = activeCourses,
    currentCourses = currentCourses.map { it.toCourseRefUiModel() },
    achievements = achievements.map { it.toAchievementUiModel() },
)

fun CourseRefEntity.toCourseRefUiModel() = CourseRefUiModel(
    name = name,
    code = code,
    studentsCount = students,
)

fun AchievementEntity.toAchievementUiModel() = AchievementUiModel(
    title = title,
    year = year,
)