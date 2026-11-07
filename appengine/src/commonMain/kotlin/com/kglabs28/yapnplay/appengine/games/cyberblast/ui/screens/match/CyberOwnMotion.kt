package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.screens.match

import com.kglabs28.yapnplay.appengine.games.base.identity.GameId
import com.kglabs28.yapnplay.appengine.games.base.identity.GameIds
import com.kglabs28.yapnplay.appengine.games.cyberblast.BlastBomb
import com.kglabs28.yapnplay.appengine.games.cyberblast.BlastDir
import com.kglabs28.yapnplay.appengine.games.cyberblast.BlastGrid
import com.kglabs28.yapnplay.appengine.games.cyberblast.BlastMovement
import com.kglabs28.yapnplay.appengine.games.cyberblast.BlastPlayer
import com.kglabs28.yapnplay.appengine.games.cyberblast.CyberBlastRules
import com.kglabs28.yapnplay.appengine.games.cyberblast.CyberBlastState
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberBody
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberBombData
import kotlin.math.abs

/**
 * The user's own character, moved on this phone the moment they push the
 * pad (the same walking rules as the server: corridors, corner sliding,
 * blockers and bombs), instead of waiting a round trip for each frame. It
 * eases back to where the server has them when they stop, and jumps there
 * when it drifts too far (blocked by something it didn't know yet, or a
 * comeback after a hit).
 */
class CyberOwnMotion {
    var speed: Int = DEFAULT_SPEED
    var boostSpeed: Int = DEFAULT_BOOST_SPEED
    /** The way the user is pushing right now (null: let go). */
    var held: BlastDir? = null
    private var x: Int? = null
    private var y: Int? = null
    private var lastAt = 0L
    private var board: CyberBlastState? = null

    /** The board as the last frame showed it: what blocks a walk. */
    fun updateBoard(cols: Int, rows: Int, crates: Set<Int>, walls: Set<Int>, bombs: List<CyberBombData>) {
        board = CyberBlastState(
            gameId = GameId(GameIds.CYBER_BLAST),
            blastRules = CyberBlastRules(),
            seatCount = 1,
            cols = cols,
            rows = rows,
            players = emptyList(),
            crates = crates,
            walls = walls,
            bombs = bombs.map { BlastBomb(it.id, it.tile, it.owner, it.radius, explodesAt = 0L) },
        )
    }

    /** Where to draw the user's character now (thousandths of a tile), [server] being the latest frame's. */
    fun position(server: CyberBody, now: Long, playing: Boolean): Pair<Int, Int> {
        val px = x
        val py = y
        val dt = (now - lastAt).coerceIn(0L, MAX_STEP_MS)
        lastAt = now
        if (px == null || py == null || abs(px - server.x) + abs(py - server.y) > SNAP_MILLI) return snap(server)
        var nx = px
        var ny = py
        val dir = held
        val state = board
        if (dir != null && playing && state != null) {
            val step = ((if (server.boostMs > 0) boostSpeed else speed) * dt / MILLIS).toInt()
            val walked = BlastMovement.walk(state, BlastPlayer(server.seat, nx, ny, lives = 1, radius = 1, held = dir), step)
            nx = walked.x
            ny = walked.y
        }
        // Standing still, or far off: ease towards the server; walking and close, the server catches up on its own.
        val off = abs(nx - server.x) + abs(ny - server.y)
        if (dir == null || off > DRIFT_MILLI) {
            nx += ((server.x - nx) * EASE).toInt()
            ny += ((server.y - ny) * EASE).toInt()
        }
        x = nx
        y = ny
        return nx to ny
    }

    /** The tile the user sees themselves on (a bomb goes there), null before the first frame. */
    fun tile(): Int? {
        val px = x ?: return null
        val py = y ?: return null
        return BlastGrid.tileAt(px, py)
    }

    /** Forgets the position (the user is out, or a new match). */
    fun reset() {
        x = null
        y = null
    }

    private fun snap(server: CyberBody): Pair<Int, Int> {
        x = server.x
        y = server.y
        return server.x to server.y
    }

    private companion object {
        const val DEFAULT_SPEED = 4_000
        const val DEFAULT_BOOST_SPEED = 5_600
        const val MILLIS = 1_000L
        /** One screen frame never moves more than this (ms): a pause doesn't fling the character. */
        const val MAX_STEP_MS = 60L
        /** Further than this from the server (thousandths of a tile): jump there. */
        const val SNAP_MILLI = 1_400
        /** Further than this while walking: start easing back. */
        const val DRIFT_MILLI = 700
        const val EASE = 0.15f
    }
}
