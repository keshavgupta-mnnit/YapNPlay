package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.board

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoPieceUi
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.LudoBoardGeometry
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.PROGRESS_FINISHED
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.PROGRESS_IN_BASE

/** Where pieces stand on the board, and which one a tap hits. */
object LudoPieceLayout {

    /** Where a piece stands (canonical grid units) and how big it is drawn (1 = full size). */
    data class Placement(val position: Offset, val scale: Float)

    /**
     * Where every piece stands. Pieces sharing a track or lane square
     * shrink and spread out a little so every one stays visible and
     * tappable. Up to four sit in the square's corners; more go round a
     * ring, smaller, with the colours taking turns so every house shows.
     */
    fun targets(pieces: List<LudoPieceUi>): Map<String, Placement> {
        val raw = pieces.associate { it.id to LudoBoardGeometry.position(it.colour.ordinal, it.progress, it.piece) }
        val result = raw.mapValues { Placement(it.value, 1f) }.toMutableMap()
        pieces.filter { it.progress != PROGRESS_IN_BASE && it.progress < PROGRESS_FINISHED }
            .groupBy { raw.getValue(it.id) }
            .filterValues { it.size > 1 }
            .forEach { (cell, group) ->
                if (group.size <= STACK_OFFSETS.size) {
                    val scale = if (group.size == 2) PAIR_SCALE else CROWD_SCALE
                    group.forEachIndexed { index, piece ->
                        result[piece.id] = Placement(cell + STACK_OFFSETS[index] * scale, scale)
                    }
                } else {
                    val scale = (CROWD_SCALE * STACK_OFFSETS.size / group.size).coerceAtLeast(MIN_SCALE)
                    interleaveColours(group).forEachIndexed { index, piece ->
                        val angle = 2 * PI * index / group.size
                        result[piece.id] = Placement(cell + Offset(cos(angle).toFloat(), sin(angle).toFloat()) * RING_RADIUS, scale)
                    }
                }
            }
        return result
    }

    /** One piece of each colour in turn (red, green, …, red, green, …), so neighbours on the ring differ. */
    private fun interleaveColours(group: List<LudoPieceUi>): List<LudoPieceUi> {
        val byColour = group.groupBy { it.colour }.values.map { it.toMutableList() }
        val out = ArrayList<LudoPieceUi>(group.size)
        while (out.size < group.size) byColour.forEach { pieces -> pieces.removeFirstOrNull()?.let(out::add) }
        return out
    }

    /** The selectable piece closest to [tap] within reach, if any. */
    fun hitTest(
        tap: Offset,
        pieces: List<LudoPieceUi>,
        positionOf: (String) -> Offset,
        projection: LudoBoardProjection,
    ): LudoPieceUi? = pieces.filter { it.selectable }
        .map { piece ->
            val grid = positionOf(piece.id)
            val reach = projection.cellSize(grid) * TAP_REACH
            val centre = projection.project(grid) - Offset(0f, reach * TAP_RAISE)
            piece to (centre - tap).getDistance() / reach
        }
        .filter { it.second <= 1f }
        .minByOrNull { it.second }
        ?.first

    /** Back-to-front order, so nearer pieces cover farther ones. */
    fun drawOrder(pieces: List<LudoPieceUi>, positionOf: (String) -> Offset, projection: LudoBoardProjection) =
        pieces.sortedBy { projection.project(positionOf(it.id)).y }

    private const val SPREAD = 0.3f
    private const val PAIR_SCALE = 0.72f
    private const val CROWD_SCALE = 0.58f
    private const val MIN_SCALE = 0.4f
    private const val RING_RADIUS = 0.3f
    private const val TAP_REACH = 1.0f
    private const val TAP_RAISE = 0.5f
    private val STACK_OFFSETS = listOf(
        Offset(-SPREAD, -SPREAD), Offset(SPREAD, SPREAD), Offset(SPREAD, -SPREAD), Offset(-SPREAD, SPREAD),
    )
}
