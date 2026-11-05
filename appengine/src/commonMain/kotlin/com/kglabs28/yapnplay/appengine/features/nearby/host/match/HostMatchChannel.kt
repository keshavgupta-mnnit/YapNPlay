package com.kglabs28.yapnplay.appengine.features.nearby.host.match

import com.kglabs28.yapnplay.appengine.features.nearby.host.HostEnvelopes
import com.kglabs28.yapnplay.appengine.features.nearby.host.HostRefusal
import com.kglabs28.yapnplay.appengine.protocol.CastEndVote
import com.kglabs28.yapnplay.appengine.protocol.ErrorCode
import com.kglabs28.yapnplay.appengine.protocol.LeaveSeat
import com.kglabs28.yapnplay.appengine.protocol.MatchRequestSnapshot
import com.kglabs28.yapnplay.appengine.protocol.MatchSnapshot
import com.kglabs28.yapnplay.appengine.protocol.ProposeEndMatch
import com.kglabs28.yapnplay.appengine.protocol.SetAutoplay
import com.kglabs28.yapnplay.appengine.protocol.SetMic
import com.kglabs28.yapnplay.appengine.protocol.SetPresence
import com.kglabs28.yapnplay.appengine.protocol.VoteExtension
import com.kglabs28.yapnplay.appengine.protocol.transport.Envelope
import com.kglabs28.yapnplay.appengine.protocol.transport.MessageType
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * The MATCH channel on the hosting phone, like the backend's handler:
 * every request names its match; the sender is the person whose phone
 * sent it. Answers with the reply envelope (Ack, a snapshot, or Rejected).
 */
class HostMatchChannel(private val envelopes: HostEnvelopes) {

    suspend fun handle(
        userId: String,
        displayName: String,
        avatarId: String,
        request: Envelope,
        matchOf: (String) -> HostMatch?,
    ): Envelope = try {
        when (request.type) {
            // The ping that times the round trip: any answer will do.
            MessageType.PING -> envelopes.ack(request)
            MessageType.MATCH_REQUEST_SNAPSHOT -> {
                val body = envelopes.payload(request, MatchRequestSnapshot.serializer())
                val snapshot = match(matchOf, body.matchId).snapshotFor(userId, displayName, avatarId)
                envelopes.reply(request, MessageType.MATCH_SNAPSHOT, MatchSnapshot.serializer(), snapshot)
            }
            MessageType.ROLL_DICE, MessageType.MOVE_PIECE, MessageType.GAME_ACTION -> {
                match(matchOf, matchIdOf(request)).play(userId, request.type, request.payload)
                envelopes.ack(request)
            }
            MessageType.DOODLE_STROKE, MessageType.DOODLE_UNDO, MessageType.DOODLE_CLEAR, MessageType.DOODLE_SYNC -> {
                match(matchOf, matchIdOf(request)).relay(userId, request.type, request.payload)
                envelopes.ack(request)
            }
            else -> {
                control(userId, request, matchOf)
                envelopes.ack(request)
            }
        }
    } catch (e: HostRefusal) {
        envelopes.rejected(request, e.code, e.message)
    } catch (e: IllegalArgumentException) {
        envelopes.rejected(request, ErrorCode.BAD_REQUEST, BAD_REQUEST)
    }

    private suspend fun control(userId: String, request: Envelope, matchOf: (String) -> HostMatch?) {
        when (request.type) {
            MessageType.SET_AUTOPLAY -> envelopes.payload(request, SetAutoplay.serializer()).let { match(matchOf, it.matchId).setAutoplay(userId, it.enabled) }
            MessageType.SET_MIC -> envelopes.payload(request, SetMic.serializer()).let { match(matchOf, it.matchId).setMic(userId, it.micOn) }
            MessageType.SET_PRESENCE -> envelopes.payload(request, SetPresence.serializer()).let { match(matchOf, it.matchId).setAway(userId, it.away) }
            MessageType.LEAVE_SEAT -> envelopes.payload(request, LeaveSeat.serializer()).let { match(matchOf, it.matchId).leave(userId) }
            MessageType.VOTE_EXTENSION -> envelopes.payload(request, VoteExtension.serializer()).let { match(matchOf, it.matchId).voteExtension(userId, it.keepPlaying) }
            MessageType.PROPOSE_END_MATCH -> envelopes.payload(request, ProposeEndMatch.serializer()).let { match(matchOf, it.matchId).proposeEnd(userId) }
            MessageType.CAST_END_VOTE -> envelopes.payload(request, CastEndVote.serializer()).let { match(matchOf, it.matchId).voteEnd(userId, it.agree) }
            // Nothing to confirm or mute offline: the table needs no answer but an Ack.
            MessageType.CONFIRM_PRESENCE, MessageType.SET_TALKING_MUTED -> Unit
            // Coins, emoji gifts and seat handover need the server.
            else -> throw HostRefusal(ErrorCode.BAD_REQUEST, NOT_OFFLINE)
        }
    }

    private fun match(matchOf: (String) -> HostMatch?, matchId: String): HostMatch =
        matchOf(matchId) ?: throw HostRefusal(ErrorCode.NOT_FOUND, NO_MATCH)

    private fun matchIdOf(request: Envelope): String =
        request.payload[MATCH_ID]?.jsonPrimitive?.contentOrNull ?: throw HostRefusal(ErrorCode.BAD_REQUEST, BAD_REQUEST)

    private companion object {
        const val MATCH_ID = "matchId"
        const val BAD_REQUEST = "The request could not be read."
        const val NO_MATCH = "No such match here."
        const val NOT_OFFLINE = "Not available when playing offline."
    }
}
