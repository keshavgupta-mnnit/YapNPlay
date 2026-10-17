package com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoCommand
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoEvent
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoMatch
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoPhase
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoUpdate
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.usecase.ObserveConnectionLatencyUseCase
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.usecase.ObserveLudoMatchUseCase
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.usecase.ObserveMatchSpeakingUseCase
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.usecase.SendLudoCommandUseCase
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.mapper.LudoMatchUiMapper
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.LudoBoardGeometry
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.PROGRESS_IN_BASE
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.ROYAL_LUDO_TURN_SECONDS
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Royal Ludo table. Server updates are queued and played one event at a
 * time — the dice tumbles, a piece hops square by square, a captured
 * piece glides home — and only then does the new turn show. A ticker
 * keeps the turn timer moving. Mic changes ask for permission first.
 */
class RoyalLudoMatchViewModel(
    private val observeMatch: ObserveLudoMatchUseCase,
    private val observeSpeaking: ObserveMatchSpeakingUseCase,
    private val sendCommand: SendLudoCommandUseCase,
    private val observeLatency: ObserveConnectionLatencyUseCase,
    private val clock: AppClock,
    dispatchers: DispatcherProvider,
) : MviViewModel<RoyalLudoMatchState, RoyalLudoMatchIntent, RoyalLudoMatchEffect>(RoyalLudoMatchState(), dispatchers) {

    private var matchId: String? = null
    private var match: LudoMatch? = null
    private var speaking: Set<String> = emptySet()
    private var latencyMs: Int? = null
    /** Progress shown per seat (lags the server while events animate). */
    private val shown = mutableMapOf<Int, MutableList<Int>>()
    private val motions = mutableMapOf<String, PieceMotion>()
    private var playing = false
    private var rollId = 0
    private var diceValue = 1
    private var userModal: LudoMatchModal? = null
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
            RoyalLudoMatchIntent.ToggleAutoplay -> send(LudoCommand.SetAutoplay(!state.value.autoplay))
            is RoyalLudoMatchIntent.ToggleMic -> when {
                state.value.micOn -> send(LudoCommand.SetMic(false))
                intent.permissionGranted -> send(LudoCommand.SetMic(true))
                else -> emitEffect(RoyalLudoMatchEffect.RequestMicPermission)
            }
            is RoyalLudoMatchIntent.MicPermissionResult ->
                if (intent.granted) send(LudoCommand.SetMic(true)) else showModal(LudoMatchModal.MicPermissionNeeded)
            RoyalLudoMatchIntent.OpenAppSettings -> {
                showModal(null)
                emitEffect(RoyalLudoMatchEffect.OpenAppSettings)
            }
            RoyalLudoMatchIntent.ExitClicked -> showModal(LudoMatchModal.LeaveConfirm)
            RoyalLudoMatchIntent.ConfirmLeave -> {
                showModal(null)
                send(LudoCommand.Leave)
                emitEffect(RoyalLudoMatchEffect.Close)
            }
            RoyalLudoMatchIntent.DismissModal -> showModal(null)
            // Match settings (sound, board style) come with the settings phase.
            RoyalLudoMatchIntent.OpenSettings -> Unit
            RoyalLudoMatchIntent.Done -> emitEffect(RoyalLudoMatchEffect.Close)
        }
    }

    private fun load(id: String) {
        if (matchId == id) return
        matchId = id
        jobs.forEach { it.cancel() }
        jobs = listOf(
            scope.launch { observeMatch(id).collect { queue.send(it) } },
            scope.launch { for (update in queue) play(update) },
            scope.launch {
                observeLatency().collect {
                    latencyMs = it
                    render()
                }
            },
            scope.launch {
                observeSpeaking(id).collect {
                    speaking = it
                    render()
                }
            },
            scope.launch {
                while (true) {
                    render()
                    delay(AppConstants.LUDO_TIMER_TICK_MS)
                }
            },
        )
    }

    /** Animates [update]'s events in order, then shows the new turn. */
    private suspend fun play(update: LudoUpdate) {
        if (match == null) {
            reset(update.match)
            return
        }
        playing = true
        for (event in update.events) {
            when (event) {
                is LudoEvent.Rolled -> {
                    diceValue = event.value
                    rollId++
                    render()
                    delay(AppConstants.DICE_ROLL_MS.toLong())
                }
                is LudoEvent.Moved -> {
                    val steps = if (event.from == PROGRESS_IN_BASE) listOf(0) else (event.from + 1)..event.to
                    for (progress in steps) {
                        place(event.seat, event.piece, progress, PieceMotion.HOP)
                        delay(AppConstants.LUDO_HOP_MS.toLong())
                    }
                }
                is LudoEvent.Captured -> {
                    place(event.victimSeat, event.victimPiece, PROGRESS_IN_BASE, PieceMotion.SLIDE)
                    delay(AppConstants.LUDO_SLIDE_MS.toLong())
                }
                is LudoEvent.TurnPassed -> Unit
            }
        }
        playing = false
        reset(update.match)
    }

    /** Shows the server's pieces as they are (after any animation). */
    private fun reset(newMatch: LudoMatch) {
        match = newMatch
        shown.clear()
        newMatch.pieces.forEach { (seat, list) -> shown[seat] = list.toMutableList() }
        if (newMatch.phase == LudoPhase.FINISHED && userModal == null) {
            userModal = LudoMatchModal.Result(LudoMatchUiMapper.resultRows(newMatch))
        }
        render()
    }

    private fun place(seat: Int, piece: Int, progress: Int, motion: PieceMotion) {
        shown[seat]?.set(piece, progress)
        motions[LudoMatchUiMapper.pieceId(seat, piece)] = motion
        render()
    }

    private fun showModal(modal: LudoMatchModal?) {
        userModal = modal
        render()
    }

    private fun render() {
        val m = match ?: return
        val me = m.me
        val rotation = me?.let { LudoBoardGeometry.rotationFor(it.colour.ordinal) } ?: 0
        val myTurn = me != null && !playing && m.currentSeat == me.seat && !me.autoplay
        val remainingMs = m.turnEndsAt?.let { (it - clock.now()).inWholeMilliseconds.coerceAtLeast(0) } ?: 0L
        setState {
            copy(
                loaded = true,
                title = Strings.ludoTitle,
                subtitle = m.title,
                rotation = rotation,
                pieces = LudoMatchUiMapper.pieces(m, shown, motions, selectable = if (myTurn && m.phase == LudoPhase.MOVE) m.legalPieces else emptyList()),
                cards = LudoMatchUiMapper.cards(m, rotation, speaking, if (playing) null else remainingMs, ROYAL_LUDO_TURN_SECONDS),
                audience = LudoMatchUiMapper.audience(m, speaking),
                spectatorCount = m.spectatorCount.toString(),
                spectatorLabel = Strings.ludoSpectators,
                pingLabel = latencyMs?.let { Strings.ludoPing(it) }.orEmpty(),
                pingQuality = LudoMatchUiMapper.pingQuality(latencyMs),
                dice = LudoDiceUi(diceValue, rollId),
                canRoll = myTurn && m.phase == LudoPhase.ROLL,
                autoplay = me?.autoplay == true,
                micOn = me?.micOn == true,
                modal = userModal,
            )
        }
    }

    private suspend fun send(command: LudoCommand) {
        val id = matchId ?: return
        sendCommand(id, command)
    }
}
