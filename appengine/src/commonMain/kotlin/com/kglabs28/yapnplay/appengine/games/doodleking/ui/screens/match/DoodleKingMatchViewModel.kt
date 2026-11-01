package com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match

import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleFeedKind
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatHandoverController
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatHandoverAction
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.LeaveDialogState
import com.kglabs28.yapnplay.appengine.core.audio.GameAudio
import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.core.mvi.playTableUpdate
import com.kglabs28.yapnplay.appengine.core.network.MatchGoneException
import com.kglabs28.yapnplay.appengine.core.ui.DoodleStrings
import com.kglabs28.yapnplay.appengine.features.matchresult.domain.SeatResult
import com.kglabs28.yapnplay.appengine.features.matchresult.usecase.ObserveMatchCoinsUseCase
import com.kglabs28.yapnplay.appengine.features.voice.domain.MicGate
import com.kglabs28.yapnplay.appengine.features.voice.domain.MySpeech
import com.kglabs28.yapnplay.appengine.features.voice.domain.VoiceNeed
import com.kglabs28.yapnplay.appengine.features.voice.usecase.ObserveMicOnUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.ObserveMySpeakingUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.SetMicOnUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.SetMicOpenUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.TableVoiceUseCases
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleCommand
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleMatch
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodlePhase
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleUpdate
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.usecase.ObserveDoodleInkUseCase
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.usecase.ObserveDoodleMatchUseCase
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.usecase.ObserveDoodleSpeakingUseCase
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.usecase.SendDoodleCommandUseCase
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.usecase.SendDoodleInkUseCase
import com.kglabs28.yapnplay.appengine.games.doodleking.identity.DoodleKingConstants
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.mapper.DoodleTableMapper
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.mapper.DoodleVoiceInputs
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardResult
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Doodle King table. The server runs the turns; this shows them: the
 * drawer's tools and word, the guessers' hint and guess box, the turn's
 * guesses and the live leaderboard. The drawing lives on [board]: the
 * drawer's strokes go out every 60 ms, everyone else's board follows the
 * drawer's. The drawer's mic stays closed all their turn.
 */
class DoodleKingMatchViewModel(
    private val observeMatch: ObserveDoodleMatchUseCase,
    private val observeInk: ObserveDoodleInkUseCase,
    private val observeSpeaking: ObserveDoodleSpeakingUseCase,
    private val sendCommand: SendDoodleCommandUseCase,
    private val sendInk: SendDoodleInkUseCase,
    private val voice: TableVoiceUseCases,
    private val observeMySpeaking: ObserveMySpeakingUseCase,
    private val setMicOpen: SetMicOpenUseCase,
    private val observeMicOn: ObserveMicOnUseCase,
    private val setMicOn: SetMicOnUseCase,
    private val observeMatchCoins: ObserveMatchCoinsUseCase,
    private val seats: SeatHandoverController,
    private val audio: GameAudio,
    private val clock: AppClock,
    dispatchers: DispatcherProvider,
) : MviViewModel<DoodleKingMatchState, DoodleKingMatchIntent, DoodleKingMatchEffect>(DoodleKingMatchState(), dispatchers) {

    private var matchId: String? = null
    private var match: DoodleMatch? = null
    private val board = DoodleBoard()
    /** Voice only while another person is here and someone's mic is on. */
    private val voiceNeed = VoiceNeed { wanted, talkers -> matchId?.let { voice.setVoiceWanted.match(it, wanted, talkers) } }
    private val micGate = MicGate { open, listenMuted -> setMicOpen(open, listenMuted) }
    private var speaking: Set<String> = emptySet()
    private var mutedForMe: Set<String> = emptySet()
    private var mutedTalkers: Set<String> = emptySet()
    private var mySpeech = MySpeech.SILENT
    private var micSwitch = false
    private var speakerOn = true
    private var foreground = true
    private var modal: DoodleModal? = null
    /** Every table's exit dialog (who plays on for the user). */
    private val leave = LeaveDialogState()
    private var leaderboard: LeaderboardUi? = null
    private var seatResults: List<SeatResult> = emptyList()
    /** Sends the drawer's strokes while they draw. */
    private var inkJob: Job? = null
    /** The user's guesses this turn (by drawing): how many the feed has answered, the last one sent, the word once guessed. */
    private var guessTurn = 0
    private var answered = 0
    private var lastGuess = ""
    private var guessedWord: String? = null
    private var feedback: DoodleGuessFeedbackUi? = null
    /** When the whole drawing was last asked for (it's asked again at most every few seconds). */
    private var lastSyncMillis = 0L
    private var jobs: List<Job> = emptyList()

    override suspend fun handleIntent(intent: DoodleKingMatchIntent) {
        val id = matchId
        when (intent) {
            is DoodleKingMatchIntent.Load -> load(intent.matchId)
            is DoodleKingMatchIntent.ScreenVisible -> {
                foreground = intent.visible
                syncMic()
                render()
            }
            is DoodleKingMatchIntent.ToggleMic -> when {
                // Their turn to draw: the mic stays closed (the table says why).
                match?.iAmDrawing == true -> Unit
                micSwitch -> setMicOn(false)
                intent.permissionGranted -> setMicOn(true)
                else -> emitEffect(DoodleKingMatchEffect.RequestMicPermission)
            }
            is DoodleKingMatchIntent.MicPermissionResult -> if (intent.granted) setMicOn(true) else showModal(DoodleModal.MicPermissionNeeded)
            DoodleKingMatchIntent.OpenAppSettings -> {
                showModal(null)
                emitEffect(DoodleKingMatchEffect.OpenAppSettings)
            }
            DoodleKingMatchIntent.ToggleSpeaker -> voice.setSpeakerOn(!speakerOn)
            is DoodleKingMatchIntent.ToggleHear -> match?.player(intent.seat)?.let { voice.setMuted(it.userId, it.userId !in mutedForMe) }
            is DoodleKingMatchIntent.ToggleHearSpectator -> voice.setMuted(intent.userId, intent.userId !in mutedForMe)
            is DoodleKingMatchIntent.PickWord -> if (id != null) sendCommand(id, DoodleCommand.PickWord(intent.index))
            is DoodleKingMatchIntent.SendGuess -> {
                val text = intent.text.trim().take(DoodleKingConstants.MAX_GUESS_LENGTH)
                if (id != null && text.isNotEmpty() && match?.me?.guessed == false) {
                    lastGuess = text
                    sendCommand(id, DoodleCommand.Guess(text))
                }
            }
            DoodleKingMatchIntent.Pass -> if (id != null && match?.iAmDrawing == true) sendCommand(id, DoodleCommand.Pass)
            is DoodleKingMatchIntent.SelectInk -> {
                board.colour = intent.colour
                board.eraser = false
                render()
            }
            DoodleKingMatchIntent.SelectBrush -> {
                // Already the brush: the next size (thin, medium, thick, round again).
                if (!board.eraser) board.size = (board.size + 1) % SIZES
                board.eraser = false
                render()
            }
            DoodleKingMatchIntent.SelectEraser -> {
                board.eraser = true
                render()
            }
            DoodleKingMatchIntent.Undo -> board.undo()
            DoodleKingMatchIntent.Redo -> board.redo()
            DoodleKingMatchIntent.Clear -> board.clear()
            DoodleKingMatchIntent.OpenSettings -> showSettings()
            DoodleKingMatchIntent.ToggleEffects -> {
                audio.setEffectsOn(!audio.settings.value.effectsOn)
                showSettings()
            }
            DoodleKingMatchIntent.ToggleMusic -> {
                audio.setMusicOn(!audio.settings.value.musicOn)
                showSettings()
            }
            DoodleKingMatchIntent.ExitClicked -> if (leaderboard != null) {
                emitEffect(DoodleKingMatchEffect.Close)
            } else {
                leave.open(clock.now())
                render()
            }
            DoodleKingMatchIntent.ConfirmLeave -> {
                // Only a player has a seat to hand on (to the picked spectator, or a CPU).
                val giveTo = leave.giveTo(match?.spectators?.map { it.userId }.orEmpty())
                leave.close()
                showModal(null)
                if (id != null) sendCommand(id, DoodleCommand.Leave(giveTo))
                emitEffect(DoodleKingMatchEffect.Close)
            }
            is DoodleKingMatchIntent.PickSeatTaker -> {
                leave.pick(intent.index)
                render()
            }
            DoodleKingMatchIntent.DismissModal -> {
                leave.close()
                showModal(null)
            }
            is DoodleKingMatchIntent.Seat -> seat(intent.action)
            // A seat the bot holds: the leader may offer it to someone watching.
            is DoodleKingMatchIntent.PlayerLongPressed -> match?.player(intent.seat)?.takeIf { it.isCpu || it.botHeld }?.let { seat(SeatHandoverAction.OpenBotSeatOffer(it.seat)) }
            // Asks first, as at every table; the vote starts on End game.
            DoodleKingMatchIntent.ProposeEnd -> showModal(DoodleModal.ConfirmEnd)
            DoodleKingMatchIntent.ConfirmEnd -> {
                showModal(null)
                if (id != null) sendCommand(id, DoodleCommand.ProposeEnd)
            }
            is DoodleKingMatchIntent.VoteEnd -> if (id != null) sendCommand(id, DoodleCommand.VoteEnd(intent.agree))
            is DoodleKingMatchIntent.VoteKeepPlaying -> if (id != null) sendCommand(id, DoodleCommand.VoteKeepPlaying(intent.keepPlaying))
            DoodleKingMatchIntent.Done -> emitEffect(DoodleKingMatchEffect.Close)
        }
    }

    private fun load(id: String) {
        if (matchId == id) return
        matchId = id
        jobs.forEach { it.cancel() }
        jobs = listOf(
            scope.launch {
                try {
                    // A failing update never stops the table: the latest match still shows and play goes on.
                    observeMatch(id).collect { update ->
                        playTableUpdate(update, "DoodleKingMatch.update", ::onUpdate) {
                            match = it.match
                            render()
                        }
                    }
                } catch (e: MatchGoneException) {
                    emitEffect(DoodleKingMatchEffect.Close)
                }
            },
            // The drawer's strokes on everyone else's board (the server never sends them back to the drawer),
            // and the whole drawing when asked for (the drawer's too, after a restart).
            scope.launch { observeInk().collect(board::apply) },
            scope.launch { observeMatchCoins().collect { seatResults = it; leaderboard = leaderboard?.let { b -> LeaderboardResult.withResult(b, it) }; render() } },
            scope.launch { observeSpeaking(id).collect { speaking = it; render() } },
            scope.launch { voice.observeMutedTalkers(id).collect { mutedTalkers = it; render() } },
            scope.launch { observeMySpeaking().collect { mySpeech = it; render() } },
            scope.launch { observeMicOn().collect { micSwitch = it; syncMic(); render() } },
            scope.launch { voice.observeMuted().collect { mutedForMe = it; render() } },
            scope.launch { voice.observeSpeakerOn().collect { speakerOn = it; render() } },
            // The phase clock: a coarse tick, once a second.
            scope.launch {
                while (isActive) {
                    delay(TICK_MS)
                    if (match?.phase != DoodlePhase.FINISHED) render()
                }
            },
        )
    }

    private fun onUpdate(update: DoodleUpdate) {
        val m = update.match
        match = m
        if (board.drawingId != m.drawingId) board.reset(m.drawingId)
        answerGuesses(m)
        askForDrawingIfMissing(m)
        manageInkSender(m)
        syncMic()
        if (m.phase == DoodlePhase.FINISHED && leaderboard == null) {
            leaderboard = DoodleTableMapper.leaderboard(m, DoodleTableMapper.elapsed(clock.now() - m.startedAt)).let { LeaderboardResult.withResult(it, seatResults) }
        }
        render()
    }

    /**
     * The table's answer to the user's latest guess, from the turn's feed: right (their word goes
     * green under their card), very close, or wrong. A new turn starts clean.
     */
    private fun answerGuesses(m: DoodleMatch) {
        if (m.drawingId != guessTurn) {
            guessTurn = m.drawingId
            answered = 0
            guessedWord = null
            feedback = null
        }
        val mine = m.feed.filter { it.seat == m.me?.seat && it.kind in GUESS_ANSWERS }
        if (mine.size <= answered) return
        answered = mine.size
        val last = mine.last()
        val kind = when (last.kind) {
            DoodleFeedKind.CORRECT -> DoodleGuessResult.RIGHT
            DoodleFeedKind.CLOSE -> DoodleGuessResult.CLOSE
            else -> DoodleGuessResult.WRONG
        }
        if (kind == DoodleGuessResult.RIGHT) guessedWord = lastGuess
        val message = when (kind) {
            DoodleGuessResult.RIGHT -> DoodleStrings.dkYouGotIt(last.points)
            DoodleGuessResult.CLOSE -> DoodleStrings.dkVeryClose(last.text.orEmpty())
            DoodleGuessResult.WRONG -> DoodleStrings.dkWrongGuess
        }
        feedback = DoodleGuessFeedbackUi((feedback?.id ?: 0) + 1, kind, message)
    }

    /** Joined mid-turn, or strokes went missing: ask for the whole drawing (at most every few seconds). */
    private fun askForDrawingIfMissing(m: DoodleMatch) {
        val id = matchId ?: return
        val drawing = m.phase == DoodlePhase.DRAW || m.phase == DoodlePhase.REVEAL
        val now = clock.now().toEpochMilliseconds()
        if (!drawing || board.strokeCount >= m.strokeCount || now - lastSyncMillis < SYNC_GAP_MS) return
        lastSyncMillis = now
        scope.launch { sendInk.requestDrawing(id) }
    }

    /** While the user draws: their new strokes go out every 60 ms, in order. */
    private fun manageInkSender(m: DoodleMatch) {
        val drawing = m.iAmDrawing && m.phase == DoodlePhase.DRAW
        if (!drawing) {
            inkJob?.cancel()
            inkJob = null
            return
        }
        if (inkJob?.isActive == true) return
        val id = matchId ?: return
        inkJob = scope.launch {
            while (isActive) {
                delay(DoodleKingConstants.STROKE_SEND_MS)
                board.takeOutgoing().forEach { sendInk(id, it) }
            }
        }
    }

    /** Tells the table the user's mic: their switch while the app is in front, never on their own turn to draw. */
    private fun syncMic() {
        val m = match ?: return
        val id = matchId ?: return
        val current = (m.me?.micOn ?: m.spectators.firstOrNull { it.userId == m.viewerUserId }?.micOn) ?: return
        val want = micSwitch && foreground && !m.iAmDrawing
        if (current != want) scope.launch { sendCommand(id, DoodleCommand.SetMic(want)) }
    }

    private fun showSettings() {
        val m = match
        val me = m?.me
        // Anyone still playing may ask to end early, but not during a vote.
        val canProposeEnd = m != null && me != null && !me.removed && !me.isCpu &&
            m.phase != DoodlePhase.FINISHED && m.phase != DoodlePhase.LIMIT && m.endVote == null
        showModal(audio.settings.value.let { DoodleModal.Settings(it.effectsOn, it.musicOn, canProposeEnd) })
    }

    /** A seat dialog's answer (or a long press on a bot's seat), handled the same in every game. */
    private suspend fun seat(action: SeatHandoverAction) {
        val m = match ?: return
        val id = matchId ?: return
        if (seats.handle(id, DoodleTableMapper.seatTable(m), action)) render()
    }

    private fun showModal(next: DoodleModal?) {
        modal = next
        render()
    }

    private fun render() {
        val m = match ?: return
        val now = clock.now()
        val me = m.me
        val locked = m.iAmDrawing
        val myMic = (me?.micOn ?: m.spectators.firstOrNull { it.userId == m.viewerUserId }?.micOn ?: false) && !locked
        micGate.update(micOn = myMic, inFront = foreground)
        voiceNeed.update(
            micGate.open,
            speakerOn,
            m.players.filter { !it.isMe && !it.isCpu && !it.removed }.associate { it.userId to it.micOn } +
                m.spectators.filter { it.userId != m.viewerUserId }.associate { it.userId to it.micOn },
        )
        val inputs = DoodleVoiceInputs(speaking + listOfNotNull(m.viewerUserId.takeIf { mySpeech == MySpeech.SPEAKING }), mutedForMe, mutedTalkers + listOfNotNull(m.viewerUserId.takeIf { mySpeech == MySpeech.SPEAKING_MUTED }))
        val cards = DoodleTableMapper.cards(m, inputs)
        val seconds = DoodleTableMapper.secondsLeft(m.phaseEndsAt, now)
        val barMode = when {
            m.iAmDrawing && m.phase == DoodlePhase.DRAW -> DoodleBarMode.DRAW
            me == null || m.phase != DoodlePhase.DRAW || m.iAmDrawing -> DoodleBarMode.WAIT
            me.guessed -> DoodleBarMode.GUESSED
            else -> DoodleBarMode.GUESS
        }
        setState {
            copy(
                loaded = true,
                title = DoodleStrings.dkTitle,
                subtitle = DoodleStrings.dkSubtitle(m.title, m.round, m.rounds, m.level),
                audience = DoodleTableMapper.audience(m, inputs),
                showAudience = m.roomId != null,
                spectatorCount = m.spectators.size.takeIf { it > 0 }?.toString().orEmpty(),
                elapsed = DoodleTableMapper.elapsed(now - m.startedAt),
                status = DoodleTableMapper.status(m),
                // Under the user's card: their word while they draw, the word once they guessed it, else the hint.
                myWord = when {
                    m.iAmDrawing -> m.word.orEmpty()
                    me?.guessed == true && m.phase == DoodlePhase.DRAW -> guessedWord?.takeIf { it.isNotEmpty() } ?: m.hint
                    m.phase == DoodlePhase.REVEAL -> m.word.orEmpty()
                    else -> m.hint
                }.uppercase(),
                myWordGuessed = me?.guessed == true && m.phase == DoodlePhase.DRAW,
                feedback = feedback.takeIf { m.phase == DoodlePhase.DRAW },
                timeLeft = if (m.phase == DoodlePhase.FINISHED) "" else DoodleTableMapper.clock(seconds),
                timeUrgent = m.phase == DoodlePhase.DRAW && seconds <= URGENT_SECONDS,
                board = this@DoodleKingMatchViewModel.board,
                barMode = barMode,
                inkColour = board.colour,
                inkSize = board.size,
                eraser = board.eraser,
                hint = m.hint,
                leftCards = cards.filter { !it.isMe },
                myCard = cards.firstOrNull { it.isMe },
                micOn = myMic,
                micLocked = locked,
                speakerOn = speakerOn,
                spectating = m.spectating,
                feed = DoodleTableMapper.feed(m),
                ranks = DoodleTableMapper.ranks(m),
                pick = DoodleTableMapper.pick(m, now),
                reveal = DoodleTableMapper.reveal(m),
                // Leaving first, then the table's votes and seat offers, then the user's own dialogs.
                modal = leave.ui(now, m.spectators.map { it.name }, me != null, roomMatch = m.roomId != null, outOfGame = me?.removed == true)?.let { DoodleModal.Leave(it) }
                    ?: DoodleTableMapper.endVote(m, now)?.let { DoodleModal.EndVote(it) }
                    ?: DoodleTableMapper.keepPlaying(m, now)?.let { DoodleModal.KeepPlaying(it) }
                    ?: seats.modal(DoodleTableMapper.seatTable(m), now)?.let { DoodleModal.Seat(it) }
                    ?: this@DoodleKingMatchViewModel.modal,
                leaderboard = this@DoodleKingMatchViewModel.leaderboard,
            )
        }
    }

    override fun onCleared() {
        micGate.clear()
        voiceNeed.clear()
    }

    private companion object {
        const val TICK_MS = 1_000L
        const val SYNC_GAP_MS = 3_000L
        const val URGENT_SECONDS = 10L
        const val SIZES = 3
        val GUESS_ANSWERS = setOf(DoodleFeedKind.GUESS, DoodleFeedKind.CLOSE, DoodleFeedKind.CORRECT)
    }
}
