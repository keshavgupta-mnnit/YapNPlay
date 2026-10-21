package com.kglabs28.yapnplay.appengine.ui.screens.mono

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.core.ui.MonoStrings
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoCommand
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoMatch
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoPhase
import com.kglabs28.yapnplay.appengine.games.mono.domain.isPlayableOn
import com.kglabs28.yapnplay.appengine.games.mono.domain.usecase.ObserveMonoMatchUseCase
import com.kglabs28.yapnplay.appengine.games.mono.domain.usecase.SendMonoCommandUseCase
import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil

/**
 * Standard MONO table. Server updates render straight to state; the
 * user's taps become commands. The turn timer and the MONO! callout
 * window both run on the clock, so a slow tick re-renders them until
 * they shut — exactly as the server's own deadlines arrive.
 */
class MatchMonoViewModel(
    private val observeMatch: ObserveMonoMatchUseCase,
    private val sendCommand: SendMonoCommandUseCase,
    private val clock: AppClock,
    dispatchers: DispatcherProvider,
) : MviViewModel<MatchMonoState, MatchMonoIntent, MatchMonoEffect>(
    MatchMonoState(), dispatchers,
) {

    private var matchId: String = ""
    private var match: MonoMatch? = null
    private var jobs: List<Job> = emptyList()

    override suspend fun handleIntent(intent: MatchMonoIntent) {
        when (intent) {
            is MatchMonoIntent.Load -> load(intent.matchId)
            is MatchMonoIntent.TapCard -> tapCard(intent.index)
            MatchMonoIntent.DrawCard -> send(MonoCommand.DrawCard)
            MatchMonoIntent.Pass -> send(MonoCommand.Pass)
            is MatchMonoIntent.ChooseSwapTarget -> {
                send(MonoCommand.ChooseSwapTarget(intent.seat))
                setState { copy(awaitingSwapChoice = false) }
            }
            is MatchMonoIntent.ChooseColor -> {
                send(MonoCommand.ChooseColor(intent.color.name))
                setState { copy(awaitingColorChoice = false, selectedCardIndex = null) }
            }
            MatchMonoIntent.CallMono -> {
                send(MonoCommand.CallMono)
                render()
            }
            MatchMonoIntent.AcceptStack -> {
                send(MonoCommand.AcceptStack)
                setState { copy(activeStackPenalty = 0) }
            }
            is MatchMonoIntent.SetAutoplay -> send(MonoCommand.SetAutoplay(intent.enabled))
            is MatchMonoIntent.SetMic -> send(MonoCommand.SetMic(intent.on))
            MatchMonoIntent.OpenSettings -> emitEffect(MatchMonoEffect.OpenSettingsDialog)
            // Leaving hands the seat to a bot (null), then closes the table.
            MatchMonoIntent.ExitClicked -> {
                send(MonoCommand.Leave(null))
                emitEffect(MatchMonoEffect.LeaveMatch)
            }
            is MatchMonoIntent.Leave -> {
                send(MonoCommand.Leave(intent.giveToUserId))
                emitEffect(MatchMonoEffect.LeaveMatch)
            }
            MatchMonoIntent.StillHere -> send(MonoCommand.StillHere)
            is MatchMonoIntent.ScreenVisible -> onScreenVisible(intent.visible)
        }
    }

    private fun load(id: String) {
        if (matchId == id) return
        matchId = id
        jobs.forEach { it.cancel() }
        jobs = listOf(
            scope.launch {
                try {
                    observeMatch(id).collect { match = it.match; render() }
                } catch (e: Exception) {
                    setState { copy(loaded = true, error = e.message ?: MonoStrings.monoOpenFailed) }
                }
            },
            // The turn timer and the callout window run on the clock.
            scope.launch {
                while (true) {
                    render()
                    delay(TICK_MS)
                }
            },
        )
    }

    /**
     * One tap plays the card. Cards the table will not accept are drawn
     * pushed back and do nothing, so a tap is never wasted and there is
     * no hidden confirm step.
     */
    private suspend fun tapCard(index: Int) {
        if (!state.value.myTurn || index !in state.value.playableIndexes) return
        send(MonoCommand.PlayCard(index))
        setState { copy(selectedCardIndex = null) }
    }

    /** Background: the server takes the user's turn while they are away. */
    private suspend fun onScreenVisible(visible: Boolean) {
        send(MonoCommand.SetAway(!visible))
        if (visible) send(MonoCommand.StillHere)
    }

    private fun render() {
        val m = match ?: return
        val now = clock.now()
        val me = m.me
        val waitingOnMe = m.currentSeat == me?.seat
        val stacking = waitingOnMe && m.phase == MonoPhase.AWAITING_STACK_CHOICE.name
        val remaining = m.turnEndsAt
            ?.let { (it - now).inWholeMilliseconds.coerceAtLeast(0) }
            ?: Long.MAX_VALUE
        val turnSeconds = MonoConstants.TURN_SECONDS * MILLIS
        val turnClock = MonoStrings.monoTurnClock(
            if (remaining == Long.MAX_VALUE) {
                MonoConstants.TURN_SECONDS.toInt()
            } else {
                ceil(remaining / 1000.0).toInt().coerceAtLeast(0)
            },
        )
        setState {
            copy(
                loaded = true,
                error = null,
                match = m,
                myTurn = waitingOnMe && me?.autoplay != true,
                // Only legal cards are lit, so a tap is never wasted. After
                // a draw, only the drawn card is in play; under a stack, only
                // a matching draw card worth at least the last one.
                playableIndexes = if (waitingOnMe) {
                    m.drawnCardIndex?.let { setOf(it) }
                        ?: me.hand.mapIndexedNotNull { index, card ->
                            val legal = card.isPlayableOn(m.topCard, m.topCardColor) &&
                                (!stacking || card.penaltyValue() >= m.stackTopValue && card.penaltyValue() > 0)
                            index.takeIf { legal }
                        }.toSet()
                } else {
                    emptySet()
                },
                awaitingColorChoice = waitingOnMe && m.phase == MonoPhase.AWAITING_COLOR_CHOICE.name,
                awaitingSwapChoice = waitingOnMe && m.phase == MonoPhase.AWAITING_SWAP_CHOICE.name,
                activeStackPenalty = m.activeStackPenalty,
                canStack = stacking,
                canCallMono = m.monoCallOpen(now),
                canPass = waitingOnMe && m.drawnCardIndex != null,
                places = MonoUiMapper.places(m),
                winnerSeat = MonoUiMapper.winner(m),
                finished = m.phase == MonoPhase.FINISHED.name,
                // Only the active seat's card shrinks.
                timerFraction = if (remaining == Long.MAX_VALUE) {
                    1f
                } else {
                    (remaining.toFloat() / turnSeconds).coerceIn(0f, 1f)
                },
                turnClock = turnClock,
                topColour = m.topCardColor,
                reversed = m.reversed,
                elapsed = monoElapsed(m.startedAt, now),
                title = m.title,
                spectatorCount = m.spectatorCount,
                audience = MonoUiMapper.audience(m),
                micOn = me?.micOn == true,
                autoplay = me?.autoplay == true,
                // A selection the table has moved past is no longer offered.
                selectedCardIndex = selectedCardIndex?.takeIf { it < (me?.hand?.size ?: 0) },
            )
        }
    }

    private suspend fun send(command: MonoCommand) {
        if (matchId.isEmpty()) return
        sendCommand(matchId, command)
    }

    /** The colour choices offered; Prism cards always allow all four. */
    fun colourChoices(): List<MonoConstants.Color> = MonoConstants.Color.entries

    private companion object {
        const val TICK_MS = 250L
        const val MILLIS = 1_000L
    }
}
