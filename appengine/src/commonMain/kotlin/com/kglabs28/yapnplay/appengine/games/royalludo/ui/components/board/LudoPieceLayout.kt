package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.board

import androidx.compose.ui.geometry.Offset
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoPieceUi
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.LudoBoardGeometry
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.PROGRESS_FINISHED
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.PROGRESS_IN_BASE

/** Where pieces stand on the board, and which one a tap hits. */
object LudoPieceLayout {

    /**
     * Canonical grid position of every piece. Pieces sharing a track or
     * lane square spread out a little so each stays visible and tappable.
     */
    fun targets(pieces: List<LudoPieceUi>): Map<String, Offset> {
        val raw = pieces.associate { it.id to LudoBoardGeometry.position(it.colour.ordinal, it.progress, it.piece) }
        val result = raw.toMutableMap()
        pieces.filter { it.progress != PROGRESS_IN_BASE && it.progress < PROGRESS_FINISHED }
            .groupBy { raw.getValue(it.id) }
            .filterValues { it.size > 1 }
            .forEach { (cell, group) ->
                group.forEachIndexed { index, piece -> result[piece.id] = cell + STACK_OFFSETS[index % STACK_OFFSETS.size] }
            }
        return result
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

    private const val SPREAD = 0.2f
    private const val TAP_REACH = 0.85f
    private const val TAP_RAISE = 0.5f
    private val STACK_OFFSETS = listOf(
        Offset(-SPREAD, -SPREAD), Offset(SPREAD, SPREAD), Offset(SPREAD, -SPREAD), Offset(-SPREAD, SPREAD),
    )
}
