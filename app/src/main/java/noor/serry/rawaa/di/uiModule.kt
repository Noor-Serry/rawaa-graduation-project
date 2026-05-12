package noor.serry.rawaa.di

import noor.serry.rawaa.ui.MainViewModel
import noor.serry.rawaa.ui.base.DefaultDispatcherProvider
import noor.serry.rawaa.ui.base.DispatcherProvider
import noor.serry.rawaa.ui.screens.courses_admin.CoursesAdminViewModel
import noor.serry.rawaa.ui.screens.courses_student.CoursesViewModel
import noor.serry.rawaa.ui.screens.courses_teacher.CoursesTeacherViewModel
import noor.serry.rawaa.ui.screens.grading_teacher.GradingViewModel
import noor.serry.rawaa.ui.screens.home_admin.HomeAdminViewModel
import noor.serry.rawaa.ui.screens.home_student.HomeStudentViewModel
import noor.serry.rawaa.ui.screens.home_teacher.HomeTeacherViewModel
import noor.serry.rawaa.ui.screens.login.LoginViewModel
import noor.serry.rawaa.ui.screens.notifications.NotificationsViewModel
import noor.serry.rawaa.ui.screens.onboarding.OnboardingViewModel
import noor.serry.rawaa.ui.screens.profile_student.ProfileViewModel
import noor.serry.rawaa.ui.screens.profile_teacher.ProfileTeacherViewModel
import noor.serry.rawaa.ui.screens.reports_admin.ReportsAdminViewModel
import noor.serry.rawaa.ui.screens.schedule.ScheduleViewModel
import noor.serry.rawaa.ui.screens.settings_admin.SettingsAdminViewModel
import noor.serry.rawaa.ui.screens.studentScreens.menu.MenuViewModel
import noor.serry.rawaa.ui.screens.student_profile_teacher.StudentProfileViewModel
import noor.serry.rawaa.ui.screens.students_teacher.StudentsViewModel
import noor.serry.rawaa.ui.screens.users_admin.AddUserViewModel
import noor.serry.rawaa.ui.screens.users_admin.UsersAdminViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val uiModule = module {
    singleOf(::DefaultDispatcherProvider) bind DispatcherProvider::class

    // ── Auth / onboarding ─────────────────────────────────────────────────────
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::LoginViewModel)

    // ── Student screens ───────────────────────────────────────────────────────
    viewModelOf(::HomeStudentViewModel)
    viewModelOf(::CoursesViewModel)
    viewModelOf(::ScheduleViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::MenuViewModel)
    viewModelOf(::NotificationsViewModel)

    // ── Teacher screens ───────────────────────────────────────────────────────
    viewModelOf(::HomeTeacherViewModel)
    viewModelOf(::CoursesTeacherViewModel)
    viewModelOf(::GradingViewModel)
    viewModelOf(::StudentsViewModel)
    viewModelOf(::ProfileTeacherViewModel)
    viewModelOf(::StudentProfileViewModel)

    // ── Admin screens ─────────────────────────────────────────────────────────
    viewModelOf(::HomeAdminViewModel)
    viewModelOf(::UsersAdminViewModel)
    viewModelOf(::CoursesAdminViewModel)
    viewModelOf(::ReportsAdminViewModel)
    viewModelOf(::SettingsAdminViewModel)
    viewModelOf(::AddUserViewModel)
    // ── Shared ────────────────────────────────────────────────────────────────
    singleOf(::MainViewModel)
}
