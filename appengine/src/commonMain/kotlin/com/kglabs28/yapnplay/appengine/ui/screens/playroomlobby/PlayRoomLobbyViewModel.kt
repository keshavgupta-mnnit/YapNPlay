package com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.DeepLinks
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.gamecatalog.domain.GameCatalogItem
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.LobbyCommand
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.LobbyData
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.LobbyMember
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.LobbyRoom
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.LobbyRules
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.MemberMenuOption
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.PlayerButtonState
import com.kglabs28.yapnplay.appengine.features.playroomlobby.usecase.ObserveMatchStartingUseCase
import com.kglabs28.yapnplay.appengine.features.playroomlobby.usecase.ObservePlayRoomLobbyUseCase
import com.kglabs28.yapnplay.appengine.features.playroomlobby.usecase.SendLobbyCommandUseCase
import com.kglabs28.yapnplay.appengine.ui.components.games.GamePickerViewMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Play-room lobby. Follows the live room, turns it into display state
 * (via [LobbyRules]) and turns taps into room commands. The server owns
 * every rule; this only shows what is possible for the user role.
 */
class PlayRoomLobbyViewModel(
    private val observeLobby: ObservePlayRoomLobbyUseCase,
    private val sendCommand: SendLobbyCommandUseCase,
    private val observeMatchStarting: ObserveMatchStartingUseCase,
    private val clock: AppClock,
    dispatchers: DispatcherProvider,
) : MviViewModel<PlayRoomLobbyState, PlayRoomLobbyIntent, PlayRoomLobbyEffect>(PlayRoomLobbyState(), dispatchers) {

    private var roomId: String? = null
    private var data: LobbyData? = null
    private var userModal: LobbyModal? = null
    private var tickJob: Job? = null

    override suspend fun handleIntent(intent: PlayRoomLobbyIntent) {
        when (intent) {
            is PlayRoomLobbyIntent.Load -> load(intent.roomId)
            is PlayRoomLobbyIntent.FocusGame -> setState { copy(focusedIndex = intent.index) }
            PlayRoomLobbyIntent.ToggleViewMode -> setState {
                copy(viewMode = if (viewMode == GamePickerViewMode.GRID) GamePickerViewMode.CAROUSEL else GamePickerViewMode.GRID)
            }
            is PlayRoomLobbyIntent.SelectGame -> state.value.games.getOrNull(intent.index)?.let {
                setState { copy(focusedIndex = intent.index, viewMode = GamePickerViewMode.CAROUSEL) }
                send(LobbyCommand.SelectGame(it.id))
            }
            PlayRoomLobbyIntent.ChangeGame -> send(LobbyCommand.SelectGame(null))
            is PlayRoomLobbyIntent.ToggleHouseRule -> toggleHouseRule(intent.key)
            PlayRoomLobbyIntent.PrimaryAction -> primaryAction()
            is PlayRoomLobbyIntent.ToggleMic -> me()?.let {
                when {
                    it.micOn -> send(LobbyCommand.SetVoiceState(false, it.speakerOn))
                    intent.permissionGranted -> send(LobbyCommand.SetVoiceState(true, it.speakerOn))
                    else -> emitEffect(PlayRoomLobbyEffect.RequestMicPermission)
                }
            }
            is PlayRoomLobbyIntent.MicPermissionResult -> if (intent.granted) {
                me()?.let { send(LobbyCommand.SetVoiceState(true, it.speakerOn)) }
            } else {
                showModal(LobbyModal.MicPermissionNeeded)
            }
            PlayRoomLobbyIntent.OpenAppSettings -> {
                showModal(null)
                emitEffect(PlayRoomLobbyEffect.OpenAppSettings)
            }
            PlayRoomLobbyIntent.ToggleSpeaker -> me()?.let { send(LobbyCommand.SetVoiceState(it.micOn, !it.speakerOn)) }
            is PlayRoomLobbyIntent.MemberClicked -> openMemberMenu(intent.userId)
            is PlayRoomLobbyIntent.MenuOptionChosen -> {
                showModal(null)
                send(intent.option.toCommand(intent.userId))
            }
            PlayRoomLobbyIntent.ShowRuleSheet -> showModal(LobbyModal.RuleSheet)
            PlayRoomLobbyIntent.LeaveClicked -> showModal(LobbyModal.LeaveConfirm)
            PlayRoomLobbyIntent.ConfirmLeave -> {
                showModal(null)
                send(LobbyCommand.LeaveRoom)
                emitEffect(PlayRoomLobbyEffect.Close)
            }
            PlayRoomLobbyIntent.CancelCountdown -> send(LobbyCommand.CancelCountdown)
            PlayRoomLobbyIntent.DismissModal -> showModal(null)
            PlayRoomLobbyIntent.Back -> {
                send(LobbyCommand.ExitLobby)
                emitEffect(PlayRoomLobbyEffect.Close)
            }
            PlayRoomLobbyIntent.ShareLink -> data?.room?.let {
                emitEffect(PlayRoomLobbyEffect.ShareText(Strings.roomInviteMessage(it.name, DeepLinks.inviteUrl(it.code))))
            }
            // Screens for these arrive later.
            PlayRoomLobbyIntent.OpenWaitingList,
            PlayRoomLobbyIntent.OpenLeaderboard -> Unit
        }
    }

    private fun load(id: String) {
        if (roomId != null) return
        roomId = id
        scope.launch {
            observeLobby(id).collect {
                data = it
                render()
            }
        }
        scope.launch {
            observeMatchStarting(id).collect { emitEffect(PlayRoomLobbyEffect.OpenMatch(it)) }
        }
    }

    private suspend fun send(command: LobbyCommand) {
        roomId?.let { sendCommand(it, command) }
    }

    private fun me(): LobbyMember? = data?.let { d -> d.room.members.firstOrNull { it.userId == d.myUserId } }

    private suspend fun toggleHouseRule(key: String) {
        val d = data ?: return
        if (me()?.isLeader != true) return
        if (key == BOT_FILL_KEY) {
            send(LobbyCommand.SetBotFill(!d.room.botFill))
        } else {
            val current = d.room.houseRules[key]
                ?: d.selectedGame?.houseRuleOptions?.firstOrNull { it.key == key }?.defaultEnabled
                ?: false
            send(LobbyCommand.SetHouseRule(key, !current))
        }
    }

    private suspend fun primaryAction() {
        val d = data ?: return
        val me = me() ?: return
        if (me.isLeader) {
            if (LobbyRules.canStart(d.room, d.selectedGame)) send(LobbyCommand.StartCountdown)
            return
        }
        when (LobbyRules.playerButtonState(d.room, me, d.selectedGame)) {
            PlayerButtonState.DISABLED -> Unit
            PlayerButtonState.NOT_READY -> send(LobbyCommand.SetReady(true))
            PlayerButtonState.READY -> send(LobbyCommand.SetReady(false))
            PlayerButtonState.NOT_WATCHING -> send(LobbyCommand.SetWatching(true))
            PlayerButtonState.WATCHING -> send(LobbyCommand.SetWatching(false))
        }
    }

    private fun openMemberMenu(userId: String) {
        val d = data ?: return
        val me = me() ?: return
        val target = d.room.members.firstOrNull { it.userId == userId } ?: return
        val options = LobbyRules.menuOptions(me, target)
        if (options.isNotEmpty()) showModal(LobbyModal.MemberMenu(target.userId, target.name, options))
    }

    private fun showModal(modal: LobbyModal?) {
        userModal = modal
        render()
    }

    /** Rebuilds the whole display state from the latest room data. */
    private fun render() {
        val d = data ?: return
        val room = d.room
        val me = me()
        val game = d.selectedGame
        manageCountdownTicker(room)
        setState {
            copy(
                loaded = true,
                roomName = room.name,
                roomDescription = room.description,
                isLeader = me?.isLeader == true,
                isAdmin = me?.isAdmin == true,
                members = LobbyRules.sortMembers(room.members, d.myUserId).map { it.toUi(d) },
                membersLabel = Strings.lobbyPlayersOnline(room.members.count { it.online }, room.members.size),
                games = d.games,
                focusedIndex = focusedIndex.coerceIn(0, (d.games.size - 1).coerceAtLeast(0)),
                selectedGame = game,
                houseRules = game?.let { houseRulesFor(room, it) }.orEmpty(),
                fixedRules = game?.fixedRules.orEmpty(),
                primary = me?.let { primaryFor(room, it, game) } ?: LobbyPrimaryButtonUi(),
                micOn = me?.micOn == true,
                speakerOn = me?.speakerOn == true,
                modal = countdownModal(room, me) ?: userModal,
            )
        }
    }

    private fun LobbyMember.toUi(d: LobbyData): LobbyMemberUi {
        val talking = online && micOn && userId in d.speakingUserIds
        return LobbyMemberUi(
            userId = userId,
            name = name,
            online = online,
            isMe = userId == d.myUserId,
            isLeader = isLeader,
            isAdmin = isAdmin,
            talking = talking,
            voiceLabel = when {
                !online || talking -> null
                !micOn && !speakerOn -> Strings.lobbyMicSpeakerOff
                !micOn -> Strings.lobbyMicOff
                !speakerOn -> Strings.lobbySpeakerOff
                else -> null
            },
            status = when {
                !online -> LobbyMemberStatus.OFFLINE
                d.selectedGame == null -> LobbyMemberStatus.NONE
                isLeader || ready -> LobbyMemberStatus.READY
                watching -> LobbyMemberStatus.WATCHING
                else -> LobbyMemberStatus.NOT_READY
            },
        )
    }

    /** "Fill with bots" first, then the game own toggles. */
    private fun houseRulesFor(room: LobbyRoom, game: GameCatalogItem): List<HouseRuleUi> =
        listOf(HouseRuleUi(BOT_FILL_KEY, Strings.lobbyBotFillTitle, Strings.lobbyBotFillDescription, room.botFill)) +
            game.houseRuleOptions.map {
                HouseRuleUi(it.key, it.title, it.description, room.houseRules[it.key] ?: it.defaultEnabled)
            }

    private fun primaryFor(room: LobbyRoom, me: LobbyMember, game: GameCatalogItem?): LobbyPrimaryButtonUi {
        if (me.isLeader) {
            val canStart = LobbyRules.canStart(room, game)
            return LobbyPrimaryButtonUi(
                label = Strings.lobbyStartMatch,
                sublabel = game?.let { Strings.lobbyPlayersReady(LobbyRules.readyCount(room), it.maxPlayers) }
                    ?: Strings.lobbyPickGameFirst,
                enabled = canStart,
                active = canStart,
                isStart = true,
            )
        }
        return when (LobbyRules.playerButtonState(room, me, game)) {
            PlayerButtonState.DISABLED -> LobbyPrimaryButtonUi(Strings.lobbyReadyButton, Strings.lobbyWaitingForGame)
            PlayerButtonState.NOT_READY -> LobbyPrimaryButtonUi(Strings.lobbyNotReadyButton, Strings.lobbyTapWhenReady, enabled = true)
            PlayerButtonState.READY -> LobbyPrimaryButtonUi(Strings.lobbyReadyButton, Strings.lobbySeatSaved, enabled = true, active = true)
            PlayerButtonState.NOT_WATCHING -> LobbyPrimaryButtonUi(Strings.lobbyNotWatchingButton, Strings.lobbyTapToWatch, enabled = true)
            PlayerButtonState.WATCHING -> LobbyPrimaryButtonUi(Strings.lobbyWatchingButton, Strings.lobbyTapToStopWatching, enabled = true, active = true)
        }
    }

    private fun countdownModal(room: LobbyRoom, me: LobbyMember?): LobbyModal? {
        val endsAt = room.countdownEndsAt ?: return null
        val seconds = (endsAt - clock.now()).inWholeSeconds.coerceAtLeast(0)
        return LobbyModal.Countdown(
            title = Strings.lobbyStartingIn(seconds),
            canCancel = me != null && (me.isLeader || me.ready),
        )
    }

    /** Re-renders every second while a countdown runs, so the number ticks. */
    private fun manageCountdownTicker(room: LobbyRoom) {
        if (room.countdownEndsAt == null) {
            tickJob?.cancel()
            tickJob = null
            return
        }
        if (tickJob?.isActive == true) return
        tickJob = scope.launch {
            while (isActive) {
                delay(AppConstants.SEARCH_TICK_MS)
                render()
            }
        }
    }

    private fun MemberMenuOption.toCommand(userId: String): LobbyCommand = when (this) {
        MemberMenuOption.MAKE_LEADER -> LobbyCommand.TransferLeader(userId)
        MemberMenuOption.MAKE_ADMIN -> LobbyCommand.TransferAdmin(userId)
        MemberMenuOption.ADD_FRIEND -> LobbyCommand.SetFriend(userId, true)
        MemberMenuOption.REMOVE_FRIEND -> LobbyCommand.SetFriend(userId, false)
    }

    private companion object {
        /** House-rule key for the room-level bot fill option. */
        const val BOT_FILL_KEY = "bot_fill"
    }
}
