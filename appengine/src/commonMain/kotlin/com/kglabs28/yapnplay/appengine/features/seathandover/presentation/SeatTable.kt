package com.kglabs28.yapnplay.appengine.features.seathandover.presentation

import com.kglabs28.yapnplay.appengine.features.seathandover.domain.SeatOffer

/** What the seat dialogs need from any game's match (each game maps its own match to this). */
data class SeatTable(
    val offer: SeatOffer?,
    val viewerUserId: String,
    /** The user has a seat (false: watching). */
    val seated: Boolean,
    val roomMatch: Boolean,
    /** Who watches, in order: (user id, name). */
    val spectators: List<Pair<String, String>>,
    /** The user is the room's leader or admin: they may offer a seat the bot plays. */
    val canOfferSeats: Boolean = false,
    /** Seats the bot plays now (a CPU, or someone who left), by the name the table shows. */
    val botSeats: Map<Int, String> = emptyMap(),
) {
    val spectatorIds: List<String> get() = spectators.map { it.first }
    val spectatorNames: List<String> get() = spectators.map { it.second }
}
