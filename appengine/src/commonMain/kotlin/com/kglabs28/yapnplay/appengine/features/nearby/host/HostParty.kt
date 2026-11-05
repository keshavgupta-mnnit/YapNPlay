package com.kglabs28.yapnplay.appengine.features.nearby.host

import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyPlayer
import com.kglabs28.yapnplay.appengine.features.nearby.host.match.HostMatch
import com.kglabs28.yapnplay.appengine.features.nearby.host.match.HostMatchChannel
import com.kglabs28.yapnplay.appengine.features.nearby.host.match.HostMatchStarter
import com.kglabs28.yapnplay.appengine.features.nearby.host.room.HostRoom
import com.kglabs28.yapnplay.appengine.protocol.ErrorCode
import com.kglabs28.yapnplay.appengine.protocol.JoinRoom
import com.kglabs28.yapnplay.appengine.protocol.JoinRoomResult
import com.kglabs28.yapnplay.appengine.protocol.JoinStatus
import com.kglabs28.yapnplay.appengine.protocol.MatchStarting
import com.kglabs28.yapnplay.appengine.protocol.RoomSnapshot
import com.kglabs28.yapnplay.appengine.protocol.RoomSummary
import com.kglabs28.yapnplay.appengine.protocol.SelectGame
import com.kglabs28.yapnplay.appengine.protocol.SetBotFill
import com.kglabs28.yapnplay.appengine.protocol.SetHouseRule
import com.kglabs28.yapnplay.appengine.protocol.SetReady
import com.kglabs28.yapnplay.appengine.protocol.SetTalkingMuted
import com.kglabs28.yapnplay.appengine.protocol.SetVoiceState
import com.kglabs28.yapnplay.appengine.protocol.SetWatching
import com.kglabs28.yapnplay.appengine.protocol.TransferLeader
import com.kglabs28.yapnplay.appengine.protocol.transport.Channel
import com.kglabs28.yapnplay.appengine.protocol.transport.Envelope
import com.kglabs28.yapnplay.appengine.protocol.transport.MessageType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * A Quick Play party hosted on this phone for Nearby play: its room (the
 * same temporary-room lobby as online) and its matches. Every person's
 * requests come in through [handle] with their user id, exactly as the
 * backend would get them; what goes out to one person goes to [deliver].
 */
class HostParty(
    val room: HostRoom,
    private val starter: HostMatchStarter,
    private val matchChannel: HostMatchChannel,
    private val envelopes: HostEnvelopes,
    private val scope: CoroutineScope,
    private val deliver: (userId: String, envelope: Envelope) -> Unit,
    /** A guest left the lobby: their phone is let go. */
    private val onGuestLeft: (userId: String) -> Unit,
    /** The host left the lobby: the party is over for everyone. */
    private val onHostLeft: () -> Unit,
) {
    private val lock = Mutex()
    private val matches = mutableMapOf<String, HostMatch>()
    private val players = mutableMapOf<String, NearbyPlayer>()
    private var countdownJob: Job? = null
    private val hostId: String = room.leaderId

    /** Someone accepted the invite: a member now; their lobby opens next. */
    suspend fun admit(player: NearbyPlayer) = lock.withLock {
        players[player.userId] = player
        room.add(player)
        publish()
    }

    /** True when there is no room for another person. */
    suspend fun isFull(): Boolean = lock.withLock { room.isFull }

    /** A guest's phone dropped off: out of the lobby; at a table the bot plays for them until they are back. */
    suspend fun dropped(userId: String) {
        lock.withLock {
            if (!room.has(userId)) return@withLock
            room.remove(userId)
            publish()
        }
        matches.values.filterNot { it.isFinished }.forEach { runCatching { it.disconnected(userId) } }
    }

    suspend fun handle(userId: String, request: Envelope): Envelope =
        if (request.channel == Channel.MATCH) {
            val me = players[userId]
            matchChannel.handle(userId, me?.name.orEmpty(), me?.avatarId.orEmpty(), request) { matches[it] }
        } else {
            try {
                roomRequest(userId, request)
            } catch (e: HostRefusal) {
                envelopes.rejected(request, e.code, e.message)
            } catch (e: IllegalArgumentException) {
                envelopes.rejected(request, ErrorCode.BAD_REQUEST, BAD_REQUEST)
            }
        }

    /** The party ends: matches stop. */
    fun close() {
        countdownJob?.cancel()
        matches.values.forEach { it.stop() }
        matches.clear()
    }

    fun register(player: NearbyPlayer) {
        players[player.userId] = player
    }

    private suspend fun roomRequest(userId: String, request: Envelope): Envelope {
        when (request.type) {
            MessageType.JOIN_ROOM -> {
                envelopes.payload(request, JoinRoom.serializer())
                val result = lock.withLock {
                    if (!room.has(userId)) throw HostRefusal(ErrorCode.NOT_FOUND, NOT_IN_PARTY)
                    room.enter(userId)
                    publish()
                    JoinRoomResult(JoinStatus.JOINED, room.name, summary(userId))
                }
                return envelopes.reply(request, MessageType.JOIN_ROOM_RESULT, JoinRoomResult.serializer(), result)
            }
            MessageType.REQUEST_SNAPSHOT -> {
                val snapshot = lock.withLock {
                    if (!room.has(userId)) throw HostRefusal(ErrorCode.NOT_MEMBER, NOT_IN_PARTY)
                    room.snapshot()
                }
                return envelopes.reply(request, MessageType.ROOM_SNAPSHOT, RoomSnapshot.serializer(), snapshot)
            }
            MessageType.LEAVE_ROOM, MessageType.EXIT_LOBBY -> leave(userId)
            MessageType.START_COUNTDOWN -> lock.withLock {
                room.startCountdown(userId, COUNTDOWN_SECONDS)?.let(::scheduleCountdown)
                publish()
            }
            else -> lock.withLock {
                apply(userId, request)
                publish()
            }
        }
        return envelopes.ack(request)
    }

    private fun apply(userId: String, request: Envelope) {
        when (request.type) {
            MessageType.CANCEL_COUNTDOWN -> room.cancelCountdown(userId)
            MessageType.SELECT_GAME -> room.selectGame(userId, envelopes.payload(request, SelectGame.serializer()).gameId)
            MessageType.SET_BOT_FILL -> room.setBotFill(userId, envelopes.payload(request, SetBotFill.serializer()).enabled)
            MessageType.SET_READY -> room.setReady(userId, envelopes.payload(request, SetReady.serializer()).ready)
            MessageType.SET_WATCHING -> room.setWatching(userId, envelopes.payload(request, SetWatching.serializer()).watching)
            MessageType.SET_HOUSE_RULE -> envelopes.payload(request, SetHouseRule.serializer()).let { room.setHouseRule(userId, it.key, it.enabled) }
            MessageType.SET_VOICE_STATE -> envelopes.payload(request, SetVoiceState.serializer()).let { room.setVoice(userId, it.micOn, it.speakerOn) }
            MessageType.SET_TALKING_MUTED -> room.setTalkingMuted(userId, envelopes.payload(request, SetTalkingMuted.serializer()).talking)
            MessageType.TRANSFER_LEADER -> room.transferLeader(userId, envelopes.payload(request, TransferLeader.serializer()).userId)
            // Leagues, admins, renaming and join requests belong to online Game Rooms.
            else -> throw HostRefusal(ErrorCode.BAD_REQUEST, NOT_OFFLINE)
        }
    }

    /** Back out of the lobby: a guest leaves the party; the host ends it. */
    private suspend fun leave(userId: String) {
        if (userId == hostId) {
            onHostLeft()
            return
        }
        lock.withLock {
            room.remove(userId)
            publish()
        }
        onGuestLeft(userId)
    }

    private fun scheduleCountdown(deadline: Long) {
        countdownJob?.cancel()
        countdownJob = scope.launch {
            delay(COUNTDOWN_SECONDS * HostConstants.MILLIS_PER_SECOND)
            startMatch(deadline)
        }
    }

    /** The countdown ran out: seat everyone, tell every lobby, and open the table. */
    private suspend fun startMatch(deadline: Long) {
        val started = lock.withLock {
            val plan = room.countdownElapsed(deadline)
            val match = plan?.let { starter.start(room.roomId, it, scope, deliver, ::finished) }
            if (plan != null && match == null) room.failedToStart()
            if (match != null) {
                matches.values.forEach { it.stop() }
                matches.clear()
                matches[match.matchId] = match
                room.matchStarted(match.matchId)
                val starting = envelopes.push(Channel.ROOM, MessageType.MATCH_STARTING, MatchStarting.serializer(), MatchStarting(match.matchId))
                room.onlineIds().forEach { deliver(it, starting) }
            }
            publish()
            if (match != null && plan != null) match to plan.players.map { it.userId }.toSet() else null
        }
        started?.let { (match, humans) -> match.open(humans) }
    }

    private fun finished(matchId: String) {
        scope.launch {
            lock.withLock {
                if (room.matchInProgress == matchId) room.matchInProgress = null
                publish()
            }
        }
    }

    private fun summary(userId: String) = RoomSummary(
        roomId = room.roomId,
        code = room.roomId,
        name = room.name,
        memberCount = room.onlineIds().size,
        onlineCount = room.onlineIds().size,
        isLeader = room.leaderId == userId,
        temporary = true,
    )

    /** Every lobby gets the new snapshot. */
    private fun publish() {
        room.touch()
        val push = envelopes.push(Channel.ROOM, MessageType.ROOM_SNAPSHOT, RoomSnapshot.serializer(), room.snapshot())
        room.onlineIds().forEach { deliver(it, push) }
    }

    private companion object {
        const val COUNTDOWN_SECONDS = 10
        const val BAD_REQUEST = "The request could not be read."
        const val NOT_IN_PARTY = "You are not in this party."
        const val NOT_OFFLINE = "Not available when playing offline."
    }
}
