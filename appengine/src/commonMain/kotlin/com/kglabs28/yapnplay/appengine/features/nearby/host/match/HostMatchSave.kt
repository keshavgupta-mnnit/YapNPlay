package com.kglabs28.yapnplay.appengine.features.nearby.host.match

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * A running Nearby match, whole, as the next host keeps it: if the host's
 * phone goes, that phone carries on with it from here. Times are on the
 * host's timeline (see PartyClock).
 */
@Serializable
data class HostMatchSave(
    val matchId: String,
    val gameId: String,
    val roomId: String,
    val title: String,
    val startedAt: Long,
    val seats: List<HostMatchSeat>,
    /** Watchers: user id → name, avatar. */
    val spectators: Map<String, List<String>>,
    val version: Long,
    val deadline: Long?,
    val pauseMs: Long,
    val botWakeAt: Long?,
    val turnOwner: Int,
    val extensionVote: HostVoteSave?,
    val endVote: HostVoteSave?,
    val lastEndProposal: Map<Int, Long>,
    val waitingFor: Set<String>,
    val startHoldUntil: Long?,
    /** The game itself (GameSession.save). */
    val session: JsonObject,
)

/** A vote in progress (end the game, or keep playing). */
@Serializable
data class HostVoteSave(
    val voters: Set<Int>,
    val endsAt: Long,
    val yes: Set<Int>,
    val no: Set<Int>,
    val proposerSeat: Int = 0,
    val startedAt: Long = 0L,
)
