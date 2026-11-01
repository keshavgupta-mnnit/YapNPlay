package com.kglabs28.yapnplay.appengine.games.doodleking.domain

/** A change to a turn's drawing, coming in from the drawer or going out from the user. */
sealed interface DoodleInkOp {
    val drawingId: Int

    /** More of stroke [stroke]'s points (its first part starts it). */
    data class Part(override val drawingId: Int, val stroke: DoodleStrokeData) : DoodleInkOp
    data class Undo(override val drawingId: Int) : DoodleInkOp
    data class Clear(override val drawingId: Int) : DoodleInkOp
    /** The whole drawing (after asking for it). */
    data class Whole(override val drawingId: Int, val strokes: List<DoodleStrokeData>) : DoodleInkOp
}
