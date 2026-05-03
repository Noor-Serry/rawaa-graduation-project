package noor.serry.rawaa.ui.screens.register

sealed interface RegisterEffect {
    data object NavigateToLogin : RegisterEffect
    data object NavigateToHome : RegisterEffect
}
