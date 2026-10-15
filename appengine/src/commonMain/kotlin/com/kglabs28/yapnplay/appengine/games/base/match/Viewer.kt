package com.kglabs28.yapnplay.appengine.games.base.match

import com.kglabs28.yapnplay.appengine.games.base.identity.SeatNumber

/**
 * Who is viewing the match: a player with a seat, or a spectator.
 */
sealed interface Viewer {
    data class Player(val seat: SeatNumber) : Viewer
    data object Spectator : Viewer
}
