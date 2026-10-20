package com.kglabs28.yapnplay.appengine.core.audio

import com.kglabs28.yapnplay.appengine.core.common.AppConstants

/**
 * The user's turn clock: a soft tick every second while it's their turn,
 * then a sharper double tick for the last [AppConstants.TURN_HURRY_SECONDS]
 * (hurry up). Call [update] on every table refresh.
 */
class TurnAlarm(private val play: (GameSound) -> Unit) {
    private var lastTick = -1L

    fun update(myTurn: Boolean, remainingMs: Long) {
        if (!myTurn || remainingMs <= 0L) {
            lastTick = -1L
            return
        }
        val second = (remainingMs + MILLIS - 1) / MILLIS
        if (second == lastTick) return
        lastTick = second
        play(if (second <= AppConstants.TURN_HURRY_SECONDS) GameSound.HURRY_TICK else GameSound.TICK)
    }

    private companion object {
        const val MILLIS = 1_000L
    }
}
