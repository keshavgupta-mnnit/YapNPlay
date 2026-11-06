package com.kglabs28.yapnplay.appengine.games.cyberblast.utils

import androidx.compose.ui.unit.Dp

/** How small the right column's cards must be drawn so all of them fit the height they have, never overlapping. */
object CyberCardFit {
    /** [count] cards of [natural] height with [gap] between them in [available]: 1 when they fit, less when they must shrink. */
    fun scale(available: Dp, count: Int, natural: Dp, gap: Dp): Float {
        if (count <= 0) return 1f
        val room = (available - gap * (count - 1)) / count
        return (room / natural).coerceIn(MIN_SCALE, 1f)
    }

    /** Never smaller than this, so names stay readable (7 others on a short phone). */
    private const val MIN_SCALE = 0.3f
}
