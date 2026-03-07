package noor.serry.rawaa.ui.screens.login

import noor.serry.rawaa.ui.base.BaseViewModel
import noor.serry.rawaa.ui.base.DispatcherProvider
    
class LoginViewModel (
    dispatcherProvider: DispatcherProvider,
    ) : BaseViewModel<LoginUiState, LoginEffect>(
    LoginUiState(),
    dispatcherProvider
    ), LoginInteractionListener {
           
}