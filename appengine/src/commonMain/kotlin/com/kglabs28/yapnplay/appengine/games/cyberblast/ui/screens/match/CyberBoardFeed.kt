package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.screens.match

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableIntStateOf
import com.kglabs28.yapnplay.appengine.games.cyberblast.BLAST_TILE
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberBody
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberFrameData
import kotlin.math.abs
import kotlin.time.TimeSource

/** A short effect on the board: a blocker shattering or a bomb's flash at [tile], since [atMs]. */
data class CyberBurst(val tile: Int, val atMs: Long, val boom: Boolean)

/** Points floating up from [seat]'s player since [atMs] ([x], [y] in tiles). */
data class CyberPop(val seat: Int, val points: Int, val x: Float, val y: Float, val atMs: Long)

/**
 * The live board the canvas draws, kept outside the screen state so 20
 * frames a second never recompose anything: the canvas reads [version]
 * while drawing. Players glide between the last two frames (one frame
 * behind, so movement looks smooth whatever the network does).
 */
@Stable
class CyberBoardFeed {
    private val clock = TimeSource.Monotonic.markNow()
    /** Bumps with every frame: the canvas redraws. */
    val version = mutableIntStateOf(0)
    var cols: Int = DEFAULT_COLS
        private set
    var rows: Int = DEFAULT_ROWS
        private set
    var frameMs: Long = DEFAULT_FRAME_MS
        private set
    var crates: Set<Int> = emptySet()
        private set
    var walls: Set<Int> = emptySet()
        private set
    var current: CyberFrameData? = null
        private set
    private var previous: CyberFrameData? = null
    /** When [current] came in ([nowMs]'s clock). */
    var currentAt: Long = 0L
        private set
    val bursts = ArrayDeque<CyberBurst>()
    val pops = ArrayDeque<CyberPop>()

    fun nowMs(): Long = clock.elapsedNow().inWholeMilliseconds

    fun setUp(cols: Int, rows: Int, frameMs: Long) {
        this.cols = cols
        this.rows = rows
        this.frameMs = frameMs.coerceAtLeast(1)
    }

    /** A new frame; one older than the one shown is dropped. [whole]: a snapshot's, always taken. */
    fun apply(frame: CyberFrameData, whole: Boolean = false) {
        val shown = current
        if (!whole && shown != null && frame.number <= shown.number) return
        val now = nowMs()
        frame.crates?.let { crates = it }
        frame.walls?.let { walls = it }
        frame.broken.forEach { bursts.addLast(CyberBurst(it, now, boom = false)) }
        frame.booms.forEach { bursts.addLast(CyberBurst(it, now, boom = true)) }
        frame.hits.forEach { hit ->
            // Where the one hit stood, and the points over the bomb's owner.
            shown?.body(hit.victim)?.let { pops.addLast(CyberPop(hit.victim, hit.victimPoints, tiles(it.x), tiles(it.y), now)) }
            if (hit.byPoints != 0) frame.body(hit.by)?.let { pops.addLast(CyberPop(hit.by, hit.byPoints, tiles(it.x), tiles(it.y), now)) }
        }
        while (bursts.isNotEmpty() && now - bursts.first().atMs > EFFECT_MS) bursts.removeFirst()
        while (pops.isNotEmpty() && now - pops.first().atMs > EFFECT_MS) pops.removeFirst()
        previous = if (whole) frame else shown
        current = frame
        currentAt = now
        version.intValue++
    }

    /** How far past [current] the board is drawn, 0–1 of a frame. */
    fun progress(now: Long): Float = ((now - currentAt).toFloat() / frameMs).coerceIn(0f, 1f)

    /** Where [body] stands now (tiles), gliding from the last frame; a jump (a comeback) snaps. */
    fun position(body: CyberBody, now: Long): Pair<Float, Float> {
        val before = previous?.body(body.seat)
        val t = progress(now)
        if (before == null || abs(before.x - body.x) + abs(before.y - body.y) > SNAP_DISTANCE) return tiles(body.x) to tiles(body.y)
        return tiles(before.x + ((body.x - before.x) * t).toInt()) to tiles(before.y + ((body.y - before.y) * t).toInt())
    }

    /** Time gone since [current] came in (to run fuses and fire down). */
    fun sinceFrame(now: Long): Long = (now - currentAt).coerceAtLeast(0)

    private fun tiles(milli: Int): Float = milli.toFloat() / BLAST_TILE

    companion object {
        const val DEFAULT_COLS = 15
        const val DEFAULT_ROWS = 11
        const val DEFAULT_FRAME_MS = 50L
        /** Bursts and floating points last this long (ms). */
        const val EFFECT_MS = 900L
        /** Moving more than this between two frames (thousandths of a tile) is a jump, not a walk. */
        const val SNAP_DISTANCE = 1_500
    }
}
