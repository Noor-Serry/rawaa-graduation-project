package noor.serry.rawaa.ui.screens.login

sealed interface LoginEffect {
    data object NavigateToStudentHome     : LoginEffect
    data object NavigateToTeacherHome     : LoginEffect
    data object NavigateToRegister        : LoginEffect
    data object NavigateToForgotPassword  : LoginEffect
}
