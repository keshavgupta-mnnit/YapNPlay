package com.kglabs28.yapnplay.appengine.core.testing

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import kotlinx.datetime.Instant
import kotlin.time.Duration

/**
 * Controllable clock for tests. Time only moves when the test moves it.
 */
class TestAppClock(initial: Instant = Instant.fromEpochMilliseconds(1_700_000_000_000)) : AppClock {
    private var current = initial

    override fun now(): Instant = current

    fun advanceBy(duration: Duration) {
        current = current.plus(duration)
    }
}
