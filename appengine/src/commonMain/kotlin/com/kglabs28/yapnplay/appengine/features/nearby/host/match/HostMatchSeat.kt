package com.kglabs28.yapnplay.appengine.features.nearby.host.match

import kotlinx.serialization.Serializable
import com.kglabs28.yapnplay.appengine.protocol.Colour
import com.kglabs28.yapnplay.appengine.protocol.SeatPresence

/**
 * One seat at a table the phone hosts. [userId] stays the owner's while the
 * bot holds the seat (away, dropped), so it is theirs again when they come
 * back; [left] means the owner gave it up.
 */
@Serializable
data class HostMatchSeat(
    val seat: Int,
    val userId: String,
    val displayName: String,
    val colour: Colour,
    val cpu: Boolean,
    val avatarId: String = "",
    val micOn: Boolean = false,
    val autoplay: Boolean = false,
    val presence: SeatPresence = SeatPresence.PLAYING,
    val connected: Boolean = true,
    val left: Boolean = false,
    /** When the connection dropped (epoch millis); the bot plays the seat after the grace. */
    val disconnectedAt: Long? = null,
    /** When the owner left (epoch millis): a leaver ranks last, the latest leaver first. */
    val leftAt: Long? = null,
    /** Already out of the game (finished, knocked out) when they left: their seat keeps their name. */
    val outBeforeLeaving: Boolean = false,
) {
    /** The host plays this seat right away instead of waiting for the turn timer. */
    val playedByHost: Boolean get() = cpu || left || autoplay || presence != SeatPresence.PLAYING
}
