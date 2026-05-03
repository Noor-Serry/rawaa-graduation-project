package noor.serry.rawaa.ui.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import noor.serry.rawaa.domain.exception.RawaaException
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

open class BaseViewModel<S, E>(
    initialState: S,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _effect = MutableSharedFlow<Pair<E, Boolean>>()
    val effect = _effect.asSharedFlow()
        .throttleFirstSelective(500) { it.second }
        .mapNotNull { it.first }

    private val _state: MutableStateFlow<S> = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()


    protected fun updateState(updater: (S) -> S) {
        viewModelScope.launch(dispatcherProvider.MainImmediate) {
            _state.update(updater)
        }
    }

    protected fun sendNewEffect(newEffect: E) {
        viewModelScope.launch(dispatcherProvider.MainImmediate) {
            _effect.emit(newEffect to false)
        }
    }

    protected fun sendNewNavigationEffect(navigationEffect: E) {
        viewModelScope.launch(dispatcherProvider.Default) {
            _effect.emit(navigationEffect to true)
        }
    }

    protected fun <T> tryToExecute(
        action: suspend () -> T,
        onSuccess: (T) -> Unit = {},
        onError: (RawaaException) -> Unit = {},
        onCompletion: () -> Unit = {},
        dispatcher: CoroutineDispatcher = dispatcherProvider.IO,
    ): Job {
        return viewModelScope.launch(dispatcher) {
            try {

                onSuccess(action())
            } catch (exception: RawaaException) {
                Log.e("BaseViewModel.kt",  "${exception.javaClass.simpleName}: ${exception.message}")
                onError(exception)
            } catch (exception: Exception) {
                Log.e("BaseViewModel.kt",  "${exception.javaClass.simpleName}: ${exception.message}")
                onError(RawaaException(exception.message ?: "Unknown error"))
            } finally {
                onCompletion()
            }
        }
    }
    @OptIn(ExperimentalTime::class)
    private fun <T> Flow<T>.throttleFirstSelective(
        periodMillis: Long,
        shouldThrottle: (T) -> Boolean
    ): Flow<T> {
        require(periodMillis > 0)
        return flow {
            var lastTime = 0L
            collect { value ->
                if (!shouldThrottle(value)) {
                    emit(value)
                } else {
                    val currentTime = Clock.System.now().toEpochMilliseconds()
                    if (currentTime - lastTime >= periodMillis) {
                        lastTime = currentTime
                        emit(value)
                    }
                }
            }
        }
    }
}