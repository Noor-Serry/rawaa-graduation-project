package noor.serry.rawaa.ui.screens.student_profile_teacher

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin module for the teacher's read-only student profile screen.
 *
 * Register this alongside your other feature modules, e.g. in your app's
 * top-level appModule list:
 *
 *   startKoin {
 *       modules(
 *           …,
 *           studentProfileTeacherModule,
 *       )
 *   }
 *
 * The `viewModel { (id: Int) -> … }` declaration lets the screen call
 * koinViewModel(parameters = { parametersOf(studentId) }) and receive a
 * correctly scoped instance per student.
 */
val studentProfileTeacherModule = module {
    viewModel { (studentId: Int) ->
        StudentProfileViewModel(
            studentId   = studentId,
            repository  = get(),
            dispatchers = get(),
        )
    }
}
