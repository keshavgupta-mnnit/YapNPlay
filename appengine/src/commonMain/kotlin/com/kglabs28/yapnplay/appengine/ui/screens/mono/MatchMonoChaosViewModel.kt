package com.kglabs28.yapnplay.appengine.ui.screens.mono

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.core.ui.MonoStrings
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosCommand
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosMatch
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosPhase
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.canStackOnto
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.isPlayableOn
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.usecase.ObserveMonoChaosMatchUseCase
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.usecase.SendMonoChaosCommandUseCase
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil

/**
 * MONO: Complete Chaos table. Same table as Standard MONO; the turn
 * timer, the callout window and the away handover all behave the same.
 * Stacking and the 25-card knockout come from the server's card counts,
 * so the screen never guesses what is legal.
 */
class MatchMonoChaosViewModel(
    private val observeMatch: ObserveMonoChaosMatchUseCase,
    private val sendCommand: SendMonoChaosCommandUseCase,
    private val clock: AppClock,
    dispatchers: DispatcherProvider,
) : MviViewModel<MatchMonoChaosState, MatchMonoChaosIntent, MatchMonoChaosEffect>(
    MatchMonoChaosState(), dispatchers,
) {

    private var matchId: String = ""
    private var match: MonoChaosMatch? = null
    private var jobs: List<Job> = emptyList()

    override suspend fun handleIntent(intent: MatchMonoChaosIntent) {
        when (intent) {
            is MatchMonoChaosIntent.Load -> load(intent.matchId)
            is MatchMonoChaosIntent.TapCard -> tapCard(intent.index)
            MatchMonoChaosIntent.DrawUntilPlayable -> send(MonoChaosCommand.DrawUntilPlayable)
            MatchMonoChaosIntent.Pass -> send(MonoChaosCommand.Pass)
            is MatchMonoChaosIntent.ChooseSwapTarget -> {
                send(MonoChaosCommand.ChooseSwapTarget(intent.seat))
                setState { copy(awaitingSwapChoice = false) }
            }
            is MatchMonoChaosIntent.ChooseColor -> {
                send(MonoChaosCommand.ChooseColor(intent.color.name))
                setState { copy(awaitingColorChoice = false, selectedCardIndex = null) }
            }
            is MatchMonoChaosIntent.ChooseRouletteColor -> {
                send(MonoChaosCommand.ChooseRouletteColor(intent.color.name))
                setState { copy(awaitingRouletteChoice = false) }
            }
            MatchMonoChaosIntent.CallMono -> {
                send(MonoChaosCommand.CallMono)
                render()
            }
            is MatchMonoChaosIntent.StackDraw -> send(MonoChaosCommand.StackDraw(intent.index))
            MatchMonoChaosIntent.AcceptStack -> {
                send(MonoChaosCommand.AcceptStack)
                setState { copy(activeStackPenalty = 0) }
            }
            is MatchMonoChaosIntent.SetAutoplay -> send(MonoChaosCommand.SetAutoplay(intent.enabled))
            is MatchMonoChaosIntent.SetMic -> send(MonoChaosCommand.SetMic(intent.on))
            MatchMonoChaosIntent.OpenSettings -> emitEffect(MatchMonoChaosEffect.OpenSettingsDialog)
            // Leaving hands the seat to a bot (null), then closes the table.
            MatchMonoChaosIntent.ExitClicked -> {
                send(MonoChaosCommand.Leave(null))
                emitEffect(MatchMonoChaosEffect.LeaveMatch)
            }
            is MatchMonoChaosIntent.Leave -> {
                send(MonoChaosCommand.Leave(intent.giveToUserId))
                emitEffect(MatchMonoChaosEffect.LeaveMatch)
            }
            MatchMonoChaosIntent.StillHere -> send(MonoChaosCommand.StillHere)
            is MatchMonoChaosIntent.ScreenVisible -> onScreenVisible(intent.visible)
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
            scope.launch {
                while (true) {
                    render()
                    delay(TICK_MS)
                }
            },
        )
    }

    /**
     * One tap plays the card — or stacks it when a penalty is on the
     * table. Cards the table will not accept are drawn pushed back and do
     * nothing, so there is no hidden confirm step.
     */
    private suspend fun tapCard(index: Int) {
        if (!state.value.myTurn || index !in state.value.playableIndexes) return
        val command = if (state.value.canStack) {
            MonoChaosCommand.StackDraw(index)
        } else {
            MonoChaosCommand.PlayCard(index)
        }
        send(command)
        setState { copy(selectedCardIndex = null) }
    }

    /** Background: the server takes the user's turn while they are away. */
    private suspend fun onScreenVisible(visible: Boolean) {
        send(MonoChaosCommand.SetAway(!visible))
        if (visible) send(MonoChaosCommand.StillHere)
    }

    private fun render() {
        val m = match ?: return
        val now = clock.now()
        val me = m.me
        val waitingOnMe = m.currentSeat == me?.seat
        val remaining = m.turnEndsAt
            ?.let { (it - now).inWholeMilliseconds.coerceAtLeast(0) }
            ?: Long.MAX_VALUE
        val turnMillis = MonoChaosConstants.TURN_SECONDS * MILLIS
        val eliminated = m.eliminatedSeats.toSet()
        val turnClock = MonoStrings.monoTurnClock(
            if (remaining == Long.MAX_VALUE) {
                MonoChaosConstants.TURN_SECONDS.toInt()
            } else {
                ceil(remaining / 1000.0).toInt().coerceAtLeast(0)
            },
        )
        val stacking = waitingOnMe && m.phase == MonoChaosPhase.AWAITING_STACK_CHOICE.name
        setState {
            copy(
                loaded = true,
                error = null,
                match = m,
                myTurn = waitingOnMe && me?.autoplay != true,
                // With a penalty on the table, stacking is the only legal
                // move; otherwise it is the usual match rules. After a
                // draw, only the drawn card is in play.
                playableIndexes = if (waitingOnMe) {
                    m.drawnCardIndex?.let { setOf(it) }
                        ?: me.hand.mapIndexedNotNull { index, card ->
                            val legal = if (stacking) {
                                card.canStackOnto(m.stackTopValue)
                            } else {
                                card.isPlayableOn(m.topCard, m.topCardColor)
                            }
                            index.takeIf { legal }
                        }.toSet()
                } else {
                    emptySet()
                },
                stackableIndexes = if (waitingOnMe && stacking) {
                    me.hand.mapIndexedNotNull { index, card ->
                        index.takeIf { card.canStackOnto(m.stackTopValue) }
                    }.toSet()
                } else {
                    emptySet()
                },
                canStack = stacking,
                awaitingColorChoice = waitingOnMe && m.phase == MonoChaosPhase.AWAITING_COLOR_CHOICE.name,
                awaitingSwapChoice = waitingOnMe && m.phase == MonoChaosPhase.AWAITING_SWAP_CHOICE.name,
                awaitingRouletteChoice = waitingOnMe && m.phase == MonoChaosPhase.AWAITING_ROULETTE_COLOR.name,
                activeStackPenalty = m.activeStackPenalty,
                canCallMono = m.monoCallOpen(now),
                canPass = waitingOnMe && m.drawnCardIndex != null,
                places = MonoUiMapper.chaosPlaces(m),
                eliminated = eliminated,
                winnerSeat = MonoUiMapper.chaosWinner(m),
                finished = m.phase == MonoChaosPhase.FINISHED.name,
                timerFraction = if (remaining == Long.MAX_VALUE) {
                    1f
                } else {
                    (remaining.toFloat() / turnMillis).coerceIn(0f, 1f)
                },
                turnClock = turnClock,
                topColour = m.topCardColor,
                reversed = m.reversed,
                elapsed = monoElapsed(m.startedAt, now),
                title = m.title,
                spectatorCount = m.spectatorCount,
                audience = MonoUiMapper.chaosAudience(m),
                micOn = me?.micOn == true,
                autoplay = me?.autoplay == true,
                selectedCardIndex = selectedCardIndex?.takeIf { it < (me?.hand?.size ?: 0) },
            )
        }
    }

    private suspend fun send(command: MonoChaosCommand) {
        if (matchId.isEmpty()) return
        sendCommand(matchId, command)
    }

    /** The colour choices offered; Prism cards always allow all four. */
    fun colourChoices(): List<MonoChaosConstants.Color> = MonoChaosConstants.Color.entries

    private companion object {
        const val TICK_MS = 250L
        const val MILLIS = 1_000L
    }
}