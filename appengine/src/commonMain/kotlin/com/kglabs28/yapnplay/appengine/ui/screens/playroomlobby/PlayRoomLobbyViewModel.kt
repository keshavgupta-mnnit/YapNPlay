package com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby

import com.kglabs28.yapnplay.appengine.core.ui.NearbyStrings
import com.kglabs28.yapnplay.appengine.features.nearby.usecase.InviteNearbyPlayerUseCase
import com.kglabs28.yapnplay.appengine.features.nearby.usecase.IsNearbyRoomUseCase
import com.kglabs28.yapnplay.appengine.features.nearby.usecase.LeaveNearbyUseCase
import com.kglabs28.yapnplay.appengine.features.nearby.usecase.ObserveNearbyUseCase
import kotlin.time.Duration.Companion.milliseconds
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.CountdownStopInfo
import com.kglabs28.yapnplay.appengine.features.voice.domain.MySpeech
import com.kglabs28.yapnplay.appengine.features.voice.domain.MicGate
import com.kglabs28.yapnplay.appengine.core.common.TextUtils
import com.kglabs28.yapnplay.appengine.core.ui.CoinsStrings
import com.kglabs28.yapnplay.appengine.ui.components.coins.LeagueUi
import com.kglabs28.yapnplay.appengine.features.voice.usecase.SetVoiceWantedUseCase
import com.kglabs28.yapnplay.appengine.features.voice.domain.VoiceNeed
import com.kglabs28.yapnplay.appengine.features.playroomlobby.usecase.GetRoomPendingInvitesUseCase
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.RoomMemberRecord
import com.kglabs28.yapnplay.appengine.features.playroomlobby.usecase.GetRoomLeaderboardUseCase
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.RoomInvites
import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings
import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
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
import com.kglabs28.yapnplay.appengine.features.voice.usecase.ObserveMicOnUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.ObserveMySpeakingUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.SetMicOnUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.SetMicOpenUseCase
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.InviteFriendsToGroupUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.GetFriendsUseCase
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
    private val observeMicOn: ObserveMicOnUseCase,
    private val setMicOn: SetMicOnUseCase,
    private val observeMySpeaking: ObserveMySpeakingUseCase,
    private val setMicOpen: SetMicOpenUseCase,
    private val getFriends: GetFriendsUseCase,
    private val inviteFriends: InviteFriendsToGroupUseCase,
    private val clock: AppClock,
    private val roomInvites: RoomInvites,
    private val getRoomLeaderboard: GetRoomLeaderboardUseCase,
    private val getPendingInvites: GetRoomPendingInvitesUseCase,
    private val setVoiceWanted: SetVoiceWantedUseCase,
    private val isNearbyRoom: IsNearbyRoomUseCase,
    private val observeNearby: ObserveNearbyUseCase,
    private val inviteNearby: InviteNearbyPlayerUseCase,
    private val leaveNearby: LeaveNearbyUseCase,
    dispatchers: DispatcherProvider,
) : MviViewModel<PlayRoomLobbyState, PlayRoomLobbyIntent, PlayRoomLobbyEffect>(PlayRoomLobbyState(), dispatchers) {

    private var roomId: String? = null
    /** Voice only while another member is here and someone's mic is on. */
    private val voiceNeed = VoiceNeed { wanted, talkers -> roomId?.let { setVoiceWanted.room(it, wanted, talkers) } }
    private var data: LobbyData? = null
    private var userModal: LobbyModal? = null
    /** The members' records behind the open leaderboard (both tabs rank these). */
    private var boardRecords: List<RoomMemberRecord> = emptyList()
    /** The app-wide mic switch the room follows. */
    private var micSwitch = false
    /** The user speaking into their mic, whether it's listening, and whether the room is on screen. */
    private var mySpeech = MySpeech.SILENT
    private val micGate = MicGate { open, listenMuted -> roomId?.let { setMicOpen.room(it, open, listenMuted) } }
    private var foreground = true
    private var tickJob: Job? = null
    /** The countdown stop already told (each is shown once). */
    private var toldStop: CountdownStopInfo? = null
    /** Keeps the party's Add Friends rows live while it is open. */
    private var inviteJob: Job? = null
    /** Friends invited from the open Add Friends (each once). */
    private val invitedNow = mutableSetOf<String>()

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
            is PlayRoomLobbyIntent.SelectLeague -> selectLeague(intent.entryCoins)
            PlayRoomLobbyIntent.PrimaryAction -> primaryAction()
            // The mic switch is the user's, everywhere: the room follows it.
            is PlayRoomLobbyIntent.ToggleMic -> when {
                micSwitch -> setMicOn(false)
                intent.permissionGranted -> setMicOn(true)
                else -> emitEffect(PlayRoomLobbyEffect.RequestMicPermission)
            }
            is PlayRoomLobbyIntent.MicPermissionResult -> if (intent.granted) {
                setMicOn(true)
            } else {
                showModal(LobbyModal.MicPermissionNeeded)
            }
            is PlayRoomLobbyIntent.ScreenVisible -> {
                foreground = intent.visible
                render()
            }
            PlayRoomLobbyIntent.OpenAppSettings -> {
                showModal(null)
                emitEffect(PlayRoomLobbyEffect.OpenAppSettings)
            }
            PlayRoomLobbyIntent.ToggleSpeaker -> me()?.let { send(LobbyCommand.SetVoiceState(it.micOn, !it.speakerOn)) }
            is PlayRoomLobbyIntent.MemberClicked -> openMemberMenu(intent.userId)
            is PlayRoomLobbyIntent.MenuOptionChosen -> {
                val name = data?.room?.members?.firstOrNull { it.userId == intent.userId }?.name.orEmpty()
                showModal(LobbyModal.ConfirmMemberAction(intent.userId, name, intent.option))
            }
            PlayRoomLobbyIntent.ConfirmMemberAction -> (userModal as? LobbyModal.ConfirmMemberAction)?.let {
                showModal(null)
                send(it.option.toCommand(it.userId))
            }
            PlayRoomLobbyIntent.ShowRuleSheet -> showModal(LobbyModal.RuleSheet)
            PlayRoomLobbyIntent.LeaveClicked -> showModal(leaveConfirm())
            PlayRoomLobbyIntent.ConfirmLeave -> leave()
            PlayRoomLobbyIntent.CancelCountdown -> send(LobbyCommand.CancelCountdown)
            PlayRoomLobbyIntent.DismissModal -> showModal(null)
            // A Quick Play party is only the people in it: going back leaves it, after asking (the last one out ends it).
            PlayRoomLobbyIntent.Back -> if (data?.room?.temporary == true) {
                showModal(leaveConfirm())
            } else {
                send(LobbyCommand.ExitLobby)
                emitEffect(PlayRoomLobbyEffect.Close)
            }
            PlayRoomLobbyIntent.ShareLink -> data?.room?.let {
                emitEffect(PlayRoomLobbyEffect.ShareText(roomInvites.message(it.name, it.code)))
            }
            PlayRoomLobbyIntent.OpenWaitingList -> showModal(LobbyModal.WaitingList)
            is PlayRoomLobbyIntent.ApproveJoin -> send(LobbyCommand.ApproveJoin(intent.userId))
            is PlayRoomLobbyIntent.DeclineJoin -> send(LobbyCommand.DeclineJoin(intent.userId))
            PlayRoomLobbyIntent.OpenLeaderboard -> openLeaderboard()
            is PlayRoomLobbyIntent.SelectLeaderboardTab -> (userModal as? LobbyModal.Leaderboard)?.let {
                showModal(LobbyModal.Leaderboard(it.board.copy(tab = intent.tab, rows = RoomLeaderboardUiMapper.rows(boardRecords, intent.tab, data?.myUserId))))
            }
            PlayRoomLobbyIntent.OpenAddFriends -> openAddFriends()
            is PlayRoomLobbyIntent.AddFriendCodeChanged -> updateAddFriends {
                copy(code = intent.code.uppercase().take(AppConstants.USER_CODE_LENGTH), error = null)
            }
            is PlayRoomLobbyIntent.ToggleInvite -> updateAddFriends {
                when {
                    // Already invited, waiting for their answer: not again.
                    intent.userId in pending -> this
                    intent.userId in selected -> copy(selected = selected - intent.userId)
                    else -> copy(selected = selected + intent.userId)
                }
            }
            PlayRoomLobbyIntent.SendInvites -> sendInvites()
            is PlayRoomLobbyIntent.InvitePlayer -> invitePlayer(intent.id)
            PlayRoomLobbyIntent.SaveRoomClicked -> saveRoomClicked()
            is PlayRoomLobbyIntent.SaveRoomNameChanged -> setState {
                copy(sheet = (sheet as? LobbySheet.SaveRoom)?.copy(name = intent.name.take(AppConstants.MAX_ROOM_NAME_LENGTH)))
            }
            PlayRoomLobbyIntent.ConfirmSaveRoom -> confirmSaveRoom()
            PlayRoomLobbyIntent.OpenEditName -> setState { copy(sheet = LobbySheet.EditName(roomName)) }
            is PlayRoomLobbyIntent.EditNameChanged -> setState {
                copy(sheet = (sheet as? LobbySheet.EditName)?.copy(name = intent.name.take(AppConstants.MAX_ROOM_NAME_LENGTH)))
            }
            PlayRoomLobbyIntent.SaveName -> saveName()
            PlayRoomLobbyIntent.DismissSheet -> {
                inviteJob?.cancel()
                setState { copy(sheet = null) }
            }
        }
    }

    private fun load(id: String) {
        if (roomId != null) return
        roomId = id
        scope.launch {
            observeLobby(id).collect {
                data = it
                syncMic()
                tellCountdownStop(it)
                render()
            }
        }
        scope.launch { observeMicOn().collect { micSwitch = it; syncMic() } }
        scope.launch { observeMySpeaking().collect { mySpeech = it; render() } }
        scope.launch {
            observeMatchStarting(id).collect { emitEffect(PlayRoomLobbyEffect.OpenMatch(selectedGameId(), it)) }
        }
    }

    private suspend fun send(command: LobbyCommand) {
        roomId?.let { sendCommand(it, command) }
    }

    private suspend fun leave() {
        showModal(null)
        send(LobbyCommand.LeaveRoom)
        // A Nearby party: this phone is out of it (the host's leaving ends it for everyone).
        if (nearby()) leaveNearby()
        emitEffect(PlayRoomLobbyEffect.Close)
    }

    private fun nearby(): Boolean = roomId?.let(isNearbyRoom::invoke) == true

    /** A party has no membership or code to come back with: it only asks to be sure. */
    private fun leaveConfirm(): LobbyModal.LeaveConfirm = if (data?.room?.temporary == true) {
        LobbyModal.LeaveConfirm(RoomStrings.lobbyLeavePartyTitle, RoomStrings.lobbyLeavePartyBody)
    } else {
        LobbyModal.LeaveConfirm(RoomStrings.lobbyLeaveTitle, RoomStrings.lobbyLeaveBody)
    }

    /** Not saved yet: ask the room's name (the party's to start with). Saved: add the newcomers at once. */
    private suspend fun saveRoomClicked() {
        val room = data?.room ?: return
        if (state.value.saveRoom?.enabled != true) return
        if (room.savedRoomId == null) {
            setState { copy(sheet = LobbySheet.SaveRoom(room.name.take(AppConstants.MAX_ROOM_NAME_LENGTH))) }
        } else {
            saveRoom(null)
        }
    }

    private suspend fun confirmSaveRoom() {
        val name = (state.value.sheet as? LobbySheet.SaveRoom)?.name?.trim().orEmpty()
        if (name.isEmpty()) return
        setState { copy(sheet = null) }
        saveRoom(name)
    }

    /** The server makes (or tops up) the Game Room with everyone here; the next snapshot updates the button. */
    private suspend fun saveRoom(name: String?) {
        val id = roomId ?: return
        if (sendCommand(id, LobbyCommand.SaveRoom(name)) is AppResult.Failure) showModal(LobbyModal.Failed(RoomStrings.lobbySaveRoomFailed))
    }

    /** The leader's button on an online party: Save as Room with 2+ people, then Update Room only while someone new isn't in it. */
    private fun saveRoomButton(room: LobbyRoom, me: LobbyMember?): SaveRoomButtonUi? = when {
        !room.temporary || me?.isLeader != true || nearby() -> null
        room.savedRoomId == null -> SaveRoomButtonUi(RoomStrings.lobbySaveRoom, enabled = room.members.size > 1)
        room.savedRoomMissing > 0 -> SaveRoomButtonUi(RoomStrings.lobbyUpdateRoom, enabled = true)
        else -> null
    }

    private fun selectedGameId(): String = data?.room?.selectedGameId.orEmpty()

    /** Tells the room the user's mic state when it differs from the app-wide switch. */
    private fun syncMic() {
        val me = me() ?: return
        if (me.micOn != micSwitch) scope.launch { send(LobbyCommand.SetVoiceState(micSwitch, me.speakerOn)) }
    }

    override fun onCleared() {
        micGate.clear()
        voiceNeed.clear()
    }

    private fun me(): LobbyMember? = data?.let { d -> d.room.members.firstOrNull { it.userId == d.myUserId } }

    /** Only the leader, and never a league someone in the lobby can't pay (the server checks too). */
    private suspend fun selectLeague(entryCoins: Int) {
        val room = data?.room ?: return
        if (me()?.isLeader != true || entryCoins == room.entryCoins || entryCoins > room.affordableEntry) return
        send(LobbyCommand.SetLeague(entryCoins))
    }

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
        // A match is already running: join it as a spectator.
        d.room.matchInProgress?.let {
            emitEffect(PlayRoomLobbyEffect.OpenMatch(selectedGameId(), it))
            return
        }
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
        val options = LobbyRules.menuOptions(me, target, d.room.temporary)
        if (options.isNotEmpty()) showModal(LobbyModal.MemberMenu(target.userId, target.name, options))
    }

    /** Add Friends: a party lists people to invite one by one; a Game Room keeps its pick-and-send dialog. */
    private suspend fun openAddFriends() {
        if (data?.room?.temporary == true) openInvitePlayers() else openGroupAddFriends()
    }

    /** A party's Add Friends: the phones around (Nearby) or online friends, live while open. */
    private suspend fun openInvitePlayers() {
        val id = roomId ?: return
        inviteJob?.cancel()
        val members = state.value.members.map { it.userId }.toSet()
        if (nearby()) {
            setState { copy(sheet = LobbySheet.InvitePlayers(NearbyStrings.addTitleNearby, NearbyStrings.addEmptyNearby, loading = false)) }
            inviteJob = scope.launch {
                observeNearby().collect { s ->
                    updateInvitePlayers { copy(rows = InviteRowsMapper.fromPeers(s.peers, state.value.members.map { it.userId }.toSet())) }
                }
            }
            return
        }
        setState { copy(sheet = LobbySheet.InvitePlayers(NearbyStrings.addTitleOnline, NearbyStrings.addEmptyOnline)) }
        invitedNow.clear()
        invitedNow += (getPendingInvites(id) as? AppResult.Success)?.data.orEmpty()
        val friends = (getFriends() as? AppResult.Success)?.data.orEmpty()
        updateInvitePlayers { copy(rows = InviteRowsMapper.fromFriends(friends, members, invitedNow), loading = false) }
    }

    /** Add + on one row: a nearby phone gets the invite over the radio, a friend through the server (and a popup). */
    private suspend fun invitePlayer(id: String) {
        val roomId = roomId ?: return
        if (nearby()) {
            inviteNearby(id)
            return
        }
        if (id in invitedNow) return
        invitedNow += id
        updateInvitePlayers { copy(rows = rows.map { if (it.id == id) it.copy(status = NearbyStrings.statusInvited, tone = InviteTone.DONE, canAdd = false) else it }, error = null) }
        if (inviteFriends(roomId, listOf(id), emptyList()) is AppResult.Failure) {
            invitedNow -= id
            val friends = (getFriends() as? AppResult.Success)?.data.orEmpty()
            updateInvitePlayers { copy(rows = InviteRowsMapper.fromFriends(friends, state.value.members.map { it.userId }.toSet(), invitedNow), error = NearbyStrings.addFailed) }
        }
    }

    private fun updateInvitePlayers(change: LobbySheet.InvitePlayers.() -> LobbySheet.InvitePlayers) {
        setState { copy(sheet = (sheet as? LobbySheet.InvitePlayers)?.change() ?: sheet) }
    }

    /** A Game Room's Add Friends: the user's friends who aren't in the group yet, online first; those already invited wait at the end. */
    private suspend fun openGroupAddFriends() {
        setState { copy(sheet = LobbySheet.AddFriends()) }
        val members = state.value.members.map { it.userId }.toSet()
        val pending = roomId?.let { (getPendingInvites(it) as? AppResult.Success)?.data }.orEmpty()
        val friends = (getFriends() as? AppResult.Success)?.data.orEmpty()
            .filter { it.userId !in members }
            .sortedWith(compareBy({ it.userId in pending }, { !it.online }, { it.name.lowercase() }))
        updateAddFriends { copy(friends = friends, pending = pending, loading = false) }
    }

    private suspend fun sendInvites() {
        val sheet = state.value.sheet as? LobbySheet.AddFriends ?: return
        val id = roomId ?: return
        if (!sheet.canSend || sheet.sending) return
        updateAddFriends { copy(sending = true, error = null) }
        val codes = listOf(sheet.code).filter { it.length == AppConstants.USER_CODE_LENGTH }
        when (inviteFriends(id, sheet.selected.toList(), codes)) {
            is AppResult.Success -> setState { copy(sheet = null) }
            is AppResult.Failure -> updateAddFriends { copy(sending = false, error = RoomStrings.lobbyAddFriendsFailed) }
        }
    }

    private fun updateAddFriends(change: LobbySheet.AddFriends.() -> LobbySheet.AddFriends) {
        setState { copy(sheet = (sheet as? LobbySheet.AddFriends)?.change() ?: sheet) }
    }

    /** Edit Name: the server renames the group and everyone sees it in the next room update. */
    private suspend fun saveName() {
        val name = (state.value.sheet as? LobbySheet.EditName)?.name?.trim().orEmpty()
        if (name.isEmpty()) return
        send(LobbyCommand.RenameRoom(name))
        setState { copy(sheet = null) }
    }

    /** The room's members ranked by wins first; Most coins re-ranks the same records. */
    private suspend fun openLeaderboard() {
        val id = roomId ?: return
        showModal(LobbyModal.Leaderboard(RoomLeaderboardUi()))
        val board = when (val result = getRoomLeaderboard(id)) {
            is AppResult.Success -> {
                boardRecords = result.data
                RoomLeaderboardUi(rows = RoomLeaderboardUiMapper.rows(boardRecords, RoomBoardTab.WINS, data?.myUserId), loading = false)
            }
            is AppResult.Failure -> RoomLeaderboardUi(loading = false, error = RoomStrings.boardFailed)
        }
        if (userModal is LobbyModal.Leaderboard) showModal(LobbyModal.Leaderboard(board))
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
        // The mic listens only while it's on in the room and the room is on screen.
        micGate.update(micOn = me?.micOn == true, inFront = foreground)
        voiceNeed.update(micGate.open, me?.speakerOn != false, room.members.filter { it.userId != d.myUserId && it.online }.associate { it.userId to it.micOn })
        val game = d.selectedGame
        manageCountdownTicker(room)
        setState {
            copy(
                loaded = true,
                roomName = room.name,
                isLeader = me?.isLeader == true,
                isAdmin = me?.isAdmin == true,
                temporary = room.temporary,
                saveRoom = saveRoomButton(room, me),
                members = LobbyRules.sortMembers(room.members, d.myUserId).map { it.toUi(d) },
                membersLabel = RoomStrings.lobbyPlayersOnline(room.members.count { it.online }, room.members.size),
                games = d.games,
                focusedIndex = focusedIndex.coerceIn(0, (d.games.size - 1).coerceAtLeast(0)),
                selectedGame = game,
                houseRules = game?.let { houseRulesFor(room, it) }.orEmpty(),
                leagues = leaguesFor(room),
                entryCoins = room.entryCoins,
                fixedRules = game?.fixedRules.orEmpty(),
                primary = me?.let { primaryFor(room, it, game) } ?: LobbyPrimaryButtonUi(),
                micOn = me?.micOn == true,
                speakerOn = me?.speakerOn == true,
                waitingList = room.waitingList.map { WaitingRequestUi(it.userId, it.name) },
                modal = countdownModal(room, me) ?: userModal,
            )
        }
    }

    private fun LobbyMember.toUi(d: LobbyData): LobbyMemberUi {
        // Others' talking comes from the server; the user's own from their mic.
        val talking = online && micOn && if (userId == d.myUserId) mySpeech == MySpeech.SPEAKING else userId in d.speakingUserIds
        return LobbyMemberUi(
            userId = userId,
            avatarId = avatarId,
            name = name,
            online = online,
            isMe = userId == d.myUserId,
            isLeader = isLeader,
            isAdmin = isAdmin,
            talking = talking,
            // Others' from the room, the user's own from their mic at once.
            talkingMuted = online && !micOn && if (userId == d.myUserId) mySpeech == MySpeech.SPEAKING_MUTED else talkingMuted,
            micOff = online && !micOn,
            status = when {
                !online -> LobbyMemberStatus.OFFLINE
                d.selectedGame == null -> LobbyMemberStatus.NONE
                isLeader || ready -> LobbyMemberStatus.READY
                watching -> LobbyMemberStatus.WATCHING
                else -> LobbyMemberStatus.NOT_READY
            },
        )
    }

    /** Every league, cheapest first; the ones someone in the lobby can't pay are locked. */
    private fun leaguesFor(room: LobbyRoom): List<LeagueUi> {
        val fees = room.leagueFees.sorted()
        return fees.mapIndexed { rank, fee ->
            LeagueUi(
                entryCoins = fee,
                name = CoinsStrings.leagueName(rank, fees.size),
                feeLabel = if (fee == 0) CoinsStrings.leagueFree else CoinsStrings.leagueFee(TextUtils.groupedThousands(fee)),
                canEnter = fee <= room.affordableEntry,
            )
        }
    }

    /** "Fill with bots" first, then the game own toggles. */
    private fun houseRulesFor(room: LobbyRoom, game: GameCatalogItem): List<HouseRuleUi> =
        listOf(HouseRuleUi(BOT_FILL_KEY, RoomStrings.lobbyBotFillTitle, RoomStrings.lobbyBotFillDescription, room.botFill)) +
            game.houseRuleOptions.map {
                HouseRuleUi(it.key, it.title, it.description, room.houseRules[it.key] ?: it.defaultEnabled)
            }

    private fun primaryFor(room: LobbyRoom, me: LobbyMember, game: GameCatalogItem?): LobbyPrimaryButtonUi {
        if (room.matchInProgress != null) {
            return LobbyPrimaryButtonUi(RoomStrings.lobbyWatchMatch, RoomStrings.lobbyMatchInProgress, enabled = true, active = true, isStart = true)
        }
        if (me.isLeader) {
            val canStart = LobbyRules.canStart(room, game)
            return LobbyPrimaryButtonUi(
                label = RoomStrings.lobbyStartMatch,
                sublabel = game?.let { RoomStrings.lobbyPlayersReady(LobbyRules.readyCount(room), it.maxPlayers) }
                    ?: RoomStrings.lobbyPickGameFirst,
                enabled = canStart,
                active = canStart,
                isStart = true,
            )
        }
        return when (LobbyRules.playerButtonState(room, me, game)) {
            PlayerButtonState.DISABLED -> LobbyPrimaryButtonUi(RoomStrings.lobbyReadyButton, RoomStrings.lobbyWaitingForGame)
            PlayerButtonState.NOT_READY -> LobbyPrimaryButtonUi(RoomStrings.lobbyNotReadyButton, RoomStrings.lobbyTapWhenReady, enabled = true)
            PlayerButtonState.READY -> LobbyPrimaryButtonUi(RoomStrings.lobbyReadyButton, RoomStrings.lobbySeatSaved, enabled = true, active = true)
            PlayerButtonState.NOT_WATCHING -> LobbyPrimaryButtonUi(RoomStrings.lobbyNotWatchingButton, RoomStrings.lobbyTapToWatch, enabled = true)
            PlayerButtonState.WATCHING -> LobbyPrimaryButtonUi(RoomStrings.lobbyWatchingButton, RoomStrings.lobbyTapToStopWatching, enabled = true, active = true)
        }
    }

    private fun countdownModal(room: LobbyRoom, me: LobbyMember?): LobbyModal? {
        val endsAt = room.countdownEndsAt ?: return null
        val seconds = (endsAt - clock.now()).inWholeSeconds.coerceAtLeast(0)
        return LobbyModal.Countdown(
            title = RoomStrings.lobbyStartingIn(seconds),
            canCancel = me != null && (me.isLeader || me.ready),
        )
    }

    /**
     * A countdown just stopped without a match: everyone is told why and who stopped it, so a person
     * cancelling is never mistaken for the server failing (or the other way round). Only fresh stops,
     * once each: one from before the user came in is not news.
     */
    private fun tellCountdownStop(d: LobbyData) {
        val stop = d.room.countdownStop ?: return
        if (stop == toldStop) return
        toldStop = stop
        if (clock.now() - stop.at > AppConstants.COUNTDOWN_STOP_NEWS_MS.milliseconds) return
        userModal = LobbyModal.CountdownStopped(CountdownStopText.of(stop, d.myUserId))
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
        MemberMenuOption.REMOVE_FROM_ROOM -> LobbyCommand.RemoveMember(userId)
    }

    private companion object {
        /** House-rule key for the room-level bot fill option. */
        const val BOT_FILL_KEY = "bot_fill"
    }
}
