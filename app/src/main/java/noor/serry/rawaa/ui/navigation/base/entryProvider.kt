package noor.serry.rawaa.ui.navigation.base

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import noor.serry.rawaa.ui.screens.login.LoginScreen
import noor.serry.rawaa.ui.screens.onboarding.OnboardingScreen
import noor.serry.rawaa.ui.navigation.student.StudentEntryPoint
import noor.serry.rawaa.ui.navigation.teatcher.TeacherEntryPoint
import noor.serry.rawaa.ui.navigation.admin.AdminEntryPoint
import noor.serry.rawaa.ui.navigation.superadmin.SuperAdminEntryPoint

val appEntryProvider: (NavKey) -> NavEntry<NavKey> = entryProvider {
    entry<AppRoute.Login> {
        LoginScreen()
    }

    entry<AppRoute.Onboarding> {
        OnboardingScreen()
    }

    entry<AppRoute.StudentEntry> {
        StudentEntryPoint()
    }

    entry<AppRoute.TeacherEntry> {
        TeacherEntryPoint()
    }

    entry<AppRoute.AdminEntry> {
        AdminEntryPoint()
    }

    entry<AppRoute.SuperAdminEntry> {
        SuperAdminEntryPoint()
    }
}
