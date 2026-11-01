package com.kglabs28.yapnplay.appengine.games.doodleking.data.remote.wire

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Doodle King moves inside `GameAction.action`. */
@Serializable
sealed interface DoodleKingWireAction {
    @Serializable @SerialName("PickWord") data class PickWord(val index: Int) : DoodleKingWireAction
    @Serializable @SerialName("Guess") data class Guess(val text: String) : DoodleKingWireAction
    @Serializable @SerialName("PassTurn") data object Pass : DoodleKingWireAction
}

/** The drawing's messages the server forwards (`MatchRelay`), by `"type"`. */
@Serializable
sealed interface DoodleRelay

@Serializable
@SerialName("DoodleStroke")
data class DoodleStroke(
    val drawingId: Int,
    val id: Int,
    val colour: Int = 0,
    val size: Int = 1,
    val eraser: Boolean = false,
    val points: List<Int> = emptyList(),
) : DoodleRelay

@Serializable
@SerialName("DoodleUndo")
data class DoodleUndo(val drawingId: Int) : DoodleRelay

@Serializable
@SerialName("DoodleClear")
data class DoodleClear(val drawingId: Int) : DoodleRelay

@Serializable
@SerialName("DoodleDrawing")
data class DoodleDrawing(val drawingId: Int, val strokes: List<DoodleStroke>) : DoodleRelay

/** What the drawer sends: a stroke part, undo or clear, each with the match id. */
@Serializable
data class DoodleStrokeOut(
    val matchId: String,
    val drawingId: Int,
    val id: Int,
    val colour: Int,
    val size: Int,
    val eraser: Boolean,
    val points: List<Int>,
)

@Serializable
data class DoodleTurnOut(val matchId: String, val drawingId: Int)

/** Asks for the whole drawing. */
@Serializable
data class DoodleSyncOut(val matchId: String)

/** Doodle King's events in `MatchSnapshot.events`. */
@Serializable
sealed interface DoodleKingWireEvent {
    @Serializable @SerialName("DoodleGuessed") data class Guessed(val seat: Int, val points: Int) : DoodleKingWireEvent
    @Serializable @SerialName("DoodleRevealed") data class Revealed(val word: String, val points: Map<Int, Int>) : DoodleKingWireEvent
}
