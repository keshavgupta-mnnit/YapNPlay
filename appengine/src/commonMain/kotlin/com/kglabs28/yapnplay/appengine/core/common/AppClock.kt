package com.kglabs28.yapnplay.appengine.core.common

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Injected clock. Game and timer logic never reads the clock directly,
 * so tests control time.
 */
interface AppClock {
    fun now(): Instant
}

class SystemAppClock : AppClock {
    override fun now(): Instant = Clock.System.now()
}
