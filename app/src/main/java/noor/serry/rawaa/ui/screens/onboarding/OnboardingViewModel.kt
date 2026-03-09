package noor.serry.rawaa.ui.screens.onboarding

import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider
    
class OnboardingViewModel (
    dispatcherProvider: DispatcherProvider,
    ) : BaseViewModel<OnboardingUiState, OnboardingEffect>(
    OnboardingUiState(),
    dispatcherProvider
    ), OnboardingInteractionListener {

    override fun onSkipClick() {
        sendNewNavigationEffect(OnboardingEffect.NavigateToLogin)
    }

    override fun onClickNext() {
    }

    override fun onClickStartNow() {
    }

    override fun onClickLogin() {
        sendNewNavigationEffect(OnboardingEffect.NavigateToLogin)
    }

}