package com.kglabs28.yapnplay.appengine.games.citybaazar.domain

/** One seat at the table, with its live balance and position. [halfRent]: in jail, collecting half rent. */
data class CityBaazarPlayer(
    val seat: Int,
    val userId: String,
    val name: String,
    val level: Int,
    val colour: CityBaazarColour,
    val isMe: Boolean,
    val micOn: Boolean,
    val autoplay: Boolean,
    val presence: CityBaazarPresence = CityBaazarPresence.PLAYING,
    val cash: Int = 0,
    val position: Int = 0,
    val inJail: Boolean = false,
    val halfRent: Boolean = false,
    val bankrupt: Boolean = false,
)
