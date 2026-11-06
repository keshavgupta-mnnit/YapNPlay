package com.kglabs28.yapnplay.appengine.games.cyberblast

import kotlin.math.abs

/** What a blast from one tile reaches: the fire tiles (with their shape), the blockers it breaks and the bombs it sets off. */
data class BlastReach(val fire: List<Pair<Int, FlameShape>>, val crates: Set<Int>, val bombs: Set<Int>)

/**
 * The board's geometry: tiles, pillars, what blocks a walk or a blast.
 * Pure functions of a [CyberBlastState].
 */
object BlastGrid {
    const val TILES = BLAST_COLS * BLAST_ROWS

    fun tileAt(x: Int, y: Int): Int = (y / BLAST_TILE) * BLAST_COLS + x / BLAST_TILE
    fun col(tile: Int): Int = tile % BLAST_COLS
    fun row(tile: Int): Int = tile / BLAST_COLS
    fun of(col: Int, row: Int): Int = row * BLAST_COLS + col
    fun centreX(tile: Int): Int = col(tile) * BLAST_TILE + BLAST_HALF_TILE
    fun centreY(tile: Int): Int = row(tile) * BLAST_TILE + BLAST_HALF_TILE

    /** Metal pillars stand on every odd column of every odd row; they never break. */
    fun isPillar(tile: Int): Boolean = col(tile) % 2 == 1 && row(tile) % 2 == 1

    fun inside(col: Int, row: Int): Boolean = col in 0 until BLAST_COLS && row in 0 until BLAST_ROWS

    /** The tile next to [tile] towards [dir], or null past the edge. */
    fun next(tile: Int, dir: BlastDir): Int? {
        val c = col(tile) + dir.dx
        val r = row(tile) + dir.dy
        return if (inside(c, r)) of(c, r) else null
    }

    fun neighbours(tile: Int): List<Int> = BlastDir.entries.mapNotNull { next(tile, it) }

    fun distance(a: Int, b: Int): Int = abs(col(a) - col(b)) + abs(row(a) - row(b))

    /** A pillar or a closed-in wall: nothing passes, nothing breaks it. */
    fun isWall(state: CyberBlastState, tile: Int): Boolean = isPillar(tile) || tile in state.walls

    /** Blocks walking and stops a blast: a wall or a blocker. */
    fun isSolid(state: CyberBlastState, tile: Int): Boolean = isWall(state, tile) || tile in state.crates

    /** [seat] may walk onto [tile]: nothing solid, and no bomb unless they're still on it (just dropped it). */
    fun canEnter(state: CyberBlastState, tile: Int, from: Int): Boolean =
        !isSolid(state, tile) && (tile == from || state.bombAt(tile) == null)

    /** Free floor: no wall, blocker or bomb. */
    fun isOpen(state: CyberBlastState, tile: Int): Boolean = !isSolid(state, tile) && state.bombAt(tile) == null

    /**
     * Fire from [tile] [radius] tiles each way: walls stop it before, a
     * blocker or a bomb stops it on that tile (the blocker breaks, the bomb
     * goes off).
     */
    fun reach(state: CyberBlastState, tile: Int, radius: Int): BlastReach {
        val fire = mutableListOf(tile to FlameShape.CENTRE)
        val crates = mutableSetOf<Int>()
        val bombs = mutableSetOf<Int>()
        for (dir in BlastDir.entries) {
            var at = tile
            for (step in 1..radius) {
                at = next(at, dir) ?: break
                if (isWall(state, at)) break
                fire += at to if (dir.horizontal) FlameShape.HORIZONTAL else FlameShape.VERTICAL
                if (at in state.crates) {
                    crates += at
                    break
                }
                if (state.bombAt(at) != null) {
                    bombs += at
                    break
                }
            }
        }
        return BlastReach(fire, crates, bombs)
    }
}
