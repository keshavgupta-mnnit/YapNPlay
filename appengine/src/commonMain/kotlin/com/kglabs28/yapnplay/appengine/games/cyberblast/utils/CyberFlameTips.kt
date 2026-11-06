package com.kglabs28.yapnplay.appengine.games.cyberblast.utils

import com.kglabs28.yapnplay.appengine.games.cyberblast.BlastDir
import com.kglabs28.yapnplay.appengine.games.cyberblast.BlastGrid
import com.kglabs28.yapnplay.appengine.games.cyberblast.FlameShape

/** Where a blast's arm ends (so the fire narrows to a point there), and steady noise for its sparks. */
object CyberFlameTips {
    /**
     * For an arm tile: −1 when the arm ends on its left / top side, +1 on its right / bottom,
     * 0 in the middle of an arm (fire on both sides) or for a blast's centre.
     */
    fun side(tile: Int, shape: FlameShape, burning: Set<Int>): Int {
        val (before, after) = when (shape) {
            FlameShape.HORIZONTAL -> BlastDir.LEFT to BlastDir.RIGHT
            FlameShape.VERTICAL -> BlastDir.UP to BlastDir.DOWN
            FlameShape.CENTRE -> return 0
        }
        val hasBefore = beside(tile, before) in burning
        val hasAfter = beside(tile, after) in burning
        return when {
            hasBefore && !hasAfter -> 1
            hasAfter && !hasBefore -> -1
            else -> 0
        }
    }

    private fun beside(tile: Int, dir: BlastDir): Int = BlastGrid.of(BlastGrid.col(tile) + dir.dx, BlastGrid.row(tile) + dir.dy)

    /** A steady pseudo-random 0–1 for [tile] and [step] (sparks that jump about every step). */
    fun noise(tile: Int, step: Long, salt: Int): Float {
        var h = tile * PRIME_A + step.toInt() * PRIME_B + salt * PRIME_C
        h = h xor (h ushr SHIFT_A)
        h *= PRIME_D
        h = h xor (h ushr SHIFT_B)
        return (h and MASK) / MASK.toFloat()
    }

    private const val PRIME_A = 73_856_093
    private const val PRIME_B = 19_349_663
    private const val PRIME_C = 83_492_791
    private const val PRIME_D = 0x45d9f3b
    private const val SHIFT_A = 16
    private const val SHIFT_B = 13
    private const val MASK = 0xFFFF
}
