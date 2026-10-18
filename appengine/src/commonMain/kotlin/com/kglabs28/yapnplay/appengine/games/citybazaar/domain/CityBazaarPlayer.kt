package com.kglabs28.yapnplay.appengine.games.citybazaar.domain

/** One seat at the table, with its live balance and position. [halfRent]: in jail, collecting half rent. */
data class CityBazaarPlayer(
    val seat: Int,
    val userId: String,
    val name: String,
    val level: Int,
    val colour: CityBazaarColour,
    val isMe: Boolean,
    val micOn: Boolean,
    val autoplay: Boolean,
    val presence: CityBazaarPresence = CityBazaarPresence.PLAYING,
    val cash: Int = 0,
    val position: Int = 0,
    val inJail: Boolean = false,
    val halfRent: Boolean = false,
    val bankrupt: Boolean = false,
    val avatarId: String = "",
    /** A CPU filling an empty Quick Play seat (no level shown). */
    val isCpu: Boolean = false,
)
