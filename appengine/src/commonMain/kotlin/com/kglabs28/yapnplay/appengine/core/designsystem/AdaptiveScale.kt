package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.min

/**
 * Single screen-size multiplier for the whole app. Every dimens, gap,
 * corner and font size multiplies through this at read time, so nothing
 * needs a tier check at the call site — read the token, get the scaled
 * value. Phones, foldables, tablets and landscape handsets all follow.
 *
 * Fed from the window size (see MainActivity); state-backed so a window
 * change recomposes with the new multiplier automatically.
 */
object AdaptiveScale {

    var multiplier: Float by mutableStateOf(1f)
        private set

    fun updateFor(windowWidthDp: Int, windowHeightDp: Int) {
        multiplier = resolveMultiplier(min(windowWidthDp, windowHeightDp))
    }

    fun resolveMultiplier(smallestWidthDp: Int): Float = when {
        smallestWidthDp >= 720 -> 1.35f
        smallestWidthDp >= 600 -> 1.25f
        smallestWidthDp >= 480 -> 1.15f
        smallestWidthDp >= 400 -> 1.05f
        smallestWidthDp >= 360 -> 1.0f
        else -> 0.9f
    }

    /** Tests only: pin a multiplier without a window. */
    fun overrideForTests(value: Float) {
        multiplier = value
    }
}

/** Scale any dp with the screen. Usage: `40.dp.scaled()`. */
fun Dp.scaled(): Dp = this * AdaptiveScale.multiplier

/** Scale any dp int with the screen. Usage: `40.scaledDp()`. */
fun Int.scaledDp(): Dp = this.dp * AdaptiveScale.multiplier

/** Scale any sp int with the screen. Usage: `16.scaledSp()`. */
val Int.scaledSp: TextUnit get() = (this * AdaptiveScale.multiplier).sp

/** Scale any sp float with the screen. */
val Float.scaledSp: TextUnit get() = (this * AdaptiveScale.multiplier).sp
