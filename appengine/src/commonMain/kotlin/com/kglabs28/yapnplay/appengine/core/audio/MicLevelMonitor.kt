package com.kglabs28.yapnplay.appengine.core.audio

import kotlinx.coroutines.flow.StateFlow

/**
 * Listens to this device's microphone while started and says whether the
 * user is speaking (their voice is above the room's noise). Nothing is
 * recorded or sent anywhere; it only reads the level.
 */
interface MicLevelMonitor {
    val speaking: StateFlow<Boolean>

    /** Starts listening (needs the microphone permission; without it nothing happens). */
    fun start()

    /** Stops listening and waits until the mic is released (so the voice room can take it at once); [speaking] goes false. */
    suspend fun stop()
}
