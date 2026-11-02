package com.kglabs28.yapnplay.appengine.ui.components.emojigift

import androidx.compose.ui.geometry.Offset
import kotlin.math.abs

/**
 * The arc an emoji flies along: a curve from [from] to [to] that bows
 * upward (a toss, not a straight line), and how big it is on the way.
 */
object EmojiGiftPath {

    /** The point [t] (0–1) of the way along the arc. */
    fun pointAt(from: Offset, to: Offset, t: Float): Offset {
        val control = control(from, to)
        val u = 1f - t
        return from * (u * u) + control * (2f * u * t) + to * (t * t)
    }

    /** Grows to [PEAK_SCALE] mid-flight, back to 1 as it lands. */
    fun scaleAt(t: Float): Float = 1f + (PEAK_SCALE - 1f) * (1f - abs(2f * t - 1f))

    /** The curve's pull point: above the middle, higher for a longer toss. */
    private fun control(from: Offset, to: Offset): Offset {
        val middle = (from + to) / 2f
        return middle - Offset(0f, (to - from).getDistance() * ARC)
    }

    private const val ARC = 0.35f
    private const val PEAK_SCALE = 1.6f
}
