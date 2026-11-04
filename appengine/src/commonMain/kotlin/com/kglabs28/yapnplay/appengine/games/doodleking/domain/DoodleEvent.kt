package com.kglabs28.yapnplay.appengine.games.doodleking.domain

/** Something that just happened (for the table's cheer and the reveal card). */
sealed interface DoodleEvent {
    data class Guessed(val seat: Int, val points: Int) : DoodleEvent
    data class Revealed(val word: String, val points: Map<Int, Int>) : DoodleEvent
}
