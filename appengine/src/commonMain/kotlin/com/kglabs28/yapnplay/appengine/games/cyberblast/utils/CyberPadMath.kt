package com.kglabs28.yapnplay.appengine.games.cyberblast.utils

import androidx.compose.ui.geometry.Offset
import com.kglabs28.yapnplay.appengine.games.cyberblast.BlastDir
import kotlin.math.abs
import kotlin.math.hypot

/** The direction pad's geometry. */
object CyberPadMath {
    /** The way a touch at [offset] from the pad's centre points (the longer axis wins); none inside [deadZone]. */
    fun dirOf(offset: Offset, deadZone: Float): BlastDir? {
        if (hypot(offset.x, offset.y) < deadZone) return null
        return if (abs(offset.x) > abs(offset.y)) {
            if (offset.x > 0) BlastDir.RIGHT else BlastDir.LEFT
        } else {
            if (offset.y > 0) BlastDir.DOWN else BlastDir.UP
        }
    }
}
