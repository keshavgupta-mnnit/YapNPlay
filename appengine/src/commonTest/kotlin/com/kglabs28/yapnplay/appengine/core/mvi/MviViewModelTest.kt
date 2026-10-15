package com.kglabs28.yapnplay.appengine.core.mvi

import app.cash.turbine.test
import com.kglabs28.yapnplay.appengine.core.testing.TestDispatcherProvider
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

private data class CounterState(val count: Int = 0) : UiState
private sealed interface CounterIntent : UiIntent {
    data object Increment : CounterIntent
    data object Finish : CounterIntent
}
private sealed interface CounterEffect : UiEffect {
    data object Done : CounterEffect
}

private class CounterViewModel :
    MviViewModel<CounterState, CounterIntent, CounterEffect>(CounterState(), TestDispatcherProvider()) {
    override suspend fun handleIntent(intent: CounterIntent) {
        when (intent) {
            CounterIntent.Increment -> setState { copy(count = count + 1) }
            CounterIntent.Finish -> emitEffect(CounterEffect.Done)
        }
    }
}

/**
 * Proves the MVI base once, so feature tests only cover their own logic:
 * intents produce states, effects arrive exactly once.
 */
class MviViewModelTest {

    @Test
    fun `intents produce expected states`() = runTest {
        val vm = CounterViewModel()
        vm.state.test {
            assertEquals(CounterState(0), awaitItem())
            vm.onIntent(CounterIntent.Increment)
            assertEquals(CounterState(1), awaitItem())
            vm.onIntent(CounterIntent.Increment)
            assertEquals(CounterState(2), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        vm.clear()
    }

    @Test
    fun `effect is delivered exactly once`() = runTest {
        val vm = CounterViewModel()
        vm.effects.test {
            vm.onIntent(CounterIntent.Finish)
            assertEquals(CounterEffect.Done, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        vm.clear()
    }
}
