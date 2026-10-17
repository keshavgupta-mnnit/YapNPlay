package com.kglabs28.yapnplay.appengine.features.playroomlobby.data.remote

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.FakeDataProvider
import com.kglabs28.yapnplay.appengine.features.gamecatalog.repository.GameCatalogRepository
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.LobbyCommand
import com.kglabs28.yapnplay.appengine.protocol.MatchStarting
import com.kglabs28.yapnplay.appengine.protocol.Presence
import com.kglabs28.yapnplay.appengine.protocol.RoomInfo
import com.kglabs28.yapnplay.appengine.protocol.RoomMember
import com.kglabs28.yapnplay.appengine.protocol.RoomSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * In-memory stand-in for the server's room actor, so the lobby behaves
 * like production before the backend exists. Starts from
 * [FakeDataProvider.Lobby] and applies the documented room rules to
 * every command (docs/backend-api-contract.md). Deleted with the fakes.
 */
class FakeLobbyServer(
    private val catalog: GameCatalogRepository,
    private val clock: AppClock,
    dispatchers: DispatcherProvider,
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatchers.default)
    private val rooms = mutableMapOf<String, MutableStateFlow<RoomSnapshot>>()
    private var countdownJob: Job? = null
    private val me = FakeDataProvider.Profile.me.userId

    private val _matchStarting = MutableSharedFlow<MatchStarting>(extraBufferCapacity = 1)
    val matchStarting: SharedFlow<MatchStarting> = _matchStarting

    fun room(roomId: String): Flow<RoomSnapshot> = flow {
        delay(FakeDataProvider.NETWORK_DELAY_MS)
        emitAll(stateOf(roomId))
    }

    /** Rotates "who is talking" among online members with their mic on. */
    fun speaking(roomId: String): Flow<Set<String>> = flow {
        var turn = 0
        while (true) {
            val talkers = stateOf(roomId).value.room.members
                .filter { it.presence == Presence.ONLINE && it.micOn }
            emit(talkers.getOrNull(turn % talkers.size.coerceAtLeast(1))?.let { setOf(it.userId) }.orEmpty())
            turn++
            delay(FakeDataProvider.Lobby.SPEAKER_ROTATION_MS)
        }
    }

    suspend fun handle(roomId: String, command: LobbyCommand) {
        when (command) {
            is LobbyCommand.SelectGame -> {
                val defaults = command.gameId
                    ?.let { catalog.findGame(it) }
                    ?.houseRuleOptions
                    ?.associate { it.key to it.defaultEnabled }
                    .orEmpty()
                cancelCountdown(roomId)
                update(roomId) {
                    copy(
                        selectedGame = command.gameId,
                        houseRules = defaults,
                        members = members.map { it.copy(ready = false, watching = false) },
                    )
                }
            }
            is LobbyCommand.SetHouseRule -> update(roomId) {
                copy(houseRules = houseRules + (command.key to command.enabled))
            }
            is LobbyCommand.SetBotFill -> update(roomId) { copy(botFill = command.enabled) }
            is LobbyCommand.SetReady -> updateMe(roomId) {
                copy(ready = command.ready, watching = if (command.ready) false else watching)
            }
            is LobbyCommand.SetWatching -> updateMe(roomId) { copy(watching = command.watching) }
            is LobbyCommand.SetVoiceState -> updateMe(roomId) {
                copy(micOn = command.micOn, speakerOn = command.speakerOn)
            }
            LobbyCommand.StartCountdown -> startCountdown(roomId)
            LobbyCommand.CancelCountdown -> cancelCountdown(roomId)
            is LobbyCommand.TransferLeader -> update(roomId) { copy(leaderId = command.userId) }
            is LobbyCommand.TransferAdmin -> update(roomId) { copy(adminId = command.userId) }
            is LobbyCommand.SetFriend -> update(roomId) {
                copy(members = members.map { if (it.userId == command.userId) it.copy(isFriend = command.friend) else it })
            }
            LobbyCommand.ExitLobby -> Unit
            LobbyCommand.LeaveRoom -> update(roomId) { copy(members = members.filterNot { it.userId == me }) }
        }
    }

    private fun startCountdown(roomId: String) {
        val endsAt = clock.now().toEpochMilliseconds() + AppConstants.COUNTDOWN_SECONDS * MILLIS_PER_SECOND
        update(roomId) { copy(countdownDeadline = endsAt) }
        countdownJob?.cancel()
        countdownJob = scope.launch {
            delay(AppConstants.COUNTDOWN_SECONDS * MILLIS_PER_SECOND)
            update(roomId) { copy(countdownDeadline = null) }
            _matchStarting.emit(MatchStarting(matchId = "match-$roomId-$endsAt"))
        }
    }

    private fun cancelCountdown(roomId: String) {
        countdownJob?.cancel()
        update(roomId) { copy(countdownDeadline = null) }
    }

    private fun stateOf(roomId: String): MutableStateFlow<RoomSnapshot> =
        rooms.getOrPut(roomId) {
            MutableStateFlow(FakeDataProvider.Lobby.snapshot(roomId, clock.now().toEpochMilliseconds()))
        }

    private fun update(roomId: String, transform: RoomInfo.() -> RoomInfo) {
        stateOf(roomId).update { snapshot ->
            snapshot.copy(
                version = snapshot.version + 1,
                serverTime = clock.now().toEpochMilliseconds(),
                room = snapshot.room.transform(),
            )
        }
    }

    private fun updateMe(roomId: String, transform: RoomMember.() -> RoomMember) = update(roomId) {
        copy(members = members.map { if (it.userId == me) it.transform() else it })
    }

    private companion object {
        const val MILLIS_PER_SECOND = 1_000L
    }
}
