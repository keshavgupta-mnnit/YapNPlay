package com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleInkOp
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleStrokeData
import com.kglabs28.yapnplay.appengine.games.doodleking.identity.DoodleKingConstants
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * The turn's drawing on this phone. The drawer's touches draw here at once
 * and queue what to send ([takeOutgoing]); everyone else's board follows the
 * drawer's messages ([apply]). [revision] changes with every change: the
 * canvas reads it while drawing only, so drawing never recomposes the table.
 * Lines grow in place ([DoodleLine]): a new point costs the same however
 * long the line is.
 */
@Stable
class DoodleBoard {
    var revision by mutableIntStateOf(0)
        private set
    var drawingId: Int = 0
        private set
    private val lines = mutableListOf<DoodleLine>()
    private val outgoing = mutableListOf<DoodleInkOp>()
    /** The points of the line being drawn not yet queued to send. */
    private val unsent = mutableListOf<Int>()
    private var nextId = 1
    private var drawing = false

    /** The pen the user draws with (the drawer's tool bar sets it). */
    var colour: Int = 0
    var size: Int = 1
    var eraser: Boolean = false

    val strokes: List<DoodleLine> get() = lines
    val strokeCount: Int get() = lines.size

    /** A new turn's drawing: the canvas starts empty. */
    fun reset(id: Int) {
        drawingId = id
        lines.clear()
        outgoing.clear()
        unsent.clear()
        nextId = 1
        drawing = false
        revision++
    }

    // ── The user drawing (x, y from 0 to 1 across the canvas) ──

    fun start(x: Float, y: Float) {
        flushUnsent()
        val line = DoodleLine(nextId++, colour, size, eraser)
        line.points += scale(x)
        line.points += scale(y)
        lines += line
        unsent += line.points
        drawing = true
        revision++
    }

    fun extend(x: Float, y: Float) {
        val line = lines.lastOrNull()?.takeIf { drawing } ?: return
        val px = scale(x)
        val py = scale(y)
        // Points too close to the last one add nothing but data.
        val n = line.points.size
        if (n >= 2 && abs(line.points[n - 2] - px) + abs(line.points[n - 1] - py) < MIN_STEP) return
        line.points += px
        line.points += py
        unsent += px
        unsent += py
        revision++
    }

    fun finish() {
        drawing = false
    }

    fun undo() {
        if (lines.isEmpty()) return
        flushUnsent()
        lines.removeAt(lines.lastIndex)
        drawing = false
        outgoing += DoodleInkOp.Undo(drawingId)
        revision++
    }

    fun clear() {
        if (lines.isEmpty()) return
        flushUnsent()
        lines.clear()
        drawing = false
        outgoing += DoodleInkOp.Clear(drawingId)
        revision++
    }

    /** What to send since last time, oldest first (the line being drawn: its new points in one part). */
    fun takeOutgoing(): List<DoodleInkOp> {
        flushUnsent()
        return outgoing.toList().also { outgoing.clear() }
    }

    // ── The drawer's messages, on everyone else's phone ──

    fun apply(op: DoodleInkOp) {
        if (op.drawingId != drawingId) return
        when (op) {
            is DoodleInkOp.Part -> {
                val last = lines.lastOrNull()
                if (last != null && last.id == op.stroke.id) last.points += op.stroke.points else lines += DoodleLine.of(op.stroke)
            }
            is DoodleInkOp.Undo -> if (lines.isNotEmpty()) lines.removeAt(lines.lastIndex)
            is DoodleInkOp.Clear -> lines.clear()
            is DoodleInkOp.Whole -> {
                lines.clear()
                op.strokes.mapTo(lines, DoodleLine::of)
            }
        }
        revision++
    }

    /** The line being drawn's new points become one part to send. */
    private fun flushUnsent() {
        val line = lines.lastOrNull() ?: return
        if (unsent.isEmpty()) return
        outgoing += DoodleInkOp.Part(drawingId, DoodleStrokeData(line.id, line.colour, line.size, line.eraser, unsent.toList()))
        unsent.clear()
    }

    private fun scale(v: Float): Int = (v.coerceIn(0f, 1f) * DoodleKingConstants.CANVAS_MAX).roundToInt()

    private companion object {
        /** Canvas units (of 4095) a point must move to be kept. */
        const val MIN_STEP = 12
    }
}
