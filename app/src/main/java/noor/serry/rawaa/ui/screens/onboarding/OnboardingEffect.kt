package noor.serry.rawaa.ui.screens.onboarding

sealed interface OnboardingEffect {
    data class ShowError(val message: String) : OnboardingEffect
    data object NavigateToLogin : OnboardingEffect
    data object NavigateToRegister : OnboardingEffect
}
