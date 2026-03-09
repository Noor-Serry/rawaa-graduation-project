package noor.serry.rawaa.ui.navigation

import androidx.compose.material3.Text
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import noor.serry.rawaa.ui.screens.onboarding.OnboardingScreen

val appEntryProvider: (NavKey) -> NavEntry<NavKey> = entryProvider {
    entry<AppRoute.Login> {
        Text("Login Screen")
    }

    entry<AppRoute.Onboarding> {
        OnboardingScreen()
    }

    entry<AppRoute.Register> {
        Text("Register Screen")
    }
}

