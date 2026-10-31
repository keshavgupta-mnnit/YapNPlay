package com.kglabs28.yapnplay.appengine.core.mvi

import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import kotlinx.coroutines.CancellationException

/**
 * Plays one update from the server at a table (its animations, sounds and
 * state). If any step of it fails, the failure is reported and [recover]
 * runs instead (jump straight to the update's state, nothing left marked
 * "animating"), so one bad update can never freeze a table: the dice, the
 * cards and the turn keep working without restarting the app. Every game
 * plays its updates through this.
 */
suspend fun <T> playTableUpdate(update: T, context: String, play: suspend (T) -> Unit, recover: suspend (T) -> Unit) {
    try {
        play(update)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        CrashReporter.record(e, context)
        recover(update)
    }
}
