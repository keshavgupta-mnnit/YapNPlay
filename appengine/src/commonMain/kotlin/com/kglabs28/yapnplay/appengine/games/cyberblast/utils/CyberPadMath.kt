package com.kglabs28.yapnplay.appengine.games.cyberblast.utils

import androidx.compose.ui.geometry.Offset
import com.kglabs28.yapnplay.appengine.games.cyberblast.BlastDir
import kotlin.math.abs
import kotlin.math.hypot

/** The direction pad's geometry. */
object CyberPadMath {
    /**
     * The way a touch at [offset] from the pad's centre points; none inside [deadZone]. While
     * [current] is held it stays until the thumb leans clearly another way (that axis [KEEP] times
     * stronger), so a thumb resting near a diagonal never flips between two directions.
     */
    fun dirOf(offset: Offset, deadZone: Float, current: BlastDir? = null): BlastDir? {
        if (hypot(offset.x, offset.y) < deadZone) return null
        val strongest = if (abs(offset.x) > abs(offset.y)) {
            if (offset.x > 0) BlastDir.RIGHT else BlastDir.LEFT
        } else {
            if (offset.y > 0) BlastDir.DOWN else BlastDir.UP
        }
        if (current == null || current == strongest) return strongest
        val along = offset.x * current.dx + offset.y * current.dy
        val other = offset.x * strongest.dx + offset.y * strongest.dy
        return if (along > 0 && other < along * KEEP) current else strongest
    }

    private const val KEEP = 1.6f
}
