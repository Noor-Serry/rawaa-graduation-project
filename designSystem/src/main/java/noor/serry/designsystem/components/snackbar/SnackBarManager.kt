package noor.serry.designsystem.components.snackbar

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SnackBarManager{
    private val _snackBarFlow = MutableSharedFlow<SnackBarUiMessage>()
    val snackBarFlow = _snackBarFlow.asSharedFlow()

    suspend fun show(snackBarUiMessage : SnackBarUiMessage){
        _snackBarFlow.emit(snackBarUiMessage)
    }
}
