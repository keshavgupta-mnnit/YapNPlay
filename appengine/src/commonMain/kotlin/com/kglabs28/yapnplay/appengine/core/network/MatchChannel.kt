package com.kglabs28.yapnplay.appengine.core.network

import com.kglabs28.yapnplay.appengine.protocol.CastEndVote
import com.kglabs28.yapnplay.appengine.protocol.ProposeEndMatch
import com.kglabs28.yapnplay.appengine.protocol.MatchEnded
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.protocol.AnswerSeatOffer
import com.kglabs28.yapnplay.appengine.protocol.ChooseSeatTaker
import com.kglabs28.yapnplay.appengine.protocol.ConfirmPresence
import com.kglabs28.yapnplay.appengine.protocol.ErrorCode
import com.kglabs28.yapnplay.appengine.protocol.GameAction
import com.kglabs28.yapnplay.appengine.protocol.LeaveSeat
import com.kglabs28.yapnplay.appengine.protocol.MatchRequestSnapshot
import com.kglabs28.yapnplay.appengine.protocol.MatchSnapshot
import com.kglabs28.yapnplay.appengine.protocol.SetAutoplay
import com.kglabs28.yapnplay.appengine.protocol.VoteExtension
import com.kglabs28.yapnplay.appengine.protocol.SetMic
import com.kglabs28.yapnplay.appengine.protocol.SetPresence
import com.kglabs28.yapnplay.appengine.protocol.TakeSeat
import com.kglabs28.yapnplay.appengine.protocol.transport.Channel
import com.kglabs28.yapnplay.appengine.protocol.transport.MessageType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonObject

/**
 * The MATCH channel, shared by every game: the snapshot stream of one
 * match and the requests every game has (autoplay, mic, presence, seat
 * handover). Game moves go through [request] with the game's own types.
 */
class MatchChannel(private val realtime: RealtimeConnection) {

    /**
     * Every `MatchSnapshot` of [matchId]. The current one is asked for on
     * opening and again after every reconnect (presence ends with the socket).
     * Ends with [MatchGoneException] when the backend no longer has the match.
     */
    fun snapshots(matchId: String): Flow<MatchSnapshot> = channelFlow {
        launch(start = CoroutineStart.UNDISPATCHED) {
            realtime.stream(Channel.MATCH)
                .filter { it.type == MessageType.MATCH_SNAPSHOT }
                .map { realtime.decode(it, MatchSnapshot.serializer()) }
                .filter { it.matchId == matchId }
                .collect { send(it) }
        }
        realtime.connections.collect { count ->
            if (count > 0) {
                try {
                    request(MessageType.MATCH_REQUEST_SNAPSHOT, MatchRequestSnapshot.serializer(), MatchRequestSnapshot(matchId, lastVersion = 0))
                } catch (e: RealtimeRejectedException) {
                    if (e.code == ErrorCode.NOT_FOUND) throw MatchGoneException(matchId)
                    CrashReporter.record(e, "MatchChannel.snapshot")
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    CrashReporter.record(e, "MatchChannel.snapshot")
                }
            }
        }
    }

    /** `MatchEnded`: the final ranking with each seat's coins (it follows the last snapshot; one match at a time). */
    fun endings(): Flow<MatchEnded> =
        realtime.stream(Channel.MATCH)
            .filter { it.type == MessageType.MATCH_ENDED }
            .map { realtime.decode(it, MatchEnded.serializer()) }

    suspend fun <T> request(type: String, serializer: KSerializer<T>, value: T) {
        realtime.request(Channel.MATCH, type, serializer, value)
    }

    /** A game-specific action: [action] carries `"type"` plus its fields. */
    suspend fun gameAction(matchId: String, action: JsonObject) =
        request(MessageType.GAME_ACTION, GameAction.serializer(), GameAction(matchId, action))

    /** The keep-playing vote at a round or time limit. */
    suspend fun voteExtension(matchId: String, keepPlaying: Boolean) =
        request(MessageType.VOTE_EXTENSION, VoteExtension.serializer(), VoteExtension(matchId, keepPlaying))

    /** Asks everyone to end the match early (the end-game vote). */
    suspend fun proposeEnd(matchId: String) =
        request(MessageType.PROPOSE_END_MATCH, ProposeEndMatch.serializer(), ProposeEndMatch(matchId))

    suspend fun castEndVote(matchId: String, agree: Boolean) =
        request(MessageType.CAST_END_VOTE, CastEndVote.serializer(), CastEndVote(matchId, agree))

    suspend fun setAutoplay(matchId: String, enabled: Boolean) =
        request(MessageType.SET_AUTOPLAY, SetAutoplay.serializer(), SetAutoplay(matchId, enabled))

    suspend fun setMic(matchId: String, on: Boolean) =
        request(MessageType.SET_MIC, SetMic.serializer(), SetMic(matchId, on))

    suspend fun setAway(matchId: String, away: Boolean) =
        request(MessageType.SET_PRESENCE, SetPresence.serializer(), SetPresence(matchId, away))

    suspend fun stillHere(matchId: String) =
        request(MessageType.CONFIRM_PRESENCE, ConfirmPresence.serializer(), ConfirmPresence(matchId))

    suspend fun leave(matchId: String, giveToUserId: String?) =
        request(MessageType.LEAVE_SEAT, LeaveSeat.serializer(), LeaveSeat(matchId, giveToUserId))

    suspend fun answerSeatOffer(matchId: String, accept: Boolean) =
        request(MessageType.ANSWER_SEAT_OFFER, AnswerSeatOffer.serializer(), AnswerSeatOffer(matchId, accept))

    suspend fun chooseSeatTaker(matchId: String, giveToUserId: String?) =
        request(MessageType.CHOOSE_SEAT_TAKER, ChooseSeatTaker.serializer(), ChooseSeatTaker(matchId, giveToUserId))

    suspend fun takeSeat(matchId: String, seat: Int) =
        request(MessageType.TAKE_SEAT, TakeSeat.serializer(), TakeSeat(matchId, seat))
}
