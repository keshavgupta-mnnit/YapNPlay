package com.kglabs28.yapnplay.appengine.features.nearby.protocol

import com.kglabs28.yapnplay.appengine.features.nearby.host.match.HostMatchSave
import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyPeer
import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyPlayer
import com.kglabs28.yapnplay.appengine.protocol.transport.Envelope
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * What two phones in Nearby play say to each other. Only this app talks
 * this way (the backend never sees it). The lobby and the games travel as
 * the same [Envelope]s the backend uses, so they work offline unchanged.
 */
@Serializable
sealed interface NearbyFrame {
    /** Host → a phone around: come to my party. */
    @Serializable
    @SerialName("invite")
    data class Invite(val roomId: String, val roomName: String, val host: NearbyPlayer, val contract: String) : NearbyFrame

    /** Invitee → host: yes, with who I am. */
    @Serializable
    @SerialName("accept")
    data class Accept(val player: NearbyPlayer, val contract: String, val fitness: Int = 0) : NearbyFrame

    /** Guest → host, now and then: how well this phone could host (battery changes). */
    @Serializable
    @SerialName("fitness")
    data class Fitness(val score: Int) : NearbyFrame

    /** Invitee → host: no thanks. */
    @Serializable
    @SerialName("decline")
    data object Decline : NearbyFrame

    /** Host → invitee: you're in; open the lobby of [roomId]. */
    @Serializable
    @SerialName("welcome")
    data class Welcome(val roomId: String) : NearbyFrame

    /** A lobby or match message: a guest's request, or the host's reply or push. */
    @Serializable
    @SerialName("message")
    data class Message(val envelope: Envelope) : NearbyFrame

    /** Host → guests: the phones around it can invite, so anyone in the party can add them. */
    @Serializable
    @SerialName("peers")
    data class Peers(val peers: List<NearbyPeer>) : NearbyFrame

    /** Guest → host: please invite the phone [endpointId] from the shared list. */
    @Serializable
    @SerialName("ask_invite")
    data class AskInvite(val endpointId: String) : NearbyFrame

    /**
     * Host → guests: I'm leaving; [newHostId] hosts the party from now on (the one in
     * the lobby longest). They host [roomId] again with the same settings; the others
     * reconnect to them and are let straight back in.
     */
    @Serializable
    @SerialName("handover")
    data class Handover(
        val roomId: String,
        val roomName: String,
        val newHostId: String,
        /** Who takes over, in order ([newHostId] first): should two phones ever both host, the later one steps down. */
        val order: List<String> = emptyList(),
        val members: List<NearbyPlayer>,
        val selectedGame: String?,
        val houseRules: Map<String, Boolean>,
        val botFill: Boolean,
        /** Who is leaving (their seat at a running match is given up). */
        val fromHostId: String = "",
    ) : NearbyFrame

    /**
     * Host → guests, after each change: who would take over ([party]) and, for that
     * one phone only, the running match whole ([match]). [hostNow] is the host's clock
     * as it sent this, so the next host can run on the same timeline. If the host's
     * phone goes without a word (closed, out of range), the party goes on from here.
     */
    @Serializable
    @SerialName("shadow")
    data class Shadow(val party: Handover, val match: HostMatchSave?, val hostNow: Long) : NearbyFrame

    /** The host turned the guest away or ended the party. */
    @Serializable
    @SerialName("closed")
    data class Closed(val reason: CloseReason) : NearbyFrame
}

@Serializable
enum class CloseReason { ENDED, FULL, VERSION_MISMATCH }
