package com.kglabs28.yapnplay.appengine.features.matchhistory.domain

import com.kglabs28.yapnplay.appengine.features.matchresult.domain.SeatResult

/** A match the user played, as it ended: every seat in finishing order, and which one was theirs. */
data class PastMatch(
    val matchId: String,
    val gameId: String,
    /** Played in a room (false: Quick Play). */
    val roomMatch: Boolean,
    /** The room's name ("" for Quick Play, or a room since deleted). */
    val roomName: String,
    val startedAt: Long,
    val endedAt: Long,
    val mySeat: Int,
    val seats: List<PastSeat>,
) {
    val mine: PastSeat? get() = seats.firstOrNull { it.seat == mySeat }
}

/** One seat of a past match: who, their place, and what the result paid ([result]). */
data class PastSeat(
    val seat: Int,
    val name: String,
    val avatarId: String,
    val place: Int,
    val isCpu: Boolean,
    val result: SeatResult,
)
