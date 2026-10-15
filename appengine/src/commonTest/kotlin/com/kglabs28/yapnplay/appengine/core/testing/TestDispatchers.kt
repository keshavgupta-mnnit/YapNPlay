package com.kglabs28.yapnplay.appengine.core.testing

import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Test dispatchers: real Unconfined everywhere, so launched work runs
 * eagerly on the calling thread. Every emission lands in Turbine's buffer
 * in order — deterministic, no virtual-time juggling.
 */
class TestDispatcherProvider : DispatcherProvider {
    override val main: CoroutineDispatcher = Dispatchers.Unconfined
    override val io: CoroutineDispatcher = Dispatchers.Unconfined
    override val default: CoroutineDispatcher = Dispatchers.Unconfined
    override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
}
