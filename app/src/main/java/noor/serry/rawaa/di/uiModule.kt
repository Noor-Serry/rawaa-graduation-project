package noor.serry.rawaa.di

import noor.serry.rawaa.ui.MainViewModel
import noor.serry.rawaa.ui.base.DefaultDispatcherProvider
import noor.serry.rawaa.ui.base.DispatcherProvider
import noor.serry.rawaa.ui.screens.courses_student.CoursesViewModel
import noor.serry.rawaa.ui.screens.login.LoginViewModel
import noor.serry.rawaa.ui.screens.onboarding.OnboardingViewModel
import noor.serry.rawaa.ui.screens.register.RegisterViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import noor.serry.rawaa.ui.screens.courses_teacher.CoursesTeacherViewModel
import noor.serry.rawaa.ui.screens.grading.GradingViewModel
import noor.serry.rawaa.ui.screens.home_student.HomeStudentViewModel
import noor.serry.rawaa.ui.screens.home_teacher.HomeTeacherViewModel
import noor.serry.rawaa.ui.screens.notifications.NotificationsViewModel
import noor.serry.rawaa.ui.screens.profile_student.ProfileViewModel
import noor.serry.rawaa.ui.screens.profile_teacher.ProfileTeacherViewModel
import noor.serry.rawaa.ui.screens.schedule.ScheduleViewModel
import noor.serry.rawaa.ui.screens.studentScreens.menu.MenuViewModel
import noor.serry.rawaa.ui.screens.students.StudentsViewModel
val uiModule = module{
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    singleOf(::DefaultDispatcherProvider) bind DispatcherProvider::class


    viewModelOf(::HomeStudentViewModel)
    viewModelOf(::HomeTeacherViewModel)
    viewModelOf(::CoursesViewModel)
    viewModelOf(::CoursesTeacherViewModel)
    viewModelOf(::GradingViewModel)
    viewModelOf(::NotificationsViewModel)
    viewModelOf(::ScheduleViewModel)
    viewModelOf(::StudentsViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::ProfileTeacherViewModel)
    viewModelOf(::MenuViewModel)

    singleOf(::MainViewModel)
}