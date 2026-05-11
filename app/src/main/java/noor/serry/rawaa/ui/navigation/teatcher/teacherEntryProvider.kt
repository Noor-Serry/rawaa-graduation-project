package noor.serry.rawaa.ui.navigation.teatcher

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import noor.serry.rawaa.ui.screens.courses_teacher.CoursesTeacherScreen
import noor.serry.rawaa.ui.screens.grading_teacher.GradingScreen
import noor.serry.rawaa.ui.screens.home_teacher.HomeTeacherScreen
import noor.serry.rawaa.ui.screens.profile_teacher.ProfileTeacherScreen
import noor.serry.rawaa.ui.screens.student_profile_teacher.StudentProfileScreen
import noor.serry.rawaa.ui.screens.students_teacher.StudentsScreen

val teacherEntryProvider: (NavKey) -> NavEntry<NavKey> = entryProvider {

    entry<TeacherRouteKeys.Home> {
        HomeTeacherScreen()
    }
    entry<TeacherRouteKeys.Courses> {
        CoursesTeacherScreen()
    }
    entry<TeacherRouteKeys.Assessment> {
        GradingScreen()
    }
    entry<TeacherRouteKeys.Students> {
        StudentsScreen()
    }
    entry<TeacherRouteKeys.Profile> {
        ProfileTeacherScreen()
    }

    entry<TeacherRouteKeys.StudentProfile> {
        StudentProfileScreen(it.studentId)
    }
}