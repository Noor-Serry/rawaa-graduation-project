package noor.serry.rawaa.ui.screens.onboarding

import noor.serry.rawaa.data.local.TokenDataStore
import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider

class OnboardingViewModel(
    dispatchers: DispatcherProvider,
    private val tokenDataStore: TokenDataStore,
) : BaseViewModel<OnboardingUiState, OnboardingEffect>(
    initialState = OnboardingUiState(),
    dispatcherProvider = dispatchers,
), OnboardingInteractionListener {

    init {
        tryToExecute(
            action = { tokenDataStore.markOnboardingSeen() }
        )
    }

    override fun onSkipClick() {
        sendNewNavigationEffect(OnboardingEffect.NavigateToRegister)
    }

    override fun onClickStartNow() {
        sendNewNavigationEffect(OnboardingEffect.NavigateToRegister)
    }

    override fun onClickLogin() {
        sendNewNavigationEffect(OnboardingEffect.NavigateToLogin)
    }
}
