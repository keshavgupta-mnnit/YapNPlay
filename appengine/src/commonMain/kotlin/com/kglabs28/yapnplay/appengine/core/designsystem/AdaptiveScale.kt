package com.kglabs28.yapnplay.appengine.core.designsystem

import kotlin.math.max
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.math.min

/**
 * Single screen-size multiplier for the whole app. Every size, gap,
 * corner and font size in Dimens multiplies through this at read time, so
 * nothing needs a tier check at the call site — read the token, get the
 * scaled value. Phones, foldables, tablets and landscape handsets all follow.
 *
 * The design is drawn for one reference screen ([REFERENCE_WIDTH_DP] ×
 * [REFERENCE_HEIGHT_DP], landscape). Any other screen gets the design
 * grown or shrunk evenly by whichever side is tighter, so every screen
 * shows the same layout in the same proportions: nothing runs off a
 * narrow or short screen, nothing looks tiny on a big one.
 *
 * Fed from the window size (see MainActivity); state-backed so a window
 * change recomposes with the new multiplier automatically.
 */
object AdaptiveScale {

    var multiplier: Float by mutableStateOf(1f)
        private set

    fun updateFor(windowWidthDp: Int, windowHeightDp: Int) {
        multiplier = resolveMultiplier(windowWidthDp, windowHeightDp)
    }

    /** The tighter of width and height against the reference screen (landscape: the long side is the width). */
    fun resolveMultiplier(windowWidthDp: Int, windowHeightDp: Int): Float {
        if (windowWidthDp <= 0 || windowHeightDp <= 0) return 1f
        val long = max(windowWidthDp, windowHeightDp).toFloat()
        val short = min(windowWidthDp, windowHeightDp).toFloat()
        return min(long / REFERENCE_WIDTH_DP, short / REFERENCE_HEIGHT_DP).coerceIn(MIN_MULTIPLIER, MAX_MULTIPLIER)
    }

    /**
     * The screen the design is drawn for: 800 × 360, the most used phone
     * screen in India (Statcounter, mid-2026: 360 × 800 portrait, ~18% of
     * phones; next 393 × 873). Bigger phones and tablets grow from it,
     * smaller ones shrink.
     */
    const val REFERENCE_WIDTH_DP = 800f
    const val REFERENCE_HEIGHT_DP = 360f
    private const val MIN_MULTIPLIER = 0.3f
    private const val MAX_MULTIPLIER = 2f

    /** Tests only: pin a multiplier without a window. */
    fun overrideForTests(value: Float) {
        multiplier = value
    }
}
