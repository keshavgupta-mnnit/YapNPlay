package com.kglabs28.yapnplay.appengine.features.nearby.host.match

import com.kglabs28.yapnplay.appengine.features.nearby.host.games.GameSession

/**
 * One hosted match's live state. Only [HostMatch] touches it, one request
 * or timer at a time, so it needs no locks of its own.
 */
class HostMatchState(
    val matchId: String,
    val gameId: String,
    /** The party room it belongs to: its table goes back to that lobby. */
    val roomId: String,
    val title: String,
    val session: GameSession,
    val seats: MutableList<HostMatchSeat>,
    val startedAt: Long,
) {
    /** Lobby members without a seat, watching the table: userId → name, avatar. */
    val spectators = LinkedHashMap<String, Pair<String, String>>()
    var version = 0L
    /** Turn deadline (epoch millis), or null before the first one. */
    var deadline: Long? = null
    /** How long the table animates the last play (and, on a new turn, the gap before it). */
    var pauseMs = 0L
    /** When the seat the host plays makes its move; null while a person holds the turn. */
    var botWakeAt: Long? = null
    var turnOwner = session.currentSeat
    var ended = false
    var extensionVote: HostExtensionVote? = null
    var endVote: HostEndVote? = null
    /** When each seat last asked to end the match (rate limit). */
    val lastEndProposal = mutableMapOf<Int, Long>()
    /** Humans who have not opened the table yet; play waits for them until [startHoldUntil]. */
    val waitingFor = mutableSetOf<String>()
    var startHoldUntil: Long? = null

    val onHold: Boolean get() = startHoldUntil != null || endVote != null

    fun seat(seat: Int): HostMatchSeat = seats.first { it.seat == seat }

    /** The seat [userId] plays, if they still hold one. */
    fun seatOf(userId: String): HostMatchSeat? = seats.firstOrNull { !it.cpu && !it.left && it.userId == userId }

    fun update(seat: Int, change: HostMatchSeat.() -> HostMatchSeat) {
        val index = seats.indexOfFirst { it.seat == seat }
        if (index >= 0) seats[index] = seats[index].change()
    }

    /** Everyone who receives snapshots: seated humans and spectators. */
    fun viewers(): Set<String> = seats.filter { !it.cpu && !it.left }.map { it.userId }.toSet() + spectators.keys

    /** Seats the host plays fully (City Bazaar's CPU bids). */
    fun hostSeats(): Set<Int> = seats.filter { it.playedByHost }.map { it.seat }.toSet()

    /** Seats with no human at them right now. They never vote. */
    fun botSeats(): Set<Int> = seats.filter { it.cpu || it.left || it.presence != com.kglabs28.yapnplay.appengine.protocol.SeatPresence.PLAYING }.map { it.seat }.toSet()
}
