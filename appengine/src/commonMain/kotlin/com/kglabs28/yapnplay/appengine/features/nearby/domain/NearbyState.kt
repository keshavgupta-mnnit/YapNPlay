package com.kglabs28.yapnplay.appengine.features.nearby.domain

import kotlinx.serialization.Serializable

/**
 * Nearby play as this phone sees it: whether it hosts a party or is a
 * guest in one, the phones around that can be invited (host), an invite
 * waiting for an answer (anyone available), and the last problem.
 */
data class NearbyState(
    val role: NearbyRole = NearbyRole.IDLE,
    /** The party's room id while hosting or a guest; null otherwise. */
    val roomId: String? = null,
    /** Phones around, free to be invited (the party's Add Friends list; guests get it from the host). */
    val peers: List<NearbyPeer> = emptyList(),
    /** Someone nearby invited this phone; null when nothing waits. */
    val incoming: NearbyInvite? = null,
    val problem: NearbyProblem? = null,
)

/** What this phone does in Nearby play. */
enum class NearbyRole {
    /** Not in a party (it may still be findable, so it can be invited). */
    IDLE,
    /** Hosting a party: its games run on this phone. */
    HOSTING,
    /** Accepted an invite; waiting for the host to let it in. */
    JOINING,
    /** In someone else's party. */
    GUEST,
}

/** A phone around, as the party's invite list shows it (the host shares it with its guests). */
@Serializable
data class NearbyPeer(
    val endpointId: String,
    val player: NearbyPlayer,
    val invite: PeerInvite = PeerInvite.NONE,
)

/** Where the host's invite to one phone stands. */
@Serializable
enum class PeerInvite { NONE, INVITING, INVITED, JOINED, DECLINED }

/** An invite from a phone nearby to its party. */
data class NearbyInvite(
    val endpointId: String,
    val roomId: String,
    val roomName: String,
    val from: NearbyPlayer,
)

/** Why Nearby play stopped or could not start. */
enum class NearbyProblem {
    /** The host could not be reached, or the party is full. */
    JOIN_FAILED,
    /** The host ended the party or went out of range. */
    HOST_LEFT,
    /** The other phone has a different app version. */
    VERSION_MISMATCH,
}
