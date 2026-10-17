package com.kglabs28.yapnplay.appengine.features.seathandover.presentation

/** One tile in a seat picker: a spectator, or the bot ([isBot]). */
data class SeatPickOptionUi(val label: String, val isBot: Boolean)
