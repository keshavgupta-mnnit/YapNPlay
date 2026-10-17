package com.kglabs28.yapnplay.appengine.games.citybaazar.domain

/** One seat at the table, with its live balance and position. */
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
    val jailPasses: Int = 0,
    /** Failed rolls for doubles so far this stay. */
    val jailTries: Int = 0,
    val bankrupt: Boolean = false,
)
