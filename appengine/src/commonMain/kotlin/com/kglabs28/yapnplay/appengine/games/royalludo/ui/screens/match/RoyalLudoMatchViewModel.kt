package com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match

import com.kglabs28.yapnplay.appengine.ui.components.moderation.EndVoteUiMapper
import com.kglabs28.yapnplay.appengine.features.voice.usecase.TableVoiceUseCases
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardResult
import com.kglabs28.yapnplay.appengine.features.matchresult.domain.SeatResult
import com.kglabs28.yapnplay.appengine.features.matchresult.usecase.ObserveMatchCoinsUseCase
import com.kglabs28.yapnplay.appengine.core.common.TextUtils
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import com.kglabs28.yapnplay.appengine.appmanager.helpers.AppManagerHelper
import com.kglabs28.yapnplay.appengine.appmanager.helpers.GamesAppManagerHelper
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardUi
import com.kglabs28.yapnplay.appengine.core.audio.TurnAlarm
import com.kglabs28.yapnplay.appengine.ui.components.table.PlayerFriendActionUi
import com.kglabs28.yapnplay.appengine.features.profile.usecase.PlayerFriendUseCases
import com.kglabs28.yapnplay.appengine.core.network.MatchGoneException
import com.kglabs28.yapnplay.appengine.core.audio.GameAudio
import com.kglabs28.yapnplay.appengine.core.audio.GameSound
import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ModerationRules
import com.kglabs28.yapnplay.appengine.features.moderation.domain.Penalty
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ReportVoteRequest
import com.kglabs28.yapnplay.appengine.features.moderation.usecase.ModerationUseCases
import com.kglabs28.yapnplay.appengine.features.voice.usecase.ObserveMySpeakingUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.SetMicOpenUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.ObserveMicOnUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.SetMicOnUseCase
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatHandoverUiMapper
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoCommand
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoEvent
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoMatch
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoPhase
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoPresence
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoUpdate
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.usecase.ObserveLudoMatchUseCase
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.usecase.ObserveMatchSpeakingUseCase
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.usecase.SendLudoCommandUseCase
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.mapper.LudoMatchUiMapper
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.mapper.LudoModerationUiMapper
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.LudoBoardGeometry
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.PROGRESS_IN_BASE
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.ROYAL_LUDO_TURN_SECONDS
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds
import kotlinx.datetime.Instant

/**
 * Royal Ludo table. Server updates are queued and played one event at a
 * time with sound — the dice tumbles (in the roller's colour), a piece
 * hops square by square, a captured piece or an undone turn glides home —
 * and only then does the new turn show. The dice hides while a piece is
 * being chosen. A ticker keeps timers moving. Mic changes ask for
 * permission first. Other players can be muted for the user only. Voice
 * reports go to everyone present for confirmation. In the background
 * the table goes quiet, the mic goes off and (after a short grace) the
 * server is told the user is away, so it plays for them; nothing
 * animates until they are back. In a room match a leaving player hands
 * their seat to a spectator or the bot; the named spectator, or the
 * leader after them, answers here while the match waits.
 */
class RoyalLudoMatchViewModel(
    private val observeMatch: ObserveLudoMatchUseCase,
    private val observeSpeaking: ObserveMatchSpeakingUseCase,
    private val sendCommand: SendLudoCommandUseCase,
    private val voice: TableVoiceUseCases,
    private val observeMySpeaking: ObserveMySpeakingUseCase,
    private val setMicOpen: SetMicOpenUseCase,
    private val observeMicOn: ObserveMicOnUseCase,
    private val setMicOn: SetMicOnUseCase,
    private val moderation: ModerationUseCases,
    private val gamesConfig: GamesAppManagerHelper,
    private val appConfig: AppManagerHelper,
    private val playerFriends: PlayerFriendUseCases,
    private val audio: GameAudio,
    private val observeMatchCoins: ObserveMatchCoinsUseCase,
    private val clock: AppClock,
    dispatchers: DispatcherProvider,
) : MviViewModel<RoyalLudoMatchState, RoyalLudoMatchIntent, RoyalLudoMatchEffect>(RoyalLudoMatchState(), dispatchers) {

    private var matchId: String? = null
    private var match: LudoMatch? = null
    private var speaking: Set<String> = emptySet()
    /** The user speaking into their mic, and whether that mic is open (on at the table, app in front). */
    private var mySpeaking = false
    /** The app-wide mic switch the table follows. */
    private var micSwitch = false
    private var micOpen = false
    private var mutedForMe: Set<String> = emptySet()
    private var speakerOn = true
    /** Progress shown per seat (lags the server while events animate). */
    private val shown = mutableMapOf<Int, MutableList<Int>>()
    private val motions = mutableMapOf<String, PieceMotion>()
    private var playing = false
    private var diceVisible = true
    private var rollId = 0
    private var diceValue = 1
    private var diceSeat: Int? = null
    private var userModal: LudoMatchModal? = null
    private var vote: ReportVoteRequest? = null
    private var penalty: Penalty? = null
    private var foreground = true
    private var leaderboard: LeaderboardUi? = null
    /** The coins and XP MatchEnded paid (it arrives just after the last snapshot). */
    private var seatResults: List<SeatResult> = emptyList()
    private val turnAlarm = TurnAlarm(hurrySeconds = { gamesConfig.turnHurrySeconds() }) { if (foreground) audio.play(it) }
    private var awayJob: Job? = null
    private var markedAway = false
    private var declinedSeat: Int? = null
    /** The spectator picked in the leader's or idle player's dialog (null: none yet). */
    private var seatPick: Int? = null
    private var leaveDeadline: Instant? = null
    private val queue = Channel<LudoUpdate>(Channel.UNLIMITED)
    private var jobs: List<Job> = emptyList()

    override suspend fun handleIntent(intent: RoyalLudoMatchIntent) {
        when (intent) {
            is RoyalLudoMatchIntent.Load -> load(intent.matchId)
            RoyalLudoMatchIntent.Roll -> if (state.value.canRoll) send(LudoCommand.Roll)
            is RoyalLudoMatchIntent.PieceClicked ->
                if (state.value.pieces.any { it.seat == intent.seat && it.piece == intent.piece && it.selectable }) {
                    send(LudoCommand.Move(intent.piece))
                }
            RoyalLudoMatchIntent.ToggleAutoplay -> toggleAutoplay((match?.me?.let { it.autoplay || it.presence == LudoPresence.BOT } == true)) { send(LudoCommand.SetAutoplay(it)) }
            // The mic switch is the user's, everywhere: this table follows it.
            is RoyalLudoMatchIntent.ToggleMic -> when {
                micSwitch -> setMicOn(false)
                intent.permissionGranted -> setMicOn(true)
                else -> emitEffect(RoyalLudoMatchEffect.RequestMicPermission)
            }
            is RoyalLudoMatchIntent.MicPermissionResult ->
                if (intent.granted) setMicOn(true) else showModal(LudoMatchModal.MicPermissionNeeded)
            RoyalLudoMatchIntent.OpenAppSettings -> {
                showModal(null)
                emitEffect(RoyalLudoMatchEffect.OpenAppSettings)
            }
            RoyalLudoMatchIntent.ToggleSpeaker -> voice.setSpeakerOn(!speakerOn)
            is RoyalLudoMatchIntent.ToggleHearSpectator -> voice.setMuted(intent.userId, intent.userId !in mutedForMe)
            is RoyalLudoMatchIntent.ToggleHear -> match?.players?.firstOrNull { it.seat == intent.seat }?.let {
                voice.setMuted(it.userId, it.userId !in mutedForMe)
            }
            // Not the user, a CPU or a seat the CPU plays for someone who left.
            is RoyalLudoMatchIntent.PlayerLongPressed -> match?.players?.firstOrNull { it.seat == intent.seat }
                ?.takeIf { !it.isMe && !it.isCpu && it.presence != LudoPresence.BOT }
                ?.let { openFriendAction(it.userId, it.name, it.avatarId) }
            RoyalLudoMatchIntent.ConfirmFriendAction -> confirmFriendAction()
            RoyalLudoMatchIntent.DismissFriendAction -> setState { copy(friendAction = null) }
            RoyalLudoMatchIntent.ExitClicked -> match?.let { m ->
                // Closes by itself if no choice is made in time.
                leaveDeadline = clock.now() + AppConstants.EXIT_DIALOG_SECONDS.seconds
                showModal(LudoMatchModal.LeaveConfirm(LudoMatchUiMapper.leaveBody(m), LudoMatchUiMapper.leaveOptions(m), selectedIndex = 0))
            }
            RoyalLudoMatchIntent.ConfirmLeave -> {
                val pick = (userModal as? LudoMatchModal.LeaveConfirm)?.selectedIndex ?: 0
                showModal(null)
                // Only a player has a seat to hand on; a spectator just goes.
                if (match?.me != null) send(LudoCommand.Leave(SeatHandoverUiMapper.giveTo(pick, spectatorIds(), withBot = true)))
                emitEffect(RoyalLudoMatchEffect.Close)
            }
            is RoyalLudoMatchIntent.PickSeatTaker -> when (val modal = userModal) {
                is LudoMatchModal.LeaveConfirm -> showModal(modal.copy(selectedIndex = intent.index))
                else -> {
                    seatPick = intent.index
                    render()
                }
            }
            is RoyalLudoMatchIntent.AnswerSeatOffer -> send(LudoCommand.AnswerSeatOffer(intent.accept))
            RoyalLudoMatchIntent.StillHere -> send(LudoCommand.StillHere)
            RoyalLudoMatchIntent.InviteToSeat -> leaderGives(SeatHandoverUiMapper.giveTo(seatPick, spectatorIds(), withBot = false) ?: return)
            RoyalLudoMatchIntent.OfferSeatToBot -> leaderGives(null)
            RoyalLudoMatchIntent.Spectate -> {
                // The seat goes to the pick (the bot by default); the user stays and watches.
                val giveTo = SeatHandoverUiMapper.giveTo(seatPick ?: 0, spectatorIds(), withBot = true)
                seatPick = null
                send(LudoCommand.Leave(giveTo))
            }
            RoyalLudoMatchIntent.DismissModal -> if (matchGone) emitEffect(RoyalLudoMatchEffect.Close) else showModal(null)
            RoyalLudoMatchIntent.OpenSettings -> showSettings()
            RoyalLudoMatchIntent.ProposeEnd -> {
                showModal(null)
                send(LudoCommand.ProposeEnd)
            }
            is RoyalLudoMatchIntent.VoteEnd -> send(LudoCommand.VoteEnd(intent.agree))
            RoyalLudoMatchIntent.ToggleEffects -> {
                audio.setEffectsOn(!audio.settings.value.effectsOn)
                showSettings()
            }
            RoyalLudoMatchIntent.ToggleMusic -> {
                audio.setMusicOn(!audio.settings.value.musicOn)
                showSettings()
            }
            RoyalLudoMatchIntent.OpenReport -> showReport(selected = null, error = null)
            is RoyalLudoMatchIntent.SelectReportPerson -> showReport(selected = intent.userId, error = null)
            RoyalLudoMatchIntent.SubmitReport -> submitReport()
            is RoyalLudoMatchIntent.Vote -> vote?.let {
                vote = null
                render()
                moderation.castVote(it.reportId, intent.agree)
            }
            RoyalLudoMatchIntent.WatchAd -> watchAd()
            is RoyalLudoMatchIntent.ScreenVisible -> onScreenVisible(intent.visible)
            is RoyalLudoMatchIntent.TakeSeat -> {
                declinedSeat = intent.seat
                send(LudoCommand.TakeSeat(intent.seat))
            }
            is RoyalLudoMatchIntent.DeclineSeat -> {
                declinedSeat = intent.seat
                render()
            }
            RoyalLudoMatchIntent.Done -> emitEffect(RoyalLudoMatchEffect.Close)
            RoyalLudoMatchIntent.ShareResult -> leaderboard?.let { emitEffect(RoyalLudoMatchEffect.ShareText(TableStrings.leaderboardShareText(it.game, it.myPlace))) }
        }
    }

    /** The backend no longer has this match; closing the message closes the table. */
    private var matchGone = false

    private fun load(id: String) {
        if (matchId == id) return
        matchId = id
        jobs.forEach { it.cancel() }
        jobs = listOf(
            scope.launch {
                try {
                    observeMatch(id).collect { queue.send(it) }
                } catch (e: MatchGoneException) {
                    // The server lost the match (e.g. it restarted): say so, then close the table.
                    matchGone = true
                    showModal(LudoMatchModal.ReportOutcome(TableStrings.matchGoneTitle, TableStrings.matchGoneBody))
                }
            },
            scope.launch { for (update in queue) play(update) },
            scope.launch { observeMatchCoins().collect { seatResults = it; leaderboard = leaderboard?.let { b -> LeaderboardResult.withResult(b, it) }; render() } },
            scope.launch { observeSpeaking(id).collect { speaking = it; render() } },
            scope.launch { observeMySpeaking().collect { mySpeaking = it; render() } },
            scope.launch { observeMicOn().collect { micSwitch = it; syncMic() } },
            scope.launch { voice.observeMuted().collect { mutedForMe = it; render() } },
            scope.launch { voice.observeSpeakerOn().collect { speakerOn = it; render() } },
            scope.launch { moderation.voteRequests().collect { vote = it; render() } },
            scope.launch {
                moderation.outcomes().collect {
                    showModal(LudoModerationUiMapper.outcome(it))
                }
            },
            scope.launch {
                moderation.penalty().collect { new ->
                    val fresh = new != null && penalty == null
                    penalty = new
                    if (fresh) showPenalty(watchingAd = false) else if (new == null && userModal is LudoMatchModal.Penalty) showModal(null)
                }
            },
            scope.launch {
                while (true) {
                    val request = vote
                    if (request != null && clock.now() >= request.expiresAt) vote = null
                    if (userModal is LudoMatchModal.LeaveConfirm && leaveDeadline?.let { clock.now() >= it } == true) userModal = null
                    render()
                    delay(AppConstants.LUDO_TIMER_TICK_MS)
                }
            },
        )
    }

    /** Animates [update]'s events in order with sound, then shows the new turn. */
    private suspend fun play(update: LudoUpdate) {
        // First snapshot, or in the background: no animation, straight to the latest board.
        if (match == null || !foreground) {
            reset(update.match)
            return
        }
        playing = true
        for (event in update.events) {
            when (event) {
                is LudoEvent.Rolled -> {
                    diceValue = event.value
                    diceSeat = event.seat
                    diceVisible = true
                    rollId++
                    sound(GameSound.DICE_ROLL)
                    render()
                    // Every roll is shown in full, and rests on its face, before anything moves.
                    delay(AppConstants.DICE_ROLL_MS + AppConstants.LUDO_DICE_REST_MS)
                }
                is LudoEvent.Moved -> {
                    diceVisible = false
                    val steps = if (event.from == PROGRESS_IN_BASE) listOf(0) else (event.from + 1)..event.to
                    for (progress in steps) {
                        place(event.seat, event.piece, progress, PieceMotion.HOP)
                        sound(GameSound.HOP)
                        delay(AppConstants.LUDO_HOP_MS.toLong())
                    }
                }
                is LudoEvent.Captured -> {
                    place(event.victimSeat, event.victimPiece, PROGRESS_IN_BASE, PieceMotion.SLIDE)
                    sound(GameSound.CAPTURE)
                    delay(AppConstants.LUDO_SLIDE_MS.toLong())
                }
                is LudoEvent.Reverted -> {
                    // Third six: every piece glides back to where the turn began.
                    motions.clear()
                    event.pieces.forEach { (seat, list) ->
                        list.forEachIndexed { piece, progress ->
                            if (shown[seat]?.getOrNull(piece) != progress) {
                                shown[seat]?.set(piece, progress)
                                motions[LudoMatchUiMapper.pieceId(seat, piece)] = PieceMotion.SLIDE
                            }
                        }
                    }
                    sound(GameSound.CAPTURE)
                    render()
                    delay(AppConstants.LUDO_SLIDE_MS.toLong())
                }
                is LudoEvent.TurnPassed -> Unit
            }
        }
        playing = false
        reset(update.match)
    }

    /**
     * Background: sound stops at once, the mic goes off, and after a short
     * grace (a glance at a notification is ignored) the server is told the
     * user is away. Front again: they are back and the board is current.
     */
    private suspend fun onScreenVisible(visible: Boolean) {
        foreground = visible
        // The mic is off at the table while the app is away, and back on (if switched on) when it returns.
        syncMic()
        awayJob?.cancel()
        if (visible) {
            audio.startMusic()
            if (markedAway) {
                markedAway = false
                send(LudoCommand.SetAway(false))
            }
        } else {
            audio.stopMusic()
            awayJob = scope.launch {
                delay(AppConstants.LUDO_AWAY_GRACE_MS)
                markedAway = true
                send(LudoCommand.SetAway(true))
            }
        }
    }

    /** The leader's choice: the picked spectator, or the bot (null). */
    private suspend fun leaderGives(giveTo: String?) {
        seatPick = null
        send(LudoCommand.ChooseSeatTaker(giveTo))
    }

    /** The exit dialog shows how long it stays open. */
    private fun LudoMatchModal.withCountdown(): LudoMatchModal {
        if (this !is LudoMatchModal.LeaveConfirm) return this
        val left = leaveDeadline?.let { (it - clock.now()).inWholeSeconds.coerceAtLeast(0) } ?: 0L
        return copy(countdown = TableStrings.seatCountdown(left))
    }

    private fun spectatorIds() = match?.spectators?.map { it.userId }.orEmpty()

    /** Tells the table the user's mic state: the app-wide switch, off while the app is in the background. */
    private fun syncMic() {
        val m = match ?: return
        val current = (m.me?.micOn ?: m.spectators.firstOrNull { it.userId == m.viewerUserId }?.micOn) ?: return
        val want = micSwitch && foreground
        if (current != want) scope.launch { send(LudoCommand.SetMic(want)) }
    }

    override fun onCleared() {
        if (micOpen) setMicOpen(false)
    }

    private fun sound(sound: GameSound) {
        if (foreground) audio.play(sound)
    }

    /** Shows the server's pieces as they are (after any animation). */
    private fun reset(newMatch: LudoMatch) {
        val newlyFinished = newMatch.finishedSeats.size > (match?.finishedSeats?.size ?: newMatch.finishedSeats.size)
        match = newMatch
        syncMic()
        shown.clear()
        newMatch.pieces.forEach { (seat, list) -> shown[seat] = list.toMutableList() }
        motions.clear()
        diceSeat = newMatch.currentSeat
        // Hidden from the roll until the move is made (a choice, a single
        // automatic move, or a six's move); back for the next roll.
        diceVisible = newMatch.phase == LudoPhase.ROLL
        if (newlyFinished) sound(GameSound.WIN)
        if (newMatch.phase == LudoPhase.FINISHED && leaderboard == null) {
            leaderboard = LudoMatchUiMapper.leaderboard(newMatch, LudoMatchUiMapper.elapsed(clock.now() - newMatch.startedAt)).copy(shareLink = appConfig.playStoreUrl()).let { LeaderboardResult.withResult(it, seatResults) }
        }
        render()
    }

    /** Moves one piece; every other piece keeps still (no sympathetic hops). */
    private fun place(seat: Int, piece: Int, progress: Int, motion: PieceMotion) {
        shown[seat]?.set(piece, progress)
        motions.clear()
        motions[LudoMatchUiMapper.pieceId(seat, piece)] = motion
        render()
    }

    private fun showModal(modal: LudoMatchModal?) {
        userModal = modal
        render()
    }

    private fun showSettings() {
        val s = audio.settings.value
        showModal(LudoMatchModal.Settings(effectsOn = s.effectsOn, musicOn = s.musicOn, canEndGame = canEndGame()))
    }

    private fun showReport(selected: String?, error: String?) {
        val m = match ?: return
        val people = LudoModerationUiMapper.reportablePeople(m)
        val voters = LudoModerationUiMapper.voters(m)
        showModal(
            LudoMatchModal.Report(
                people = people,
                selectedUserId = selected,
                warning = LudoStrings.ludoReportWarning(ModerationRules.requiredAgreements(voters)),
                error = error ?: if (ModerationRules.canReport(voters)) null else LudoStrings.ludoReportNeedPeople,
            ),
        )
    }

    private suspend fun submitReport() {
        val m = match ?: return
        val report = userModal as? LudoMatchModal.Report ?: return
        val person = report.people.firstOrNull { it.userId == report.selectedUserId } ?: return
        when (moderation.report(m.matchId, person.userId, person.name, LudoModerationUiMapper.voters(m))) {
            is AppResult.Success -> showModal(null)
            is AppResult.Failure -> showReport(report.selectedUserId, LudoStrings.ludoReportNeedPeople)
        }
    }

    private fun showPenalty(watchingAd: Boolean) {
        val p = penalty ?: return
        showModal(LudoModerationUiMapper.penalty(p, clock.now(), watchingAd, adOffered = watchingAd || moderation.isAdReady()))
    }

    private suspend fun watchAd() {
        showPenalty(watchingAd = true)
        if (moderation.clearPenaltyWithAd() is AppResult.Failure) showPenalty(watchingAd = false)
    }

    private fun render() {
        val m = match ?: return
        val me = m.me
        if (autoplayWanted != null && autoplayWanted == (me?.let { it.autoplay || it.presence == LudoPresence.BOT } == true)) autoplayWanted = null
        // The mic listens only while it's on at the table and the app is in front.
        val wantMic = foreground && (me?.micOn ?: m.spectators.firstOrNull { it.userId == m.viewerUserId }?.micOn ?: false)
        if (wantMic != micOpen) {
            micOpen = wantMic
            setMicOpen(wantMic)
        }
        // Others' talking comes from the server; the user's own from their mic.
        val talkers = if (micOpen && mySpeaking) speaking + m.viewerUserId else speaking
        val rotation = me?.let { LudoBoardGeometry.rotationFor(it.colour.ordinal) } ?: 0
        val myTurn = me != null && !playing && !m.onHold && m.currentSeat == me.seat && !me.autoplay
        val remainingMs = m.turnEndsAt?.let { (it - clock.now()).inWholeMilliseconds.coerceAtLeast(0) } ?: 0L
        turnAlarm.update(myTurn, remainingMs)
        val cards = LudoMatchUiMapper.cards(
            m,
            LudoMatchUiMapper.CardInputs(rotation, talkers, mutedForMe, if (playing || m.onHold) null else remainingMs, ROYAL_LUDO_TURN_SECONDS),
        )
        setState {
            copy(
                // The view model's own value (inside copy, a bare name would be the old state's).
                leaderboard = this@RoyalLudoMatchViewModel.leaderboard,
                loaded = true,
                title = LudoStrings.ludoTitle,
                subtitle = TableStrings.tableSubtitle(listOfNotNull(m.title)),
                rotation = rotation,
                pieces = LudoMatchUiMapper.pieces(m, shown, motions, selectable = if (myTurn && m.phase == LudoPhase.MOVE) m.legalPieces else emptyList()),
                myCard = cards.firstOrNull { it.isMe },
                spectating = me == null,
                leftCards = if (me == null) LudoMatchUiMapper.side(cards, left = true) else emptyList(),
                rightCards = if (me == null) LudoMatchUiMapper.side(cards, left = false) else emptyList(),
                opponents = cards.filterNot { it.isMe },
                basePlaces = LudoMatchUiMapper.basePlaces(m),
                audience = LudoMatchUiMapper.audience(m, talkers, mutedForMe),
                spectatorCount = m.spectatorCount.toString(),
                spectatorLabel = LudoStrings.ludoSpectators,
                // The clock stops once the match is over.
                elapsed = if (m.phase == LudoPhase.FINISHED && elapsed.isNotEmpty()) elapsed else LudoMatchUiMapper.elapsed(clock.now() - m.startedAt),
                dice = LudoDiceUi(diceValue, rollId, m.players.firstOrNull { it.seat == diceSeat }?.colour, diceVisible),
                canRoll = myTurn && m.phase == LudoPhase.ROLL,
                seatBanner = LudoMatchUiMapper.seatBanner(m, clock.now()),
                roomMatch = m.isRoomMatch,
                moveHint = m.lastRoll?.takeIf { myTurn && m.phase == LudoPhase.MOVE }?.let { LudoStrings.ludoMove(it) },
                // On when the bot holds the seat too; turning it off takes the seat back.
                // What the user just set wins until the server agrees (no waiting on the round trip).
                autoplay = autoplayWanted ?: (me?.autoplay == true || me?.presence == LudoPresence.BOT),
                // A spectator's mic is on their spectator entry.
                // The app-wide mic switch, at once (the server catches up).
                micOn = micSwitch,
                speakerOn = speakerOn,
                // A vote or a seat decision is timed, so it comes before the user's own dialogs.
                modal = vote?.let { LudoModerationUiMapper.vote(it, clock.now()) }
                    // The end-game vote pauses play until it is decided.
                    ?: m.endVote?.let { LudoMatchModal.EndVote(EndVoteUiMapper.of(it, m.me?.seat, m.players.firstOrNull { p -> p.seat == it.proposerSeat }?.name.orEmpty(), LudoStrings.ludoEndRanking, clock.now())) }
                    ?: LudoMatchUiMapper.seatModal(m, clock.now(), declinedSeat, seatPick)
                    ?: userModal?.withCountdown(),
            )
        }
    }

    /** Whether the user may ask to end the game now (none running). */
    private fun canEndGame(): Boolean {
        val m = match ?: return false
        val me = m.me ?: return false
        return EndVoteUiMapper.canPropose(me.isCpu, me.presence.name, m.phase == LudoPhase.FINISHED, m.endVote != null)
    }

    private suspend fun send(command: LudoCommand) {
        val id = matchId ?: return
        sendCommand(id, command)
    }

    /** Long press on [player]'s card: confirm adding them (or removing them if already a friend). */
    private suspend fun openFriendAction(userId: String, name: String, avatarId: String) {
        val isFriend = userId in playerFriends.friendIds()
        setState { copy(friendAction = PlayerFriendActionUi(userId, name, avatarId, isFriend)) }
    }

    private suspend fun confirmFriendAction() {
        val action = state.value.friendAction ?: return
        if (action.busy) return
        setState { copy(friendAction = action.copy(busy = true, error = null)) }
        when (playerFriends.toggle(action.userId, action.isFriend)) {
            is AppResult.Success -> setState { copy(friendAction = null) }
            is AppResult.Failure -> setState { copy(friendAction = action.copy(busy = false, error = TableStrings.playerFriendFailed)) }
        }
    }

    /** Autoplay as the user just set it, shown at once until the server agrees (or gives up after a few seconds). */
    private var autoplayWanted: Boolean? = null
    private var autoplayJob: Job? = null

    private suspend fun toggleAutoplay(serverValue: Boolean, send: suspend (Boolean) -> Unit) {
        val next = !(autoplayWanted ?: serverValue)
        autoplayWanted = next
        autoplayJob?.cancel()
        autoplayJob = scope.launch {
            delay(AppConstants.TOGGLE_CONFIRM_MS)
            autoplayWanted = null
            render()
        }
        render()
        send(next)
    }
}
