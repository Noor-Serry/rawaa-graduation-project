package noor.serry.rawaa.ui.navigation.base

import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import noor.serry.rawaa.ui.navigation.student.studentEntryProvider
import noor.serry.rawaa.ui.screens.login.LoginScreen
import noor.serry.rawaa.ui.screens.onboarding.OnboardingScreen
import noor.serry.rawaa.ui.screens.register.RegisterScreen
import noor.serry.rawaa.ui.screens.studentScreens.menu.StudentEntryPoint

val appEntryProvider: (NavKey) -> NavEntry<NavKey> = entryProvider {
    entry<AppRoute.Login> {
        LoginScreen()
    }

    entry<AppRoute.Onboarding> {
        OnboardingScreen()
    }

    entry<AppRoute.Register> {
        RegisterScreen()
    }

    entry<AppRoute.StudentEntry> {
        StudentEntryPoint()
    }

}

