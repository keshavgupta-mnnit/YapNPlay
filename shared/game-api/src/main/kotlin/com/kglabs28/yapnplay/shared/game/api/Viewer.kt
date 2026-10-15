package com.kglabs28.yapnplay.shared.game.api

/**
 * Who is viewing the match: a player with a seat, or a spectator.
 */
sealed interface Viewer {
    data class Player(val seat: SeatNumber) : Viewer
    data object Spectator : Viewer
}
