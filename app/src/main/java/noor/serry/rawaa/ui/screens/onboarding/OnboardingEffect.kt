package noor.serry.rawaa.ui.screens.onboarding

sealed interface OnboardingEffect{
        data class ShowError(val message: String): OnboardingEffect
        object NavigateToLogin: OnboardingEffect
}