package com.kglabs28.yapnplay.appengine.core.mvi

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppLog
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MVI base view model. Plain class (no Android ViewModel) so it stays
 * multiplatform and unit-testable on plain JVM.
 *
 * - [state] is the single immutable screen state, exposed as StateFlow.
 * - Intents enter through [onIntent] only.
 * - [setState] computes the next state; keep the transform pure.
 * - [emitEffect] sends a one-off effect, delivered exactly once.
 */
abstract class MviViewModel<S : UiState, I : UiIntent, E : UiEffect>(
    initialState: S,
    dispatchers: DispatcherProvider,
) {
    protected val scope = CoroutineScope(SupervisorJob() + dispatchers.main)

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effects = Channel<E>(Channel.BUFFERED)
    val effects: Flow<E> = _effects.receiveAsFlow()

    /** Single entry point for every user action. */
    fun onIntent(intent: I) {
        AppLog.d(AppConstants.LOG_TAG_INTENTS, "${this::class.simpleName} <- ${intent::class.simpleName} (scope active=${scope.isActive})")
        scope.launch { handleIntent(intent) }
    }

    /** Subclasses interpret intents here. */
    protected abstract suspend fun handleIntent(intent: I)

    /** Replace state with [transform] applied to the current state. */
    protected fun setState(transform: S.() -> S) {
        _state.update(transform)
    }

    /** Send a one-off effect. */
    protected suspend fun emitEffect(effect: E) {
        _effects.send(effect)
    }

    /** Cancel coroutines when the host is destroyed. */
    fun clear() {
        onCleared()
        scope.cancel()
    }

    /** Release anything held outside [scope] (e.g. an open mic). */
    protected open fun onCleared() = Unit
}
