package com.kglabs28.yapnplay.appengine.core.designsystem

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

    /**
     * The tighter of width and height against the reference screen, as the
     * window really is: the design frame is fitted the same way, so even a
     * window turned upright (should Android ever force it) shows the whole
     * design, smaller, instead of spilling off the sides.
     */
    fun resolveMultiplier(windowWidthDp: Int, windowHeightDp: Int): Float {
        if (windowWidthDp <= 0 || windowHeightDp <= 0) return 1f
        return min(windowWidthDp / REFERENCE_WIDTH_DP, windowHeightDp / REFERENCE_HEIGHT_DP).coerceIn(MIN_MULTIPLIER, MAX_MULTIPLIER)
    }

    /**
     * The screen the design's numbers are drawn for (landscape, dp). Set so
     * a 6.7" phone (914 × 411) shows exactly the look of the Play Store
     * release (1.05 there); the most used phone in India, 800 × 360
     * (Statcounter, mid-2026), gets the same layout 8% smaller, tablets and
     * bigger phones the same layout bigger.
     */
    const val REFERENCE_WIDTH_DP = 870f
    const val REFERENCE_HEIGHT_DP = 391f
    private const val MIN_MULTIPLIER = 0.3f
    private const val MAX_MULTIPLIER = 2f

    /** Tests only: pin a multiplier without a window. */
    fun overrideForTests(value: Float) {
        multiplier = value
    }
}
