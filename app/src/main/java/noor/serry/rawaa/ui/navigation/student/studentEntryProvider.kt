package noor.serry.rawaa.ui.navigation.student

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import noor.serry.rawaa.ui.screens.courses_student.CoursesScreen
import noor.serry.rawaa.ui.screens.home_student.HomeStudentScreen
import noor.serry.rawaa.ui.screens.login.LoginScreen
import noor.serry.rawaa.ui.screens.notifications.NotificationsScreen
import noor.serry.rawaa.ui.screens.profile_student.ProfileScreen
import noor.serry.rawaa.ui.screens.register.RegisterScreen
import noor.serry.rawaa.ui.screens.schedule.ScheduleScreen
import noor.serry.rawaa.ui.screens.studentScreens.menu.MenuScreen

val studentEntryProvider: (NavKey) -> NavEntry<NavKey> = entryProvider {


    entry<StudentRouteKeys.Home> {
        HomeStudentScreen()
    }
    entry<StudentRouteKeys.Courses> {
        CoursesScreen()
    }
    entry<StudentRouteKeys.Profile> {
        ProfileScreen()
    }
    entry<StudentRouteKeys.Notifications> {
        NotificationsScreen()
    }
    entry<StudentRouteKeys.Schedule> {
        ScheduleScreen()
    }
}
