package com.kglabs28.yapnplay.appengine.ui.screens.mono

import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.ui.components.moderation.TableSheetUi
import com.kglabs28.yapnplay.appengine.ui.components.moderation.EndVoteUiMapper
import com.kglabs28.yapnplay.appengine.ui.components.table.TableFeedLineUi
import com.kglabs28.yapnplay.appengine.features.voice.usecase.TableVoiceUseCases
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
    private val voice: TableVoiceUseCases,
    dispatchers: DispatcherProvider,
) : MviViewModel<MatchMonoChaosState, MatchMonoChaosIntent, MatchMonoChaosEffect>(
    MatchMonoChaosState(), dispatchers,
) {

    private var matchId: String = ""
    private var match: MonoChaosMatch? = null
    private var talking: Set<String> = emptySet()
    private var mutedForMe: Set<String> = emptySet()
    private var speakerOn = true
    /** What MatchEnded paid each seat (CPUs included, display only); it arrives just after the last snapshot. */
    private var seatResults: List<SeatResult> = emptyList()
    /** The final board, built once (so its clock stops); the coins go on as they arrive. */
    private var finalBoard: LeaderboardUi? = null
    /** The latest finisher's banner and when it went up; the final board waits for it. */
    private var banner: Pair<String, Instant>? = null
    /** The latest MONO! penalty snackbar for the user and when it went up. */
    private var snack: Pair<TableFeedLineUi, Instant>? = null
    private var snackId = 0L
    private var jobs: List<Job> = emptyList()
    /** Settings (sound, music), Report and report votes, as Royal Ludo has them. */
    private val sheets = TableSheetController(moderation, audio, scope) { render() }
    /** The turn (seat, deadline) the user was last drawn for automatically. */
    private var autoDrewFor: DrawStep? = null
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
            // A press with nothing to do says why on the table instead of doing nothing.
            MatchMonoChaosIntent.CallMono -> if (state.value.canCallMono) {
                director.pressed(GameSound.MONO_CALL)
                send(MonoChaosCommand.CallMono)
                render()
            } else {
                note(MonoStrings.monoCantCallYet)
            }
            is MatchMonoChaosIntent.VoteContinue -> send(MonoChaosCommand.VoteContinue(intent.keepPlaying))
            // Always goes to the table: with nobody to catch it's a wrong catch, and the presser pays.
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
            MatchMonoChaosIntent.ToggleSpeaker -> voice.setSpeakerOn(!speakerOn)
            is MatchMonoChaosIntent.ToggleHear -> match?.players?.firstOrNull { it.seat == intent.seat }?.userId?.let { voice.setMuted(it, it !in mutedForMe) }
            is MatchMonoChaosIntent.ToggleHearSpectator -> voice.setMuted(intent.userId, intent.userId !in mutedForMe)
            MatchMonoChaosIntent.OpenSettings -> sheets.openSettings(canEndGame())
            MatchMonoChaosIntent.ProposeEnd -> {
                sheets.close()
                send(MonoChaosCommand.ProposeEnd)
            }
            is MatchMonoChaosIntent.VoteEnd -> send(MonoChaosCommand.VoteEnd(intent.agree))
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
            scope.launch { voice.observeMuted().collect { mutedForMe = it; render() } },
            scope.launch { voice.observeSpeakerOn().collect { speakerOn = it; render() } },
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
        if (EndVoteUiMapper.rejected(previous?.endVote, update.match.endVote, update.match.phase == MonoChaosPhase.FINISHED.name)) banner = TableStrings.endVoteRejectedShort to clock.now()
        update.events.filterIsInstance<MonoChaosEvent.SeatFinished>().lastOrNull()?.let { finished ->
            val place = MonoUiMapper.placeLabel(finished.place)
            val who = update.match.player(finished.seat.value)
            val text = if (who?.isMe == true) MonoStrings.monoYouFinishBanner(place) else MonoStrings.monoFinishBanner(who?.name.orEmpty(), place)
            banner = text to clock.now()
        }
        if (update.events.none { it is MonoChaosEvent.SeatFinished }) MonoTableNotes.chaos(update.events, update.match)?.let { banner = it to clock.now() }
        MonoPenaltySnack.chaos(update.events, update.match, snackId + 1)?.let { snackId++; snack = it to clock.now() }
        val flights = director.direct(cues(update.events, previous, update.match), update.match.me?.seat)
        if (flights.isNotEmpty()) emitEffect(MatchMonoChaosEffect.Fly(flights))
        render()
    }

    /** The server's events in the director's words; each play covers the card it lands on. */
    private fun cues(events: List<MonoChaosEvent>, previous: MonoChaosMatch?, now: MonoChaosMatch): List<MonoTableDirector.Cue> {
        var top = previous?.topCard?.look()
        val me = now.me?.seat
        /** The faces of the cards [seat] now holds, when it is the viewer (theirs are the only faces they see). */
        fun facesFor(seat: Int) = if (seat == me) now.me?.hand.orEmpty().map { it.look() } else emptyList()
        val cues = events.flatMap { event ->
            when (event) {
                // A 7: two hands trade places.
                is MonoChaosEvent.HandSwapped -> {
                    val a = event.from.value
                    val b = event.to.value
                    listOf(
                        MonoTableDirector.Cue.Moved(a, b, previous?.cardCountForSeat(a) ?: 0, facesFor(b)),
                        MonoTableDirector.Cue.Moved(b, a, previous?.cardCountForSeat(b) ?: 0, facesFor(a)),
                    )
                }
                // A 0: every hand moves on one seat, the way play goes.
                is MonoChaosEvent.HandsPassed -> {
                    val seats = previous?.players.orEmpty().filter { !it.removed && it.cardCount > 0 }.map { it.seat }.sorted()
                    val forward = event.direction == CLOCKWISE
                    seats.mapIndexed { i, seat ->
                        val next = seats[(i + if (forward) 1 else seats.size - 1) % seats.size]
                        MonoTableDirector.Cue.Moved(seat, next, previous?.cardCountForSeat(seat) ?: 0, facesFor(next))
                    }.filter { it.fromSeat != it.toSeat }
                }
                else -> listOfNotNull(
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
                // The viewer's own new cards fly face up (the event only counts them).
                is MonoChaosEvent.CardsDrawn -> MonoTableDirector.Cue.Drew(
                    event.seat.value,
                    event.count,
                    if (event.seat.value == me) MonoHandFaces.added(previous?.me?.hand.orEmpty().map { it.look() }, now.me?.hand.orEmpty().map { it.look() }) else emptyList(),
                )
                is MonoChaosEvent.DeckReshuffled -> MonoTableDirector.Cue.Shuffled
                // The cards really dumped (the viewer's own faces; others' fly face down).
                is MonoChaosEvent.CardsDiscarded -> MonoTableDirector.Cue.Discarded(
                    event.seat.value,
                    event.count,
                    if (event.seat.value == me) MonoHandFaces.removed(previous?.me?.hand.orEmpty().map { it.look() }, now.me?.hand.orEmpty().map { it.look() }, top) else emptyList(),
                )
                is MonoChaosEvent.MonoCalled -> MonoTableDirector.Cue.Called
                is MonoChaosEvent.MonoCaught, is MonoChaosEvent.FalseCatch, is MonoChaosEvent.MonoMissed -> MonoTableDirector.Cue.Caught
                is MonoChaosEvent.MatchEnded -> MonoTableDirector.Cue.Won
                        else -> null
                    },
                )
            }
        }
        return cues + unexplainedDraws(cues, previous, now)
    }

    /**
     * Cards that appeared in a hand with no event to show them coming (a
     * penalty the server sent only as a count, a missed update): they fly
     * in from the deck too, so a hand never grows without a card arriving.
     */
    private fun unexplainedDraws(cues: List<MonoTableDirector.Cue>, previous: MonoChaosMatch?, now: MonoChaosMatch): List<MonoTableDirector.Cue> {
        if (previous == null || cues.any { it is MonoTableDirector.Cue.Moved }) return emptyList()
        val me = now.me?.seat
        return now.players.mapNotNull { player ->
            val seat = player.seat
            val drawn = cues.sumOf { if (it is MonoTableDirector.Cue.Drew && it.seat == seat) it.count else 0 }
            val gone = cues.sumOf {
                when (it) {
                    is MonoTableDirector.Cue.Played -> if (it.seat == seat) 1 else 0
                    is MonoTableDirector.Cue.Discarded -> if (it.seat == seat) it.count else 0
                    else -> 0
                }
            }
            val extra = player.cardCount - previous.cardCountForSeat(seat) - drawn + gone
            if (extra <= 0) return@mapNotNull null
            // The viewer's new faces, found by comparing the hands (the server may sort them).
            val faces = if (seat == me) MonoHandFaces.added(previous.me?.hand.orEmpty().map { it.look() }, player.hand.map { it.look() }) else emptyList()
            MonoTableDirector.Cue.Drew(seat, extra, faces)
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
        // Mid-draw (until a card fits, or the Roulette's flips) nothing in hand can be played.
        val drawing = m.phase == MonoChaosPhase.AWAITING_DRAW_UNTIL_PLAYABLE.name || m.phase == MonoChaosPhase.AWAITING_ROULETTE_DRAW.name
        val myTurn = waitingOnMe && me?.autoplay != true
        director.turn(myTurn, if (remaining == Long.MAX_VALUE) 0L else remaining)
        setState {
            copy(
                leaveDialog = leave.ui(now, m.spectators.map { it.name }, m.me != null, roomMatch = m.roomId != null),
                // The end-game vote comes first: play is paused until it is decided.
                sheet = m.endVote?.let { TableSheetUi.EndVote(EndVoteUiMapper.of(it, m.me?.seat, m.player(it.proposerSeat)?.name.orEmpty(), MonoStrings.monoEndRanking, now)) }
                    ?: sheets.current(now),
                loaded = true,
                error = null,
                match = m,
                myTurn = myTurn,
                // With a penalty on the table, stacking is the only legal
                // move; otherwise it is the usual match rules. After a
                // draw, only the drawn card is in play.
                playableIndexes = if (waitingOnMe && !drawing) {
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
                audience = MonoUiMapper.chaosAudience(m, talking, mutedForMe),
                // A spectator's mic is on their spectator entry.
                micOn = (me?.micOn ?: m.spectators.firstOrNull { it.userId == m.viewerUserId }?.micOn) == true,
                speakerOn = speakerOn,
                mutedForMe = mutedForMe,
                autoplay = me?.autoplay == true,
                talking = talking,
                vote = MonoUiMapper.vote(m.continueVote, me?.seat, now),
                // The finisher's banner shows first; the board follows once it has had its moment.
                finishBanner = banner?.takeIf { now - it.second < BANNER_TIME }?.first,
                penaltySnack = snack?.takeIf { now - it.second < SNACK_TIME }?.first,
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
     * stack, takes it) after a short beat so they see why. Drawing until a
     * card fits, and the Roulette, go one card at a time: each new card is
     * another step, a beat apart. A drawn card that can be played is then
     * theirs to play or pass.
     */
    private fun autoDraw(m: MonoChaosMatch) {
        val shown = state.value
        val drawing = m.phase == MonoChaosPhase.AWAITING_DRAW_UNTIL_PLAYABLE.name || m.phase == MonoChaosPhase.AWAITING_ROULETTE_DRAW.name
        val playing = m.phase == MonoChaosPhase.AWAITING_PLAY.name || shown.canStack || drawing
        if (!shown.myTurn || !playing || shown.canPass || shown.playableIndexes.isNotEmpty()) return
        // One step per card: the hand's size tells the steps of a turn apart.
        val step = DrawStep(m.currentSeat, m.turnEndsAt, m.me?.hand?.size ?: 0)
        if (autoDrewFor == step) return
        autoDrewFor = step
        if (shown.canStack) note(MonoStrings.monoNoteStackOnYou(shown.activeStackPenalty))
        scope.launch {
            delay(if (shown.canStack) AUTO_STACK_DELAY else if (drawing) AUTO_STEP_DELAY else AUTO_DRAW_DELAY)
            val now = state.value
            if (now.myTurn && !now.canPass && now.playableIndexes.isEmpty() && match?.let { DrawStep(it.currentSeat, it.turnEndsAt, it.me?.hand?.size ?: 0) } == step) {
                send(if (now.canStack) MonoChaosCommand.AcceptStack else MonoChaosCommand.DrawUntilPlayable)
            }
        }
    }

    /** A short line across the table, in the finish banner's place, for a moment. */
    private fun note(text: String) {
        banner = text to clock.now()
        render()
    }

    /** Whether the user may ask to end the game now (none running, no keep-playing vote). */
    private fun canEndGame(): Boolean {
        val m = match ?: return false
        val me = m.me ?: return false
        return EndVoteUiMapper.canPropose(me.isCpu, me.presence, m.phase == MonoChaosPhase.FINISHED.name, m.endVote != null || m.continueVote != null)
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
        /** How long a penalty snackbar stays up. */
        val SNACK_TIME = 4.seconds
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
/** The server's word for a hand pass that goes the way of play. */
private const val CLOCKWISE = "clockwise"
/** The beat before the table draws for a user with nothing to play. */
private val AUTO_DRAW_DELAY = 700.milliseconds
/** Between one card and the next while drawing one at a time. */
private val AUTO_STEP_DELAY = 900.milliseconds
/** Longer for a stack, so its message can be read before the cards come. */
private val AUTO_STACK_DELAY = 2_500.milliseconds
