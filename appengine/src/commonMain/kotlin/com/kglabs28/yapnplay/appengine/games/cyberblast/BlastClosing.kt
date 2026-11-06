package com.kglabs28.yapnplay.appengine.games.cyberblast

import com.kglabs28.yapnplay.appengine.games.base.play.GameRandom

/**
 * Late in a match the arena closes in, so it always ends: one tile at a
 * time, round the outside and spiralling in, becomes a wall until only a
 * small core is left. Whatever is on that tile goes; a player standing there
 * loses a heart (no points) and comes back inside.
 */
object BlastClosing {

    /** Tiles in the order they close: the outer ring clockwise from the top left, then the next ring in. */
    val order: List<Int> = buildList {
        var left = 0
        var top = 0
        var right = BLAST_COLS - 1
        var bottom = BLAST_ROWS - 1
        val coreCol = BLAST_COLS / 2
        val coreRow = BLAST_ROWS / 2
        val inCore = { tile: Int ->
            kotlin.math.abs(BlastGrid.col(tile) - coreCol) <= BLAST_CORE_HALF_COLS && kotlin.math.abs(BlastGrid.row(tile) - coreRow) <= BLAST_CORE_HALF_ROWS
        }
        while (left <= right && top <= bottom) {
            val ring = buildList {
                for (c in left..right) add(BlastGrid.of(c, top))
                for (r in top + 1..bottom) add(BlastGrid.of(right, r))
                if (bottom > top) for (c in right - 1 downTo left) add(BlastGrid.of(c, bottom))
                if (right > left) for (r in bottom - 1 downTo top + 1) add(BlastGrid.of(left, r))
            }
            ring.filter { !BlastGrid.isPillar(it) && !inCore(it) && it !in this }.forEach(::add)
            left++
            top++
            right--
            bottom--
        }
    }

    fun update(state: CyberBlastState, random: GameRandom): Pair<CyberBlastState, List<BlastEvent>> {
        val rules = state.mandatory
        if (rules.closeAfterMs <= 0 || state.time < rules.closeAfterMs || state.closed >= order.size) return state to emptyList()
        if (state.nextCloseAt == 0L) return state.copy(nextCloseAt = state.time) to emptyList()
        if (state.time < state.nextCloseAt) return state to emptyList()
        val tile = order[state.closed]
        var s = state.copy(
            closed = state.closed + 1,
            nextCloseAt = state.time + rules.closeStepMs,
            walls = state.walls + tile,
            crates = state.crates - tile,
            bombs = state.bombs.filter { it.tile != tile },
            flames = state.flames.filter { it.tile != tile },
            items = state.items.filter { it.tile != tile },
        )
        val events = mutableListOf<BlastEvent>()
        s.alive.filter { it.tile == tile }.forEach { p ->
            val (next, hit) = BlastHits.hit(s, p.seat, p.seat, crushed = true, random)
            s = next
            events += hit
        }
        return s to events
    }

    /** How long until the arena starts closing (null: it never does, or it has started). */
    fun startsIn(state: CyberBlastState): Long? {
        val after = state.mandatory.closeAfterMs
        return if (after <= 0 || state.time >= after) null else after - state.time
    }
}
