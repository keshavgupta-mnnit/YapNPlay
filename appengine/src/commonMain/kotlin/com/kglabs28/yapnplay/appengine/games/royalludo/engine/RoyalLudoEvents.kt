package com.kglabs28.yapnplay.appengine.games.royalludo.engine

import com.kglabs28.yapnplay.appengine.games.base.play.GameEvent
import kotlinx.serialization.Serializable

@Serializable
data class DiceRolled(val seat: Int, val value: Int) : GameEvent
@Serializable
data class PieceMoved(val seat: Int, val piece: Int, val fromProgress: Int, val toProgress: Int, val squaresPassed: Int) : GameEvent
@Serializable
data class PieceCaptured(val seat: Int, val victim: Int, val victimPiece: Int, val reason: String) : GameEvent
@Serializable
data class PieceFinished(val seat: Int, val piece: Int) : GameEvent
@Serializable
data class ExtraTurn(val seat: Int, val reason: String) : GameEvent
@Serializable
data class TurnForfeited(val seat: Int, val reason: String) : GameEvent
@Serializable
data class TurnPassed(val seat: Int, val reason: String) : GameEvent
@Serializable
data class SeatRemoved(val seat: Int, val reason: String) : GameEvent
@Serializable
data class SeatFinished(val seat: Int, val place: Int) : GameEvent
@Serializable
data class MatchEnded(val ranking: List<Int>) : GameEvent
