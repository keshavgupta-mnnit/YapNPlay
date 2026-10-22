package com.kglabs28.yapnplay.appengine.ui.screens.mono

import kotlin.time.Duration.Companion.milliseconds
import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import com.kglabs28.yapnplay.appengine.ui.components.moderation.ReportPersonUi
import com.kglabs28.yapnplay.appengine.ui.components.moderation.TableSheetController
import com.kglabs28.yapnplay.appengine.features.moderation.usecase.ModerationUseCases
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.LeaveDialogState
import com.kglabs28.yapnplay.appengine.appmanager.helpers.GamesAppManagerHelper
import com.kglabs28.yapnplay.appengine.core.audio.GameAudio
import com.kglabs28.yapnplay.appengine.features.matchresult.domain.SeatResult
import com.kglabs28.yapnplay.appengine.features.matchresult.usecase.ObserveMatchCoinsUseCase
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardResult
import com.kglabs28.yapnplay.appengine.core.audio.GameSound
import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.core.ui.MonoStrings
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosCommand
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosEvent
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosMatch
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosPlayer
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosUpdate
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosPhase
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.canStackOnto
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.isPlayableOn
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.usecase.ObserveMonoChaosMatchUseCase
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.usecase.ObserveMonoChaosSpeakingUseCase
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.usecase.SendMonoChaosCommandUseCase
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants
import com.kglabs28.yapnplay.appengine.games.monochaos.ui.components.look
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlin.math.ceil
import kotlin.time.Duration.Companion.seconds

/**
 * MONO: Complete Chaos table. Same table as Standard MONO; the turn
 * timer, the callout window and the away handover all behave the same.
 * Stacking and the 25-card knockout come from the server's card counts,
 * so the screen never guesses what is legal. Events become sounds and
 * flying cards through the same MonoTableDirector.
 */
class MatchMonoChaosViewModel(
    private val observeMatch: ObserveMonoChaosMatchUseCase,
    private val observeSpeaking: ObserveMonoChaosSpeakingUseCase,
    private val sendCommand: SendMonoChaosCommandUseCase,
    private val clock: AppClock,
    audio: GameAudio,
    gamesConfig: GamesAppManagerHelper,
    private val observeMatchCoins: ObserveMatchCoinsUseCase,
    moderation: ModerationUseCases,
    dispatchers: DispatcherProvider,
) : MviViewModel<MatchMonoChaosState, MatchMonoChaosIntent, MatchMonoChaosEffect>(
    MatchMonoChaosState(), dispatchers,
) {

    private var matchId: String = ""
    private var match: MonoChaosMatch? = null
    private var talking: Set<String> = emptySet()
    /** What MatchEnded paid each seat (CPUs included, display only); it arrives just after the last snapshot. */
    private var seatResults: List<SeatResult> = emptyList()
    /** The final board, built once (so its clock stops); the coins go on as they arrive. */
    private var finalBoard: LeaderboardUi? = null
    /** The latest finisher's banner and when it went up; the final board waits for it. */
    private var banner: Pair<String, Instant>? = null
    private var jobs: List<Job> = emptyList()
    /** Settings (sound, music), Report and report votes, as Royal Ludo has them. */
    private val sheets = TableSheetController(moderation, audio, scope) { render() }
    /** The turn (seat, deadline) the user was last drawn for automatically. */
    private var autoDrewFor: Pair<Int, Any?>? = null
    /** The shared exit dialog (who takes the seat, its countdown). */
    private val leave = LeaveDialogState()
    private val director = MonoTableDirector(audio, gamesConfig, chaos = true)

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
                director.pressed(GameSound.MONO_CALL)
                send(MonoChaosCommand.CallMono)
                render()
            }
            is MatchMonoChaosIntent.VoteContinue -> send(MonoChaosCommand.VoteContinue(intent.keepPlaying))
            MatchMonoChaosIntent.Caught -> {
                director.pressed(GameSound.CAUGHT)
                send(MonoChaosCommand.Caught)
                setState { copy(canCatch = false) }
            }
            is MatchMonoChaosIntent.StackDraw -> send(MonoChaosCommand.StackDraw(intent.index))
            MatchMonoChaosIntent.AcceptStack -> {
                send(MonoChaosCommand.AcceptStack)
                setState { copy(activeStackPenalty = 0) }
            }
            is MatchMonoChaosIntent.SetAutoplay -> send(MonoChaosCommand.SetAutoplay(intent.enabled))
            is MatchMonoChaosIntent.SetMic -> send(MonoChaosCommand.SetMic(intent.on))
            MatchMonoChaosIntent.OpenSettings -> sheets.openSettings()
            MatchMonoChaosIntent.ToggleEffects -> sheets.toggleEffects()
            MatchMonoChaosIntent.ToggleMusic -> sheets.toggleMusic()
            MatchMonoChaosIntent.OpenReport -> match?.let { sheets.openReport(reportable(it), voters(it)) }
            is MatchMonoChaosIntent.SelectReportPerson -> match?.let { sheets.selectReportPerson(intent.userId, voters(it)) }
            MatchMonoChaosIntent.SubmitReport -> match?.let { sheets.submitReport(it.matchId, voters(it)) }
            is MatchMonoChaosIntent.Vote -> sheets.vote(intent.agree)
            MatchMonoChaosIntent.CloseSheet -> sheets.close()
            // Leaving hands the seat to a bot (null), then closes the table.
            // Exit asks first; leaving hands the seat to a bot (null), then closes the table.
            MatchMonoChaosIntent.ExitClicked -> {
                leave.open(clock.now())
                render()
            }
            MatchMonoChaosIntent.CancelExit -> {
                leave.close()
                setState { copy(leaveDialog = null) }
            }
            is MatchMonoChaosIntent.PickSeatTaker -> {
                leave.pick(intent.index)
                render()
            }
            MatchMonoChaosIntent.ConfirmExit -> {
                // Only a player has a seat to hand on (to the picked spectator, or a CPU).
                val giveTo = if (match?.me != null) leave.giveTo(match?.spectators?.map { it.userId }.orEmpty()) else null
                leave.close()
                setState { copy(leaveDialog = null) }
                send(MonoChaosCommand.Leave(giveTo))
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
        jobs = sheets.start() + listOf(
            scope.launch {
                try {
                    observeMatch(id).collect { onUpdate(it) }
                } catch (e: Exception) {
                    setState { copy(loaded = true, error = e.message ?: MonoStrings.monoOpenFailed) }
                }
            },
            scope.launch { observeSpeaking(id).collect { talking = it; render() } },
            scope.launch { observeMatchCoins().collect { seatResults = it; render() } },
            scope.launch {
                while (true) {
                    render()
                    delay(TICK_MS)
                }
            },
        )
    }

    /** A server update: its events become sounds and flying cards, then the table shows the new state. */
    private suspend fun onUpdate(update: MonoChaosUpdate) {
        val previous = match
        match = update.match
        update.events.filterIsInstance<MonoChaosEvent.SeatFinished>().lastOrNull()?.let { finished ->
            val place = MonoUiMapper.placeLabel(finished.place)
            val who = update.match.player(finished.seat.value)
            val text = if (who?.isMe == true) MonoStrings.monoYouFinishBanner(place) else MonoStrings.monoFinishBanner(who?.name.orEmpty(), place)
            banner = text to clock.now()
        }
        val flights = director.direct(cues(update.events, previous), update.match.me?.seat)
        if (flights.isNotEmpty()) emitEffect(MatchMonoChaosEffect.Fly(flights))
        render()
    }

    /** The server's events in the director's words; each play covers the card it lands on. */
    private fun cues(events: List<MonoChaosEvent>, previous: MonoChaosMatch?): List<MonoTableDirector.Cue> {
        var top = previous?.topCard?.look()
        return events.mapNotNull { event ->
            when (event) {
                is MonoChaosEvent.CardPlayed -> {
                    val look = event.card.look()
                    MonoTableDirector.Cue.Played(event.seat.value, look, top).also { top = look }
                }
                is MonoChaosEvent.StackDraw -> {
                    val look = event.card.look()
                    MonoTableDirector.Cue.Played(event.seat.value, look, top).also { top = look }
                }
                is MonoChaosEvent.CardDrawn -> MonoTableDirector.Cue.Drew(event.seat.value, 1, listOf(event.card.look()))
                is MonoChaosEvent.CardsDrawn -> MonoTableDirector.Cue.Drew(event.seat.value, event.count)
                is MonoChaosEvent.DeckReshuffled -> MonoTableDirector.Cue.Shuffled
                is MonoChaosEvent.CardsDiscarded -> MonoTableDirector.Cue.Discarded(event.seat.value, event.count, top)
                is MonoChaosEvent.MonoCalled -> MonoTableDirector.Cue.Called
                is MonoChaosEvent.MonoCaught, is MonoChaosEvent.FalseCatch, is MonoChaosEvent.MonoMissed -> MonoTableDirector.Cue.Caught
                is MonoChaosEvent.MatchEnded -> MonoTableDirector.Cue.Won
                else -> null
            }
        }
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
        director.setForeground(visible)
        send(MonoChaosCommand.SetAway(!visible))
        if (visible) send(MonoChaosCommand.StillHere)
    }

    override fun onCleared() {
        director.stop()
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
        val myTurn = waitingOnMe && me?.autoplay != true
        director.turn(myTurn, if (remaining == Long.MAX_VALUE) 0L else remaining)
        setState {
            copy(
                leaveDialog = leave.ui(now, m.spectators.map { it.name }, m.me != null, roomMatch = m.roomId != null),
                sheet = sheets.current(now),
                loaded = true,
                error = null,
                match = m,
                myTurn = myTurn,
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
                canCallMono = me != null && me.autoplay != true && canCall(m, me, waitingOnMe, now),
                canCatch = me != null && canCatch(m, me),
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
                talking = talking,
                vote = MonoUiMapper.vote(m.continueVote, me?.seat, now),
                // The finisher's banner shows first; the board follows once it has had its moment.
                finishBanner = banner?.takeIf { now - it.second < BANNER_TIME }?.first,
                // The winner's banner first; then the board, with the coins each seat won.
                leaderboard = (finalBoard ?: board(m, now)?.takeIf { banner?.let { now - it.second >= BANNER_TIME } ?: true }?.also { finalBoard = it })
                    ?.let { LeaderboardResult.withResult(it, seatResults) },
                selectedCardIndex = selectedCardIndex?.takeIf { it < (me?.hand?.size ?: 0) },
            )
        }
        autoDraw(m)
    }

    /** The final board once the match is over (built once, so its clock stops). */
    private fun board(m: MonoChaosMatch, now: Instant): LeaderboardUi? {
        if (m.phase != MonoChaosPhase.FINISHED.name) return null
        val standings = m.players.map {
            MonoUiMapper.Standing(
                it.seat, it.name, it.avatarId, it.micOn, it.isMe, it.cardCount,
                finished = it.seat in m.finishedSeats,
                knockedOut = it.seat in m.eliminatedSeats,
            )
        }
        return MonoUiMapper.leaderboard(MonoStrings.monoChaosTitle, m.ranking.map { it.seat }, standings, monoElapsed(m.startedAt, now))
    }

    /** MONO on the user's own turn with two cards, or just after going down to one (while their catch window is open). */
    private fun canCall(m: MonoChaosMatch, me: MonoChaosPlayer, waitingOnMe: Boolean, now: Instant): Boolean {
        if (m.monoCalledSeat == me.seat) return false
        val beforePlay = waitingOnMe && me.hand.size == 2
        val afterPlay = me.hand.size == 1 && (m.catchSeat == me.seat || m.monoCallOpen(now))
        return beforePlay || afterPlay
    }

    /** CAUGHT is open on someone else, and the user hasn't already paid a wrong catch in this window. */
    private fun canCatch(m: MonoChaosMatch, me: MonoChaosPlayer): Boolean {
        val target = m.catchSeat ?: return false
        return target != me.seat && me.seat !in m.catchClosedFor && me.seat !in m.finishedSeats && me.seat !in m.eliminatedSeats
    }

    /**
     * No card the user can play: the table draws for them (or, under a
     * stack, takes it), once per turn and after a short beat so they see
     * why. A drawn card that can be played is then theirs to play or pass.
     */
    private fun autoDraw(m: MonoChaosMatch) {
        val shown = state.value
        val playing = m.phase == MonoChaosPhase.AWAITING_PLAY.name || shown.canStack
        if (!shown.myTurn || !playing || shown.canPass || shown.playableIndexes.isNotEmpty()) return
        val turn = m.currentSeat to m.turnEndsAt
        if (autoDrewFor == turn) return
        autoDrewFor = turn
        scope.launch {
            delay(AUTO_DRAW_DELAY)
            val now = state.value
            if (now.myTurn && !now.canPass && now.playableIndexes.isEmpty() && match?.let { it.currentSeat to it.turnEndsAt } == turn) {
                send(if (now.canStack) MonoChaosCommand.AcceptStack else MonoChaosCommand.DrawUntilPlayable)
            }
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
        /** How long a finisher's banner stays up (and the final board waits). */
        val BANNER_TIME = 3.seconds
        const val MILLIS = 1_000L
    }

    /** Who the user may report: the other players (not CPUs) and the spectators. */
    private fun reportable(m: MonoChaosMatch): List<ReportPersonUi> =
        m.players.filter { !it.isMe && !it.removed && it.userId != null }.map { ReportPersonUi(it.userId.orEmpty(), it.name, LudoStrings.ludoRolePlayer) } +
            m.spectators.filter { it.userId != m.viewerUserId }.map { ReportPersonUi(it.userId, it.name, LudoStrings.ludoRoleSpectator) }

    /** Who would vote on a report: everyone at the table but the reporter and the reported. */
    private fun voters(m: MonoChaosMatch): Int =
        (m.players.count { !it.removed && it.userId != null } + m.spectators.size - REPORT_INVOLVED).coerceAtLeast(0)
}

private const val REPORT_INVOLVED = 2
/** The beat before the table draws for a user with nothing to play. */
private val AUTO_DRAW_DELAY = 700.milliseconds
