package noor.serry.rawaa.ui.screens.studentScreens.menu

sealed interface MenuEffect {
    data object NavigateToSettings : MenuEffect
    data object NavigateToHelpAndSupport : MenuEffect
    data object NavigateToPrivacyPolicy : MenuEffect
    data object NavigateToLogin : MenuEffect
}
