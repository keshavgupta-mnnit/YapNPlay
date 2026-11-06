package com.kglabs28.yapnplay.appengine.games.cyberblast.domain

import com.kglabs28.yapnplay.appengine.features.seathandover.domain.SeatOffer
import com.kglabs28.yapnplay.appengine.games.base.match.MatchEndVote
import kotlinx.datetime.Instant

/**
 * A Cyber Blast match as this user sees it: who sits where and the table's
 * votes and offers. The moving board itself comes in frames ([CyberFrameData]).
 */
data class CyberMatch(
    val matchId: String,
    val title: String,
    val roomId: String?,
    val seats: List<CyberSeat>,
    val spectators: List<CyberSpectator>,
    val cols: Int,
    val rows: Int,
    /** Hearts everyone starts with. */
    val lives: Int,
    /** How often frames come (ms). */
    val frameMs: Long,
    /** The room's Mini-map toggle. */
    val miniMap: Boolean = false,
    val startedAt: Instant,
    val viewerUserId: String,
    val seatOffer: SeatOffer? = null,
    val canOfferSeats: Boolean = false,
    val endVote: MatchEndVote? = null,
) {
    val me: CyberSeat? get() = seats.firstOrNull { it.isMe }
    val spectating: Boolean get() = me == null

    fun seat(seat: Int): CyberSeat? = seats.firstOrNull { it.seat == seat }
}

data class CyberSeat(
    val seat: Int,
    val userId: String,
    val name: String,
    val level: Int,
    val avatarId: String,
    val isMe: Boolean,
    val micOn: Boolean,
    val isCpu: Boolean,
    val away: Boolean,
    /** The bot holds the seat for someone who left (the leader may offer it to a spectator). */
    val botHeld: Boolean,
    val removed: Boolean,
)

data class CyberSpectator(val userId: String, val name: String, val micOn: Boolean, val avatarId: String)
