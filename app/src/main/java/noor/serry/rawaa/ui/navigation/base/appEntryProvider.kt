package noor.serry.rawaa.ui.navigation.base

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import noor.serry.rawaa.ui.screens.login.LoginScreen
import noor.serry.rawaa.ui.screens.onboarding.OnboardingScreen
import noor.serry.rawaa.ui.navigation.student.StudentEntryPoint
import noor.serry.rawaa.ui.navigation.teatcher.TeacherEntryPoint
import noor.serry.rawaa.ui.navigation.super_admin.AdminEntryPoint          // super-admin (unchanged)
import noor.serry.rawaa.ui.navigation.university_admin.UniversityAdminEntryPoint  // university admin (new)

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

    /**
     * University admin (role = "admin") — uses the new full-featured nav graph.
     */
    entry<AppRoute.AdminEntry> {
        UniversityAdminEntryPoint()
    }

    /**
     * Super admin (role = "super_admin") — keeps the existing Anthropic/platform admin flow.
     */
    entry<AppRoute.SuperAdminEntry> {
        AdminEntryPoint()
    }
}
