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
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoCommand
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoEvent
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoMatch
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoPhase
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoPlayer
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoUpdate
import com.kglabs28.yapnplay.appengine.games.mono.domain.isPlayableOn
import com.kglabs28.yapnplay.appengine.games.mono.domain.usecase.ObserveMonoMatchUseCase
import com.kglabs28.yapnplay.appengine.games.mono.domain.usecase.ObserveMonoSpeakingUseCase
import com.kglabs28.yapnplay.appengine.games.mono.domain.usecase.SendMonoCommandUseCase
import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants
import com.kglabs28.yapnplay.appengine.games.mono.ui.components.look
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardUi
import kotlinx.coroutines.Job
import kotlinx.datetime.Instant
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.time.Duration.Companion.seconds

/**
 * Standard MONO table. Server updates render straight to state; their
 * events become sounds and flying cards (MonoTableDirector); the user's
 * taps become commands. The turn timer and the callout windows run on
 * the clock, so a slow tick re-renders them until they shut — exactly as
 * the server's own deadlines arrive.
 */
class MatchMonoViewModel(
    private val observeMatch: ObserveMonoMatchUseCase,
    private val observeSpeaking: ObserveMonoSpeakingUseCase,
    private val sendCommand: SendMonoCommandUseCase,
    private val clock: AppClock,
    audio: GameAudio,
    gamesConfig: GamesAppManagerHelper,
    private val observeMatchCoins: ObserveMatchCoinsUseCase,
    moderation: ModerationUseCases,
    dispatchers: DispatcherProvider,
) : MviViewModel<MatchMonoState, MatchMonoIntent, MatchMonoEffect>(
    MatchMonoState(), dispatchers,
) {

    private var matchId: String = ""
    private var match: MonoMatch? = null
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
    private val director = MonoTableDirector(audio, gamesConfig, chaos = false)

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
                director.pressed(GameSound.MONO_CALL)
                send(MonoCommand.CallMono)
                render()
            }
            is MatchMonoIntent.VoteContinue -> send(MonoCommand.VoteContinue(intent.keepPlaying))
            MatchMonoIntent.Caught -> {
                director.pressed(GameSound.CAUGHT)
                send(MonoCommand.Caught)
                setState { copy(canCatch = false) }
            }
            MatchMonoIntent.AcceptStack -> {
                send(MonoCommand.AcceptStack)
                setState { copy(activeStackPenalty = 0) }
            }
            is MatchMonoIntent.SetAutoplay -> send(MonoCommand.SetAutoplay(intent.enabled))
            is MatchMonoIntent.SetMic -> send(MonoCommand.SetMic(intent.on))
            MatchMonoIntent.OpenSettings -> sheets.openSettings()
            MatchMonoIntent.ToggleEffects -> sheets.toggleEffects()
            MatchMonoIntent.ToggleMusic -> sheets.toggleMusic()
            MatchMonoIntent.OpenReport -> match?.let { sheets.openReport(reportable(it), voters(it)) }
            is MatchMonoIntent.SelectReportPerson -> match?.let { sheets.selectReportPerson(intent.userId, voters(it)) }
            MatchMonoIntent.SubmitReport -> match?.let { sheets.submitReport(it.matchId, voters(it)) }
            is MatchMonoIntent.Vote -> sheets.vote(intent.agree)
            MatchMonoIntent.CloseSheet -> sheets.close()
            // Leaving hands the seat to a bot (null), then closes the table.
            // Exit asks first; leaving hands the seat to a bot (null), then closes the table.
            MatchMonoIntent.ExitClicked -> {
                leave.open(clock.now())
                render()
            }
            MatchMonoIntent.CancelExit -> {
                leave.close()
                setState { copy(leaveDialog = null) }
            }
            is MatchMonoIntent.PickSeatTaker -> {
                leave.pick(intent.index)
                render()
            }
            MatchMonoIntent.ConfirmExit -> {
                // Only a player has a seat to hand on (to the picked spectator, or a CPU).
                val giveTo = if (match?.me != null) leave.giveTo(match?.spectators?.map { it.userId }.orEmpty()) else null
                leave.close()
                setState { copy(leaveDialog = null) }
                send(MonoCommand.Leave(giveTo))
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
            // The turn timer and the callout window run on the clock.
            scope.launch {
                while (true) {
                    render()
                    delay(TICK_MS)
                }
            },
        )
    }

    /** A server update: its events become sounds and flying cards, then the table shows the new state. */
    private suspend fun onUpdate(update: MonoUpdate) {
        val previous = match
        match = update.match
        update.events.filterIsInstance<MonoEvent.SeatFinished>().lastOrNull()?.let { finished ->
            val place = MonoUiMapper.placeLabel(finished.place)
            val who = update.match.player(finished.seat.value)
            val text = if (who?.isMe == true) MonoStrings.monoYouFinishBanner(place) else MonoStrings.monoFinishBanner(who?.name.orEmpty(), place)
            banner = text to clock.now()
        }
        val flights = director.direct(cues(update.events, previous), update.match.me?.seat)
        if (flights.isNotEmpty()) emitEffect(MatchMonoEffect.Fly(flights))
        render()
    }

    /** The server's events in the director's words; each play covers the card it lands on. */
    private fun cues(events: List<MonoEvent>, previous: MonoMatch?): List<MonoTableDirector.Cue> {
        var top = previous?.topCard?.look()
        return events.mapNotNull { event ->
            when (event) {
                is MonoEvent.CardPlayed -> {
                    val look = event.card.look()
                    MonoTableDirector.Cue.Played(event.seat.value, look, top).also { top = look }
                }
                is MonoEvent.CardDrawn -> MonoTableDirector.Cue.Drew(event.seat.value, 1, listOf(event.card.look()))
                is MonoEvent.CardsDrawn -> MonoTableDirector.Cue.Drew(event.seat.value, event.count)
                is MonoEvent.DeckReshuffled -> MonoTableDirector.Cue.Shuffled
                is MonoEvent.MonoCalled -> MonoTableDirector.Cue.Called
                is MonoEvent.MonoCaught, is MonoEvent.FalseCatch, is MonoEvent.MonoMissed -> MonoTableDirector.Cue.Caught
                is MonoEvent.MatchEnded -> MonoTableDirector.Cue.Won
                else -> null
            }
        }
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

    /** Background: the table goes quiet and the server takes the user's turn while they are away. */
    private suspend fun onScreenVisible(visible: Boolean) {
        director.setForeground(visible)
        send(MonoCommand.SetAway(!visible))
        if (visible) send(MonoCommand.StillHere)
    }

    override fun onCleared() {
        director.stop()
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
                playableIndexes = if (waitingOnMe) playable(m, me, stacking) else emptySet(),
                awaitingColorChoice = waitingOnMe && m.phase == MonoPhase.AWAITING_COLOR_CHOICE.name,
                awaitingSwapChoice = waitingOnMe && m.phase == MonoPhase.AWAITING_SWAP_CHOICE.name,
                activeStackPenalty = m.activeStackPenalty,
                canStack = stacking,
                canCallMono = me != null && me.autoplay != true && canCall(m, me, waitingOnMe, now),
                canCatch = me != null && canCatch(m, me),
                canPass = waitingOnMe && m.drawnCardIndex != null,
                places = MonoUiMapper.places(m),
                winnerSeat = MonoUiMapper.winner(m),
                finished = m.phase == MonoPhase.FINISHED.name,
                // Only the active seat's outline runs down.
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
                talking = talking,
                vote = MonoUiMapper.vote(m.continueVote, me?.seat, now),
                // The finisher's banner shows first; the board follows once it has had its moment.
                finishBanner = banner?.takeIf { now - it.second < BANNER_TIME }?.first,
                // The winner's banner first; then the board, with the coins each seat won.
                leaderboard = (finalBoard ?: board(m, now)?.takeIf { banner?.let { now - it.second >= BANNER_TIME } ?: true }?.also { finalBoard = it })
                    ?.let { LeaderboardResult.withResult(it, seatResults) },
                // A selection the table has moved past is no longer offered.
                selectedCardIndex = selectedCardIndex?.takeIf { it < (me?.hand?.size ?: 0) },
            )
        }
        autoDraw(m)
    }

    /**
     * Only legal cards are lit, so a tap is never wasted. After a draw only
     * the drawn card is in play (with No Power Finish, the whole hand is).
     * Under a stack only a draw card worth at least the last one (Stack
     * Upward: +2 on +2, +4 on either; never +2 on +4). With No Power
     * Finish the last card can't be a power card.
     */
    private fun playable(m: MonoMatch, me: MonoPlayer, stacking: Boolean): Set<Int> {
        val drawnOnly = m.drawnCardIndex.takeUnless { m.noPowerFinish }
        if (drawnOnly != null) return setOf(drawnOnly)
        return me.hand.mapIndexedNotNull { index, card ->
            val matches = card.isPlayableOn(m.topCard, m.topCardColor)
            val stacks = !stacking || (card.penaltyValue() > 0 && card.penaltyValue() >= m.stackTopValue)
            val powerFinish = m.noPowerFinish && me.hand.size == 1 && card.isPower()
            index.takeIf { matches && stacks && !powerFinish }
        }.toSet()
    }

    /**
     * MONO can be called on the user's own turn while they hold two cards
     * (before the second-last goes down). Without Call Before You Play it
     * may also be called just after, while they are on one card and their
     * catch window is still open.
     */
    private fun canCall(m: MonoMatch, me: MonoPlayer, waitingOnMe: Boolean, now: Instant): Boolean {
        if (m.monoCalledSeat == me.seat) return false
        val beforePlay = waitingOnMe && me.hand.size == 2
        val afterPlay = !m.callBeforePlay && me.hand.size == 1 && (m.catchSeat == me.seat || m.monoCallOpen(now))
        return beforePlay || afterPlay
    }

    /** CAUGHT is open on someone else, and the user hasn't already paid a wrong catch in this window. */
    private fun canCatch(m: MonoMatch, me: MonoPlayer): Boolean {
        val target = m.catchSeat ?: return false
        return target != me.seat && me.seat !in m.catchClosedFor && me.seat !in m.finishedSeats
    }

    /** The final board once the match is over (built once, so its clock stops). */
    private fun board(m: MonoMatch, now: Instant): LeaderboardUi? {
        if (m.phase != MonoPhase.FINISHED.name) return null
        val standings = m.players.map {
            MonoUiMapper.Standing(it.seat, it.name, it.avatarId, it.micOn, it.isMe, it.cardCount, finished = it.seat in m.finishedSeats)
        }
        return MonoUiMapper.leaderboard(MonoStrings.monoTitle, m.ranking.map { it.seat }, standings, monoElapsed(m.startedAt, now))
    }

    /**
     * No card the user can play: the table draws for them (or, under a
     * stack, takes it), once per turn and after a short beat so they see
     * why. A drawn card that can be played is then theirs to play or pass.
     */
    private fun autoDraw(m: MonoMatch) {
        val shown = state.value
        val playing = m.phase == MonoPhase.AWAITING_PLAY.name || shown.canStack
        if (!shown.myTurn || !playing || shown.canPass || shown.playableIndexes.isNotEmpty()) return
        val turn = m.currentSeat to m.turnEndsAt
        if (autoDrewFor == turn) return
        autoDrewFor = turn
        scope.launch {
            delay(AUTO_DRAW_DELAY)
            val now = state.value
            if (now.myTurn && !now.canPass && now.playableIndexes.isEmpty() && match?.let { it.currentSeat to it.turnEndsAt } == turn) {
                send(if (now.canStack) MonoCommand.AcceptStack else MonoCommand.DrawCard)
            }
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
        /** How long a finisher's banner stays up (and the final board waits). */
        val BANNER_TIME = 3.seconds
        const val MILLIS = 1_000L
    }

    /** Who the user may report: the other players (not CPUs) and the spectators. */
    private fun reportable(m: MonoMatch): List<ReportPersonUi> =
        m.players.filter { !it.isMe && !it.removed && it.userId != null }.map { ReportPersonUi(it.userId.orEmpty(), it.name, LudoStrings.ludoRolePlayer) } +
            m.spectators.filter { it.userId != m.viewerUserId }.map { ReportPersonUi(it.userId, it.name, LudoStrings.ludoRoleSpectator) }

    /** Who would vote on a report: everyone at the table but the reporter and the reported. */
    private fun voters(m: MonoMatch): Int =
        (m.players.count { !it.removed && it.userId != null } + m.spectators.size - REPORT_INVOLVED).coerceAtLeast(0)
}

private const val REPORT_INVOLVED = 2
/** The beat before the table draws for a user with nothing to play. */
private val AUTO_DRAW_DELAY = 700.milliseconds
