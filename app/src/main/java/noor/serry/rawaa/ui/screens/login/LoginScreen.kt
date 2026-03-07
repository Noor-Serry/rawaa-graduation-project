package noor.serry.rawaa.ui.screens.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(viewModel: LoginViewModel) {

}

@Composable
private fun LoginContent(
    state: LoginUiState,
    interactionListener: LoginInteractionListener
) {

}

@Composable
private fun HandleEffects(effects: Flow<LoginEffect>) {
    LaunchedEffect(Unit) {
        effects.collectLatest { effect ->
            when(effect) {
                else -> {}
            }
        }
    }
}