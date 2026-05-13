package noor.serry.rawaa.di

import noor.serry.rawaa.ui.screens.attendance_admin.AttendanceAdminViewModel
import noor.serry.rawaa.ui.screens.attendance_admin.CourseAttendanceDetailViewModel
import noor.serry.rawaa.ui.screens.attendance_admin.StudentAttendanceDetailViewModel
import noor.serry.rawaa.ui.screens.departments_admin.DepartmentsAdminViewModel
import noor.serry.rawaa.ui.screens.exams_admin.ExamDetailViewModel
import noor.serry.rawaa.ui.screens.exams_admin.ExamsAdminViewModel
import noor.serry.rawaa.ui.screens.exams_admin.QuestionsAdminViewModel
import noor.serry.rawaa.ui.screens.schedules_admin.SchedulesAdminViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin module for all University Admin ViewModels.
 *
 * Add this module to your Koin startKoin { } call alongside the existing uiModule.
 * Example:
 *   startKoin {
 *       modules(dataModule, uiModule, universityAdminUiModule)
 *   }
 */
val universityAdminUiModule = module {

    // ── List / CRUD screens ────────────────────────────────────────────────────

    viewModelOf(::DepartmentsAdminViewModel)
    viewModelOf(::SchedulesAdminViewModel)
    viewModelOf(::AttendanceAdminViewModel)
    viewModelOf(::ExamsAdminViewModel)
    viewModelOf(::QuestionsAdminViewModel)

    // ── Parametrised detail screens ────────────────────────────────────────────

    // StudentAttendanceDetailViewModel(studentUserId: Int, repository)
    viewModel { params ->
        StudentAttendanceDetailViewModel(
            studentUserId = params.get(),
            repository    = get(),
        )
    }

    // CourseAttendanceDetailViewModel(courseId: Int, repository)
    viewModel { params ->
        CourseAttendanceDetailViewModel(
            courseId   = params.get(),
            repository = get(),
        )
    }

    // ExamDetailViewModel(examId: Int, repository)
    viewModel { params ->
        ExamDetailViewModel(
            examId     = params.get(),
            repository = get(),
        )
    }
}
