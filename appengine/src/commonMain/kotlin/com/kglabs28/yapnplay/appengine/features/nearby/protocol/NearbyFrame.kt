package com.kglabs28.yapnplay.appengine.features.nearby.protocol

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
    data class Accept(val player: NearbyPlayer, val contract: String) : NearbyFrame

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

    /** The host turned the guest away or ended the party. */
    @Serializable
    @SerialName("closed")
    data class Closed(val reason: CloseReason) : NearbyFrame
}

@Serializable
enum class CloseReason { ENDED, FULL, VERSION_MISMATCH }
