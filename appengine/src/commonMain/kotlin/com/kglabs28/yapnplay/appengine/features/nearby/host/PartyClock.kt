package com.kglabs28.yapnplay.appengine.features.nearby.host

import com.kglabs28.yapnplay.appengine.core.common.AppClock

/**
 * The hosting phone's clock. A phone that takes a party over mid-game
 * runs on the old host's timeline ([offsetMs], measured as the game's
 * copies arrived), so every deadline in the copied game stays right even
 * though the two phones' clocks differ.
 */
class PartyClock(private val base: AppClock) : HostClock {
    var offsetMs: Long = 0L

    override fun nowMillis(): Long = phoneNow() + offsetMs

    /** This phone's own time, without any offset. */
    fun phoneNow(): Long = base.now().toEpochMilliseconds()
}
