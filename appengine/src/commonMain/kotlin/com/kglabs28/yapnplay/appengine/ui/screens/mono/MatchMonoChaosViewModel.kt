package com.kglabs28.yapnplay.appengine.ui.screens.mono

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
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
            is MatchMonoChaosIntent.ChooseColor -> {
                send(MonoChaosCommand.ChooseColor(intent.color.name))
                setState { copy(awaitingColorChoice = false, selectedCardIndex = null) }
            }
            is MatchMonoChaosIntent.ChooseRouletteColor -> {
                send(MonoChaosCommand.ChooseRouletteColor(intent.color.name))
                setState { copy(awaitingColorChoice = false, selectedCardIndex = null) }
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
            MatchMonoChaosIntent.ExitClicked -> send(MonoChaosCommand.Leave(null))
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
                    setState { copy(loaded = true, error = e.message ?: DESCRIBE_FAILED) }
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
        val command = if (state.value.activeStackPenalty > 0) {
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
        setState {
            copy(
                loaded = true,
                error = null,
                match = m,
                myTurn = waitingOnMe && me != null && !me.autoplay,
                // With a penalty on the table, stacking is the only legal
                // move; otherwise it is the usual match rules.
                playableIndexes = if (waitingOnMe) {
                    me.hand.mapIndexedNotNull { index, card ->
                        val legal = if (m.activeStackPenalty > 0) {
                            card.canStackOnto(m.activeStackPenalty)
                        } else {
                            card.isPlayableOn(m.topCard, m.topCardColor)
                        }
                        index.takeIf { legal }
                    }.toSet()
                } else {
                    emptySet()
                },
                awaitingColorChoice = waitingOnMe && m.phase == MonoChaosPhase.AWAITING_COLOR_CHOICE.name,
                activeStackPenalty = m.activeStackPenalty,
                canCallMono = waitingOnMe && m.monoCallOpen(now),
                places = MonoUiMapper.chaosPlaces(m),
                eliminated = eliminated,
                winnerSeat = MonoUiMapper.chaosWinner(m),
                finished = m.phase == MonoChaosPhase.FINISHED.name,
                timerFraction = if (remaining == Long.MAX_VALUE) {
                    1f
                } else {
                    (remaining.toFloat() / turnMillis).coerceIn(0f, 1f)
                },
                elapsed = monoElapsed(m.startedAt, now),
                title = m.title,
                spectatorCount = m.spectatorCount,
                audience = MonoUiMapper.chaosAudience(m),
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
        const val DESCRIBE_FAILED = "Could not open this match"
    }
}