package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.screens.match

import com.kglabs28.yapnplay.appengine.core.audio.GameAudio
import com.kglabs28.yapnplay.appengine.core.audio.GameSound
import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.core.mvi.playTableUpdate
import com.kglabs28.yapnplay.appengine.core.network.MatchGoneException
import com.kglabs28.yapnplay.appengine.core.ui.CyberBlastStrings
import com.kglabs28.yapnplay.appengine.features.matchresult.domain.SeatResult
import com.kglabs28.yapnplay.appengine.features.matchresult.usecase.ObserveMatchCoinsUseCase
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.LeaveDialogState
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatHandoverAction
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatHandoverController
import com.kglabs28.yapnplay.appengine.features.voice.domain.MicGate
import com.kglabs28.yapnplay.appengine.features.voice.domain.MySpeech
import com.kglabs28.yapnplay.appengine.features.voice.domain.VoiceNeed
import com.kglabs28.yapnplay.appengine.features.voice.usecase.ObserveMicOnUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.ObserveMySpeakingUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.SetMicOnUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.SetMicOpenUseCase
import com.kglabs28.yapnplay.appengine.features.voice.usecase.TableVoiceUseCases
import com.kglabs28.yapnplay.appengine.games.cyberblast.BlastDir
import com.kglabs28.yapnplay.appengine.games.cyberblast.BlastPhase
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberCommand
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberFrameData
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberMatch
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.usecase.ObserveCyberFramesUseCase
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.usecase.ObserveCyberMatchUseCase
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.usecase.ObserveCyberSpeakingUseCase
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.usecase.SendCyberCommandUseCase
import com.kglabs28.yapnplay.appengine.games.cyberblast.ui.mapper.CyberTableMapper
import com.kglabs28.yapnplay.appengine.games.cyberblast.ui.mapper.CyberVoiceInputs
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardResult
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Cyber Blast table. The server runs the board; frames go straight into
 * [feed] (drawn by the canvas, no screen-state change), and the cards
 * re-render only when someone's hearts or points change. The user's pad
 * and bomb button go out in order, as soon as they change.
 */
class CyberBlastMatchViewModel(
    private val observeMatch: ObserveCyberMatchUseCase,
    private val observeFrames: ObserveCyberFramesUseCase,
    private val observeSpeaking: ObserveCyberSpeakingUseCase,
    private val sendCommand: SendCyberCommandUseCase,
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
) : MviViewModel<CyberBlastMatchState, CyberBlastMatchIntent, CyberBlastMatchEffect>(CyberBlastMatchState(), dispatchers) {

    private var matchId: String? = null
    private var match: CyberMatch? = null
    private val feed = CyberBoardFeed()
    private val voiceNeed = VoiceNeed { wanted, talkers -> matchId?.let { voice.setVoiceWanted.match(it, wanted, talkers) } }
    private val micGate = MicGate { open, listenMuted -> matchId?.let { setMicOpen.match(it, open, listenMuted) } }
    private var speaking: Set<String> = emptySet()
    private var mutedForMe: Set<String> = emptySet()
    private var mutedTalkers: Set<String> = emptySet()
    private var mySpeech = MySpeech.SILENT
    private var micSwitch = false
    private var speakerOn = true
    private var foreground = true
    private var modal: CyberModal? = null
    private val leave = LeaveDialogState()
    private var leaderboard: LeaderboardUi? = null
    private var seatResults: List<SeatResult> = emptyList()
    /** The cards' hearts and points as last shown: a frame re-renders only when they change. */
    private var shownCards: List<Long> = emptyList()
    private var shownPhase: BlastPhase? = null
    /** "GO!" shows until then ([CyberBoardFeed.nowMs]'s clock). */
    private var goUntil = 0L
    /** The direction the user is pushing, as last sent. */
    private var held: BlastDir? = null
    /** Controls go out one at a time, in the order pressed. */
    private val inputs = Channel<CyberCommand.Input>(Channel.UNLIMITED)
    private var jobs: List<Job> = emptyList()

    override suspend fun handleIntent(intent: CyberBlastMatchIntent) {
        val id = matchId
        when (intent) {
            is CyberBlastMatchIntent.Load -> load(intent.matchId)
            is CyberBlastMatchIntent.ScreenVisible -> {
                foreground = intent.visible
                // Away from the screen: let go of the pad.
                if (!intent.visible) steer(null)
                syncMic()
                render()
            }
            is CyberBlastMatchIntent.Steer -> steer(intent.dir)
            CyberBlastMatchIntent.DropBomb -> if (canPlay()) inputs.trySend(CyberCommand.Input(held, bomb = true))
            is CyberBlastMatchIntent.ToggleMic -> when {
                micSwitch -> setMicOn(false)
                intent.permissionGranted -> setMicOn(true)
                else -> emitEffect(CyberBlastMatchEffect.RequestMicPermission)
            }
            is CyberBlastMatchIntent.MicPermissionResult -> if (intent.granted) setMicOn(true) else showModal(CyberModal.MicPermissionNeeded)
            CyberBlastMatchIntent.OpenAppSettings -> {
                showModal(null)
                emitEffect(CyberBlastMatchEffect.OpenAppSettings)
            }
            CyberBlastMatchIntent.ToggleSpeaker -> voice.setSpeakerOn(!speakerOn)
            is CyberBlastMatchIntent.ToggleHear -> match?.seat(intent.seat)?.let { voice.setMuted(it.userId, it.userId !in mutedForMe) }
            is CyberBlastMatchIntent.ToggleHearSpectator -> voice.setMuted(intent.userId, intent.userId !in mutedForMe)
            CyberBlastMatchIntent.OpenSettings -> showSettings()
            CyberBlastMatchIntent.ToggleEffects -> {
                audio.setEffectsOn(!audio.settings.value.effectsOn)
                showSettings()
            }
            CyberBlastMatchIntent.ToggleMusic -> {
                audio.setMusicOn(!audio.settings.value.musicOn)
                showSettings()
            }
            CyberBlastMatchIntent.ExitClicked -> if (leaderboard != null) {
                emitEffect(CyberBlastMatchEffect.Close)
            } else {
                leave.open(clock.now())
                render()
            }
            CyberBlastMatchIntent.ConfirmLeave -> {
                val giveTo = leave.giveTo(match?.spectators?.map { it.userId }.orEmpty())
                leave.close()
                showModal(null)
                if (id != null) sendCommand(id, CyberCommand.Leave(giveTo))
                emitEffect(CyberBlastMatchEffect.Close)
            }
            is CyberBlastMatchIntent.PickSeatTaker -> {
                leave.pick(intent.index)
                render()
            }
            CyberBlastMatchIntent.DismissModal -> {
                leave.close()
                showModal(null)
            }
            is CyberBlastMatchIntent.Seat -> seat(intent.action)
            is CyberBlastMatchIntent.PlayerLongPressed -> match?.seat(intent.seat)?.takeIf { it.isCpu || it.botHeld }?.let { seat(SeatHandoverAction.OpenBotSeatOffer(it.seat)) }
            CyberBlastMatchIntent.ProposeEnd -> showModal(CyberModal.ConfirmEnd)
            CyberBlastMatchIntent.ConfirmEnd -> {
                showModal(null)
                if (id != null) sendCommand(id, CyberCommand.ProposeEnd)
            }
            is CyberBlastMatchIntent.VoteEnd -> if (id != null) sendCommand(id, CyberCommand.VoteEnd(intent.agree))
            CyberBlastMatchIntent.Done -> emitEffect(CyberBlastMatchEffect.Close)
        }
    }

    private fun load(id: String) {
        if (matchId == id) return
        matchId = id
        jobs.forEach { it.cancel() }
        jobs = listOf(
            scope.launch {
                try {
                    observeMatch(id).collect { update ->
                        playTableUpdate(update, "CyberBlastMatch.update", { u ->
                            match = u.match
                            feed.setUp(u.match.cols, u.match.rows, u.match.frameMs)
                            feed.apply(u.frame, whole = feed.current == null || u.frame.number >= (feed.current?.number ?: 0))
                            onFrame(u.frame)
                            syncMic()
                            render()
                        }) {
                            match = it.match
                            render()
                        }
                    }
                } catch (e: MatchGoneException) {
                    emitEffect(CyberBlastMatchEffect.Close)
                }
            },
            scope.launch {
                observeFrames().collect { frame ->
                    if (match == null) return@collect
                    feed.apply(frame)
                    onFrame(frame)
                }
            },
            // The user's controls, in order.
            scope.launch { for (input in inputs) sendCommand(id, input) },
            scope.launch { observeMatchCoins().collect { seatResults = it; leaderboard = leaderboard?.let { b -> LeaderboardResult.withResult(b, it) }; render() } },
            scope.launch { observeSpeaking(id).collect { speaking = it; render() } },
            scope.launch { voice.observeMutedTalkers(id).collect { mutedTalkers = it; render() } },
            scope.launch { observeMySpeaking().collect { mySpeech = it; render() } },
            scope.launch { observeMicOn().collect { micSwitch = it; syncMic(); render() } },
            scope.launch { voice.observeMuted().collect { mutedForMe = it; render() } },
            scope.launch { voice.observeSpeakerOn().collect { speakerOn = it; render() } },
            // The running clock and the countdown: a coarse tick.
            scope.launch {
                while (isActive) {
                    delay(TICK_MS)
                    if (leaderboard == null) render()
                }
            },
        )
    }

    /** A frame: sounds for hits, the end board, and a re-render only when the cards change. */
    private fun onFrame(frame: CyberFrameData) {
        val m = match ?: return
        // One blast sound a frame, however many bombs went off together (a chain sounds like one big boom).
        if (frame.booms.isNotEmpty()) audio.play(GameSound.BOMB_BLAST)
        if (frame.hits.isNotEmpty()) audio.play(GameSound.CAPTURE)
        if (shownPhase == BlastPhase.COUNTDOWN && frame.phase == BlastPhase.PLAYING) goUntil = feed.nowMs() + GO_MS
        val phaseChanged = shownPhase != frame.phase
        shownPhase = frame.phase
        if (frame.phase == BlastPhase.FINISHED) {
            steer(null)
            if (leaderboard == null) {
                audio.play(GameSound.WIN)
                leaderboard = CyberTableMapper.leaderboard(m, frame, CyberTableMapper.elapsed(clock.now() - m.startedAt)).let { LeaderboardResult.withResult(it, seatResults) }
            }
        }
        val key = CyberTableMapper.cardKey(frame)
        if (key != shownCards || phaseChanged) {
            shownCards = key
            render()
        }
    }

    private fun canPlay(): Boolean {
        val m = match ?: return false
        val me = m.me ?: return false
        val body = feed.current?.body(me.seat) ?: return false
        return !me.removed && !body.out && feed.current?.phase != BlastPhase.FINISHED
    }

    private fun steer(dir: BlastDir?) {
        val next = dir.takeIf { canPlay() }
        if (next == held) return
        held = next
        inputs.trySend(CyberCommand.Input(next, bomb = false))
    }

    private fun syncMic() {
        val m = match ?: return
        val id = matchId ?: return
        val current = (m.me?.micOn ?: m.spectators.firstOrNull { it.userId == m.viewerUserId }?.micOn) ?: return
        val want = micSwitch && foreground
        if (current != want) scope.launch { sendCommand(id, CyberCommand.SetMic(want)) }
    }

    private fun showSettings() {
        val m = match
        val me = m?.me
        val canProposeEnd = m != null && me != null && !me.removed && !me.isCpu && leaderboard == null && m.endVote == null
        showModal(audio.settings.value.let { CyberModal.Settings(it.effectsOn, it.musicOn, canProposeEnd) })
    }

    private suspend fun seat(action: SeatHandoverAction) {
        val m = match ?: return
        val id = matchId ?: return
        if (seats.handle(id, CyberTableMapper.seatTable(m), action)) render()
    }

    private fun showModal(next: CyberModal?) {
        modal = next
        render()
    }

    private fun render() {
        val m = match ?: return
        val now = clock.now()
        val me = m.me
        val myMic = me?.micOn ?: m.spectators.firstOrNull { it.userId == m.viewerUserId }?.micOn ?: false
        micGate.update(micOn = myMic, inFront = foreground)
        voiceNeed.update(
            micGate.open,
            speakerOn,
            m.seats.filter { !it.isMe && !it.isCpu && !it.removed }.associate { it.userId to it.micOn } +
                m.spectators.filter { it.userId != m.viewerUserId }.associate { it.userId to it.micOn },
        )
        val inputs = CyberVoiceInputs(speaking + listOfNotNull(m.viewerUserId.takeIf { mySpeech == MySpeech.SPEAKING }), mutedForMe, mutedTalkers + listOfNotNull(m.viewerUserId.takeIf { mySpeech == MySpeech.SPEAKING_MUTED }))
        val cards = CyberTableMapper.cards(m, feed.current, inputs)
        val half = (cards.size + 1) / 2
        setState {
            copy(
                loaded = true,
                title = CyberBlastStrings.blastTitle,
                subtitle = CyberBlastStrings.blastSubtitle(m.title),
                audience = CyberTableMapper.audience(m, inputs),
                showAudience = m.roomId != null,
                spectatorCount = m.spectators.size.takeIf { it > 0 }?.toString().orEmpty(),
                elapsed = CyberTableMapper.elapsed(now - m.startedAt),
                feed = this@CyberBlastMatchViewModel.feed,
                leftCards = cards.take(half),
                rightCards = cards.drop(half),
                banner = CyberTableMapper.banner(m, feed.current, goUntil, feed.nowMs()),
                canPlay = canPlay(),
                micOn = myMic,
                speakerOn = speakerOn,
                spectating = m.spectating,
                modal = leave.ui(now, m.spectators.map { it.name }, me != null, roomMatch = m.roomId != null, outOfGame = me?.removed == true)?.let { CyberModal.Leave(it) }
                    ?: CyberTableMapper.endVote(m, now)?.let { CyberModal.EndVote(it) }
                    ?: seats.modal(CyberTableMapper.seatTable(m), now)?.let { CyberModal.Seat(it) }
                    ?: this@CyberBlastMatchViewModel.modal,
                leaderboard = this@CyberBlastMatchViewModel.leaderboard,
            )
        }
    }

    override fun onCleared() {
        inputs.close()
        micGate.clear()
        voiceNeed.clear()
    }

    private companion object {
        /** The clock and countdown refresh this often (ms). */
        const val TICK_MS = 250L
        /** "GO!" stays up this long after the countdown (ms). */
        const val GO_MS = 900L
    }
}
