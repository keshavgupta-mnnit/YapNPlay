package com.kglabs28.yapnplay.appengine.core.mvi

import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource
import kotlin.time.TimeMark
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

    /** Actions still being handled, with when each started: the same one again waits a moment (no repeat taps). */
    private val running = mutableMapOf<I, Run>()

    /**
     * Single entry point for every user action. A tap that repeats an
     * action still running (a dialog opening, an ad loading) is dropped,
     * so mashing a button can't queue opens and closes. After [REPEAT_AFTER]
     * it goes through again, so a request stuck on a weak connection never
     * leaves a button dead for long.
     */
    fun onIntent(intent: I) {
        AppLog.d(AppConstants.LOG_TAG_INTENTS, "${this::class.simpleName} <- ${intent::class.simpleName} (scope active=${scope.isActive})")
        val started = running[intent]
        if (started != null && started.mark.elapsedNow() < REPEAT_AFTER) {
            return AppLog.d(AppConstants.LOG_TAG_INTENTS, "${intent::class.simpleName} already running: ignored")
        }
        val run = Run(TimeSource.Monotonic.markNow())
        running[intent] = run
        scope.launch {
            try {
                handleIntent(intent)
            } finally {
                // Only this run's own mark: a later repeat keeps its own.
                if (running[intent] === run) running.remove(intent)
            }
        }
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

    /** One handling of an action, and when it started. */
    private class Run(val mark: TimeMark)

    private companion object {
        /** How long a repeat of a still-running action is ignored. */
        val REPEAT_AFTER = 3.seconds
    }
}
