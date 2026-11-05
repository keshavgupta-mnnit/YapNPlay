package com.kglabs28.yapnplay.appengine.features.nearby.host

/** The current time in epoch milliseconds, for the phone hosting a Nearby game (injected so tests control time). */
fun interface HostClock {
    fun nowMillis(): Long
}
