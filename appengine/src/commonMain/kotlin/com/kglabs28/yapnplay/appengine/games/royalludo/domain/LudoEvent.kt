package com.kglabs28.yapnplay.appengine.games.royalludo.domain

/** What just happened, so the screen can animate it in order. */
sealed interface LudoEvent {
    data class Rolled(val seat: Int, val value: Int) : LudoEvent
    data class Moved(val seat: Int, val piece: Int, val from: Int, val to: Int) : LudoEvent
    data class Captured(val seat: Int, val victimSeat: Int, val victimPiece: Int) : LudoEvent
    data class TurnPassed(val seat: Int) : LudoEvent
    /** Third six: every piece goes back to [pieces] (seat -> progress). */
    data class Reverted(val seat: Int, val pieces: Map<Int, List<Int>>) : LudoEvent
}
