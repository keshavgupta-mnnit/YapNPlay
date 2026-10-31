package com.kglabs28.yapnplay.appengine.ui.screens.mono

import com.kglabs28.yapnplay.appengine.ui.components.table.MonoDeckButtons
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoCallButton
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoCaughtButton
import com.kglabs28.yapnplay.appengine.ui.components.seat.SeatHandoverDialog
import com.kglabs28.yapnplay.appengine.ui.components.seat.KeepPlayingVoteDialog
import com.kglabs28.yapnplay.appengine.ui.components.seat.EndGameConfirmDialog
import com.kglabs28.yapnplay.appengine.ui.components.common.AppPermission
import com.kglabs28.yapnplay.appengine.ui.components.common.rememberPermissionRequester
import androidx.compose.runtime.SideEffect
import com.kglabs28.yapnplay.appengine.ui.components.table.TableSpectatorVoiceButtons
import com.kglabs28.yapnplay.appengine.ui.components.table.flightsIntoHand
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoHandArrivals
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoColourChoice
import com.kglabs28.yapnplay.appengine.ui.components.moderation.TableSheetContent
import com.kglabs28.yapnplay.appengine.ui.components.moderation.TableSheetUi
import com.kglabs28.yapnplay.appengine.ui.components.seat.LeaveMatchDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import com.kglabs28.yapnplay.appengine.games.mono.ui.components.look
import com.kglabs28.yapnplay.appengine.ui.components.table.mark
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoSpot
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.draw.alpha
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.ui.components.table.arenaGlass
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.LifecycleStartEffect
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.MonoStrings
import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants
import com.kglabs28.yapnplay.appengine.games.mono.ui.components.MonoCardView
import com.kglabs28.yapnplay.appengine.games.mono.ui.components.hue
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.MatchLeaderboard
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoAnchors
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoCardFace
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoEmptyPile
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoFlightState
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoHandRow
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoLocalPod
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoPlayerPodUi
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoTablePrompt
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoTableArena
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoTableShell
import com.kglabs28.yapnplay.appengine.ui.components.table.TableGlassButton
import com.kglabs28.yapnplay.appengine.ui.components.table.fill
import com.kglabs28.yapnplay.appengine.ui.components.table.TableSeatPlan
import com.kglabs28.yapnplay.appengine.ui.components.table.rememberMonoAnchors
import com.kglabs28.yapnplay.appengine.ui.components.table.rememberMonoFlightState
import com.kglabs28.yapnplay.appengine.ui.placeholders.MonoArtwork

/**
 * Standard MONO table: the players' seats around the table, the draw and
 * discard piles on it, the user's seat over the MONO / CAUGHT button, the
 * hand and the Draw button along the bottom. Every card that moves flies
 * there. This file only assembles components — every rule lives on the
 * server.
 */
@Composable
fun MatchMonoScreen(
    matchId: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MatchMonoViewModel? = null,
) {
    val state = viewModel?.state?.collectAsState()?.value ?: MatchMonoState()
    val send: (MatchMonoIntent) -> Unit = { viewModel?.onIntent(it) }
    val anchors = rememberMonoAnchors()
    val flights = rememberMonoFlightState()

    val micPermission = rememberPermissionRequester(AppPermission.MICROPHONE)

    LaunchedEffect(viewModel, matchId) {
        viewModel?.onIntent(MatchMonoIntent.Load(matchId))
    }
    LaunchedEffect(viewModel) {
        viewModel?.effects?.collect { effect ->
            when (effect) {
                MatchMonoEffect.LeaveMatch -> onClose()
                is MatchMonoEffect.Fly -> flights.launch(effect.flights)
                MatchMonoEffect.RequestMicPermission -> micPermission.request { send(MatchMonoIntent.MicPermissionResult(it)) }
            }
        }
    }
    // Started/stopped covers leaving the screen and the app going to the background.
    LifecycleStartEffect(viewModel) {
        send(MatchMonoIntent.ScreenVisible(true))
        onStopOrDispose { send(MatchMonoIntent.ScreenVisible(false)) }
    }

    MatchMonoContent(state = state, send = send, onClose = onClose, anchors = anchors, flights = flights, modifier = modifier)
}

/** The table itself, drawn from [state] alone (the screen above wires it to its view model). */
@Composable
fun MatchMonoContent(
    state: MatchMonoState,
    send: (MatchMonoIntent) -> Unit,
    onClose: () -> Unit,
    anchors: MonoAnchors,
    flights: MonoFlightState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        val match = state.match
        val currentSeat = match?.currentSeat ?: 0
        val pods = state.opponents.map { opponent ->
            MonoUiMapper.card(
                player = opponent,
                currentSeat = currentSeat,
                turnEndsAtMillis = state.turnEndsAtMillis,                turnTotalMs = state.turnTotalMs,
                places = state.places,
                calledSeat = match?.monoCalledSeat,
                talking = state.talking,
                mutedForMe = state.mutedForMe,
                mutedTalkers = state.mutedTalkers,
            )
        }
        val me = match?.me
        val (leftPods, topPods, rightPods) = TableSeatPlan.zones(pods, watching = me == null)

        MonoTableShell(
            title = MonoStrings.monoTitle,
            subtitle = state.title,
            audience = state.audience,
            showAudience = state.audience.isNotEmpty(),
            spectatorCount = state.spectatorCount.toString(),
            elapsed = state.elapsed,
            onSettings = { send(MatchMonoIntent.OpenSettings) },
            onExit = { send(MatchMonoIntent.ExitClicked) },
            leftPods = leftPods,
            topPods = topPods,
            rightPods = rightPods,
            anchors = anchors,
            flights = flights,
            logo = { MonoArtwork(modifier = it) },
            // Choices sit over the piles only, so the hand and every seat stay in view.
            prompt = when {
                state.awaitingColorChoice -> ({ MonoTablePrompt(MonoStrings.monoChooseColour) { MonoColourChoice(MonoConstants.Color.entries.map { MonoStrings.monoColourName(it.name) to it.hue().fill }, { send(MatchMonoIntent.ChooseColor(MonoConstants.Color.entries[it])) }) } })
                else -> null
            },
            arena = { MonoArena(state = state, anchors = anchors, flights = flights, send = send) },
            localPod = {
                MonoLocalPod(
                    card = me?.let {
                        MonoUiMapper.card(
                            player = it,
                            currentSeat = currentSeat,
                            turnEndsAtMillis = state.turnEndsAtMillis,                            turnTotalMs = state.turnTotalMs,
                            places = state.places,
                            calledSeat = match.monoCalledSeat,
                            talking = state.talking,
                            mutedForMe = state.mutedForMe,
                            mutedTalkers = state.mutedTalkers,
                        )
                    } ?: MonoPlayerPodUi(
                        seat = 1,
                        name = MonoStrings.monoYourTurn,
                        level = "",
                        tint = YapNPlayTheme.colors.TextPrimary,
                        isMe = true,
                        isTurn = state.myTurn,
                        talking = false,
                        cards = state.hand.size,
                    ),
                    micOn = state.micOn,
                    autoplay = state.autoplay,
                    onToggleMic = { send(MatchMonoIntent.ToggleMic) },
                    onToggleAutoplay = { send(MatchMonoIntent.SetAutoplay(!state.autoplay)) },
                )
            },
            // CAUGHT, the hand, MONO along the bottom (gone once the user is out).
            caughtButton = { if (!state.outOfPlay) MonoCaughtButton(onClick = { send(MatchMonoIntent.Caught) }) },
            hand = { MonoHand(state = state, send = send, anchors = anchors, flights = flights) },
            monoButton = { if (!state.outOfPlay) MonoCallButton(onClick = { send(MatchMonoIntent.CallMono) }) },
            watching = me == null,
            watcherControls = {
                TableSpectatorVoiceButtons(
                    micOn = state.micOn,
                    speakerOn = state.speakerOn,
                    onToggleMic = { send(MatchMonoIntent.ToggleMic) },
                    onToggleSpeaker = { send(MatchMonoIntent.ToggleSpeaker) },
                )
            },
            onToggleHear = { send(MatchMonoIntent.ToggleHear(it)) },
            onPodLongPress = { send(MatchMonoIntent.PlayerLongPressed(it)) },
            onToggleHearSpectator = { send(MatchMonoIntent.ToggleHearSpectator(it)) },
        )

        // A seat dialog (offered a seat, idle check, the leader's pick), the same in every game.
        state.seatModal?.let { modal ->
            BlockingOverlay(visible = true, onDismiss = null) { SeatHandoverDialog(modal, { send(MatchMonoIntent.Seat(it)) }) }
        }
        // The keep-playing vote over the dimmed table, like every game's votes (a sheet, if open, comes first).
        if (state.sheet == null) {
            state.vote?.let { v ->
                BlockingOverlay(visible = true, onDismiss = null) {
                    KeepPlayingVoteDialog(v.secondsLeft, v.yes, v.no, v.voters, v.canVote) { send(MatchMonoIntent.VoteContinue(it)) }
                }
            }
        }
        state.sheet?.let { sheet ->
            // A report to vote on needs an answer; the rest close on a tap outside.
            BlockingOverlay(visible = true, onDismiss = if (sheet is TableSheetUi.Vote || sheet is TableSheetUi.EndVote) null else ({ send(MatchMonoIntent.CloseSheet) })) {
                TableSheetContent(
                    sheet = sheet,
                    onToggleEffects = { send(MatchMonoIntent.ToggleEffects) },
                    onToggleMusic = { send(MatchMonoIntent.ToggleMusic) },
                    onOpenReport = { send(MatchMonoIntent.OpenReport) },
                    onSelectReportPerson = { send(MatchMonoIntent.SelectReportPerson(it)) },
                    onSubmitReport = { send(MatchMonoIntent.SubmitReport) },
                    onVote = { send(MatchMonoIntent.Vote(it)) },
                    onClose = { send(MatchMonoIntent.CloseSheet) },
                    rules = MonoStrings.monoRules,
                    onEndGame = { send(MatchMonoIntent.ProposeEnd) },
                    onEndVote = { send(MatchMonoIntent.VoteEnd(it)) },
                )
            }
        }
        state.leaveDialog?.let { dialog ->
            BlockingOverlay(visible = true, onDismiss = { send(MatchMonoIntent.CancelExit) }) {
                LeaveMatchDialog(
                    onStay = { send(MatchMonoIntent.CancelExit) },
                    onLeave = { send(MatchMonoIntent.ConfirmExit) },
                    message = dialog.message,
                    options = dialog.options,
                    selectedIndex = dialog.selectedIndex,
                    onSelect = { send(MatchMonoIntent.PickSeatTaker(it)) },
                    countdown = dialog.countdown,
                )
            }
        }
        if (state.confirmEnd) {
            BlockingOverlay(visible = true, onDismiss = { send(MatchMonoIntent.CancelEnd) }) {
                EndGameConfirmDialog(onCancel = { send(MatchMonoIntent.CancelEnd) }, onEnd = { send(MatchMonoIntent.ConfirmEnd) })
            }
        }
        state.leaderboard?.let { board ->
            BlockingOverlay(visible = true) {
                MatchLeaderboard(board = board, gameArt = { MonoArtwork(modifier = it) }, onPlayAgain = onClose)
            }
        }
        state.error?.let { MonoErrorOverlay(it) }
    }
}

/** The two piles on the table. A card in flight to the discard pile shows there only once it lands. */
@Composable
private fun MonoArena(state: MatchMonoState, anchors: MonoAnchors, flights: MonoFlightState, send: (MatchMonoIntent) -> Unit) {
    val match = state.match
    val dimens = YapNPlayTheme.dimens
    MonoTableArena(
        hue = state.topColour?.hue(),
        // The ring's arrows follow the direction of play.
        clockwise = !state.reversed,
        drawPileCount = match?.drawPileCount ?: 0,
        discardPileCount = match?.discardPileCount ?: 0,
        activeStackPenalty = state.activeStackPenalty,
        note = state.finishBanner,
        snack = state.penaltySnack,
        cardWidth = dimens.monoPileCardWidth,
        anchors = anchors,
        // The deck draws too (or takes the stack, like the Draw button).
        onDeckClick = { send(if (state.canStack) MatchMonoIntent.AcceptStack else MatchMonoIntent.DrawCard) },
        // On the user's turn only: draw (or take the stack), and pass after a draw.
        deckButtons = if (state.myTurn && !state.outOfPlay) ({ MonoDeckButtons(state.canStack, state.canPass, state.activeStackPenalty, { send(if (state.canStack) MatchMonoIntent.AcceptStack else MatchMonoIntent.DrawCard) }, { send(MatchMonoIntent.Pass) }) }) else null,
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = Spacing.Xs),
    ) {
        val topCard = match?.topCard
        val cover = flights.discardCover
        when {
            flights.coveringDiscard && cover != null -> MonoCardFace(look = cover, modifier = Modifier.fillMaxWidth())
            flights.coveringDiscard || topCard == null -> MonoEmptyPile(modifier = Modifier.fillMaxWidth())
            else -> MonoCardView(card = topCard, modifier = Modifier.fillMaxWidth())
        }
    }
}

/**
 * The user's hand, fanned and sorted by colour (power cards last). Off
 * their turn every card is dimmed and can't be tapped; on it, the cards
 * they can play rise a little with a bright rim. A big hand uses smaller
 * cards and scrolls rather than hiding numbers. A card still flying in
 * keeps its place empty until it lands; a tapped card notes where it is,
 * so its flight to the pile starts there.
 */
@Composable
private fun MonoHand(state: MatchMonoState, send: (MatchMonoIntent) -> Unit, anchors: MonoAnchors, flights: MonoFlightState) {
    val dimens = YapNPlayTheme.dimens
    val order = state.handOrder
    val width = if (state.hand.size > AppConstants.MONO_BIG_HAND) dimens.monoHandCardWidthSmall else dimens.monoHandCardWidth
    val spots = remember { mutableMapOf<Int, Rect>() }
    // Slots whose card is still in the air: hidden until it lands, and its flight aims at the slot.
    val arriving = MonoHandArrivals.slots(order, state.hand, { it.look() }, flights.flightsIntoHand)
    // A flight can start after its card was laid out (no new layout comes): aim it at the slot already measured.
    SideEffect { arriving.forEach { (index, flightId) -> spots[index]?.let { anchors.mark(MonoSpot.HandSlot(flightId), it) } } }
    MonoHandRow(
        handSize = state.hand.size,
        cardWidth = width,
        modifier = Modifier.fillMaxWidth(),
    ) { slot ->
        // Shown sorted by colour; a tap still plays the card's real index.
        val index = order[slot]
        MonoCardView(
            card = state.hand[index],
            isSelected = state.myTurn && index in state.playableIndexes,
            isPlayable = state.myTurn,
            onClick = if (state.myTurn) {
                {
                    spots[index]?.let { anchors.mark(MonoSpot.PlayedCard, it) }
                    send(MatchMonoIntent.TapCard(index))
                }
            } else {
                null
            },
            modifier = Modifier
                .width(width)
                .alpha(if (index in arriving) 0f else 1f)
                .onGloballyPositioned { coordinates ->
                    val rect = coordinates.boundsInRoot()
                    spots[index] = rect
                    arriving[index]?.let { anchors.mark(MonoSpot.HandSlot(it), rect) }
                },
        )
    }
}

/** The keep-playing vote: keep or end, with the tally and the countdown. */
/** Covers the table when the match could not be opened. */
@Composable
private fun MonoErrorOverlay(message: String) {
    val colors = YapNPlayTheme.colors
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.Lg),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = message,
            style = YapNPlayTheme.typography.body.copy(
                color = colors.Error,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

