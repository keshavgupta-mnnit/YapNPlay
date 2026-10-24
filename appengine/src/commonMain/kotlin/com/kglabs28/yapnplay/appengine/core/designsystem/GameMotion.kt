package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.ui.MotionDurationScale
import kotlinx.coroutines.withContext

/**
 * Game moves (the dice roll, tokens and pieces walking) always play at
 * full length, even when the phone has animations turned off or sped up
 * (Developer options, or Accessibility > Remove animations): there the
 * system would skip them and the player would only see the result.
 */
suspend fun <T> atGameSpeed(block: suspend () -> T): T = withContext(GameSpeed) { block() }

private object GameSpeed : MotionDurationScale {
    override val scaleFactor: Float = 1f
}
