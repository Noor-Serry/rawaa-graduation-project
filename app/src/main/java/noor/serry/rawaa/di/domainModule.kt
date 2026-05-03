package noor.serry.rawaa.di

import org.koin.dsl.module
import noor.serry.rawaa.domain.usecase.*
import org.koin.core.module.dsl.factoryOf

val domainModule = module {

    factoryOf(::GetStudentDashboardUseCase)
    factoryOf(::GetTeacherDashboardUseCase)
    factoryOf(::GetStudentCoursesUseCase)
    factoryOf(::GetAvailableCoursesUseCase)
    factoryOf(::GetTeacherCoursesUseCase)
    factoryOf(::GetPendingAssignmentsUseCase)
    factoryOf(::GetGradedAssignmentsUseCase)
    factoryOf(::GetNotificationsUseCase)
    factoryOf(::MarkAllNotificationsReadUseCase)
    factoryOf(::DeleteAllNotificationsUseCase)
    factoryOf(::GetWeeklyScheduleUseCase)
    factoryOf(::GetScheduleSummaryUseCase)
    factoryOf(::GetStudentsUseCase)
    factoryOf(::GetStudentProfileUseCase)
    factoryOf(::GetTeacherProfileUseCase)
}