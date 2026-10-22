package com.kglabs28.yapnplay.appengine.ui.screens.mono

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
import com.kglabs28.yapnplay.appengine.games.monochaos.ui.components.look
import com.kglabs28.yapnplay.appengine.ui.components.table.mark
import com.kglabs28.yapnplay.appengine.ui.components.table.arrivingInHand
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
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants
import com.kglabs28.yapnplay.appengine.games.monochaos.ui.components.MonoChaosCardView
import com.kglabs28.yapnplay.appengine.games.monochaos.ui.components.hue
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.MatchLeaderboard
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoAnchors
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoCallButtons
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoCardFace
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoColourPicker
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoDrawButton
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoFlightState
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoEmptyPile
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoHandRow
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoLocalPod
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoTablePrompt
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoPlayerPodUi
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoTableArena
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoTableShell
import com.kglabs28.yapnplay.appengine.ui.components.table.TableGlassButton
import com.kglabs28.yapnplay.appengine.ui.components.table.fill
import com.kglabs28.yapnplay.appengine.ui.components.table.monoTableZones
import com.kglabs28.yapnplay.appengine.ui.components.table.rememberMonoAnchors
import com.kglabs28.yapnplay.appengine.ui.components.table.rememberMonoFlightState
import com.kglabs28.yapnplay.appengine.ui.placeholders.MonoChaosArtwork

/**
 * MONO: Complete Chaos table. The same table as Standard MONO, with the
 * penalty stack counter over the discard pile, the Rule of 7 and the
 * Roulette's colour prompt added. This file only assembles components —
 * every rule lives in the engine.
 */
@Composable
fun MatchMonoChaosScreen(
    matchId: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MatchMonoChaosViewModel? = null,
) {
    val state = viewModel?.state?.collectAsState()?.value ?: MatchMonoChaosState()
    val send: (MatchMonoChaosIntent) -> Unit = { viewModel?.onIntent(it) }
    val anchors = rememberMonoAnchors()
    val flights = rememberMonoFlightState()

    LaunchedEffect(viewModel, matchId) {
        viewModel?.onIntent(MatchMonoChaosIntent.Load(matchId))
    }
    LaunchedEffect(viewModel) {
        viewModel?.effects?.collect { effect ->
            when (effect) {
                MatchMonoChaosEffect.LeaveMatch -> onClose()
                is MatchMonoChaosEffect.Fly -> flights.launch(effect.flights)
            }
        }
    }
    LifecycleStartEffect(viewModel) {
        send(MatchMonoChaosIntent.ScreenVisible(true))
        onStopOrDispose { send(MatchMonoChaosIntent.ScreenVisible(false)) }
    }

    MatchMonoChaosContent(state = state, send = send, onClose = onClose, anchors = anchors, flights = flights, modifier = modifier)
}

/** The table itself, drawn from [state] alone (the screen above wires it to its view model). */
@Composable
fun MatchMonoChaosContent(
    state: MatchMonoChaosState,
    send: (MatchMonoChaosIntent) -> Unit,
    onClose: () -> Unit,
    anchors: MonoAnchors,
    flights: MonoFlightState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        val match = state.match
        val currentSeat = match?.currentSeat ?: 0
        val pods = state.opponents.map { opponent ->
            MonoUiMapper.chaosCard(
                player = opponent,
                currentSeat = currentSeat,
                timerFraction = state.timerFraction,
                places = state.places,
                eliminated = state.eliminated,
                calledSeat = match?.monoCalledSeat,
                talking = state.talking,
            )
        }
        val (leftPods, topPods, rightPods) = monoTableZones(pods)
        val me = match?.me

        MonoTableShell(
            title = MonoStrings.monoChaosTitle,
            subtitle = state.title,
            audience = state.audience,
            showAudience = state.audience.isNotEmpty(),
            spectatorLabel = MonoStrings.monoSpectatorCount(state.spectatorCount),
            elapsed = state.elapsed,
            onSettings = { send(MatchMonoChaosIntent.OpenSettings) },
            onExit = { send(MatchMonoChaosIntent.ExitClicked) },
            leftPods = leftPods,
            topPods = topPods,
            rightPods = rightPods,
            anchors = anchors,
            flights = flights,
            logo = { MonoChaosArtwork(modifier = it) },
            // Choices sit over the piles only, so the hand and every seat (swap partners too) stay in view.
            prompt = when {
                state.awaitingColorChoice -> ({ MonoTablePrompt(MonoStrings.monoChooseColour) { MonoChaosColourPrompt(send) } })
                state.awaitingRouletteChoice -> ({ MonoTablePrompt(MonoStrings.monoChooseRouletteColour) { MonoChaosRoulettePrompt(send) } })
                state.awaitingSwapChoice -> ({ MonoTablePrompt(MonoStrings.monoSwapTitle) { MonoChaosSwapPrompt(state = state, send = send) } })
                state.vote != null -> ({ MonoTablePrompt(MonoStrings.monoVoteTitle) { MonoVotePrompt(state.vote) { send(MatchMonoChaosIntent.VoteContinue(it)) } } })
                else -> null
            },
            arena = { MonoChaosArena(state = state, anchors = anchors, flights = flights, send = send) },
            localPod = {
                MonoLocalPod(
                    card = me?.let {
                        MonoUiMapper.chaosCard(
                            player = it,
                            currentSeat = currentSeat,
                            timerFraction = state.timerFraction,
                            places = state.places,
                            eliminated = state.eliminated,
                            calledSeat = match.monoCalledSeat,
                            talking = state.talking,
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
                    onToggleMic = { send(MatchMonoChaosIntent.SetMic(!state.micOn)) },
                    onToggleAutoplay = { send(MatchMonoChaosIntent.SetAutoplay(!state.autoplay)) },
                )
            },
            callButton = {
                // CAUGHT first, then MONO, then the hand and Draw along the bottom.
                MonoCallButtons(
                    onCaught = { send(MatchMonoChaosIntent.Caught) },
                    onMono = { send(MatchMonoChaosIntent.CallMono) },
                )
            },
            hand = { MonoChaosHand(state = state, send = send, anchors = anchors, flights = flights) },
            drawButton = { MonoChaosDrawControls(state = state, send = send) },
        )

        state.finishBanner?.let { MonoFinishBanner(it, Modifier.align(Alignment.Center)) }
        state.sheet?.let { sheet ->
            // A report to vote on needs an answer; the rest close on a tap outside.
            BlockingOverlay(visible = true, onDismiss = if (sheet is TableSheetUi.Vote) null else ({ send(MatchMonoChaosIntent.CloseSheet) })) {
                TableSheetContent(
                    sheet = sheet,
                    onToggleEffects = { send(MatchMonoChaosIntent.ToggleEffects) },
                    onToggleMusic = { send(MatchMonoChaosIntent.ToggleMusic) },
                    onOpenReport = { send(MatchMonoChaosIntent.OpenReport) },
                    onSelectReportPerson = { send(MatchMonoChaosIntent.SelectReportPerson(it)) },
                    onSubmitReport = { send(MatchMonoChaosIntent.SubmitReport) },
                    onVote = { send(MatchMonoChaosIntent.Vote(it)) },
                    onClose = { send(MatchMonoChaosIntent.CloseSheet) },
                )
            }
        }
        state.leaveDialog?.let { dialog ->
            BlockingOverlay(visible = true, onDismiss = { send(MatchMonoChaosIntent.CancelExit) }) {
                LeaveMatchDialog(
                    onStay = { send(MatchMonoChaosIntent.CancelExit) },
                    onLeave = { send(MatchMonoChaosIntent.ConfirmExit) },
                    message = dialog.message,
                    options = dialog.options,
                    selectedIndex = dialog.selectedIndex,
                    onSelect = { send(MatchMonoChaosIntent.PickSeatTaker(it)) },
                    countdown = dialog.countdown,
                )
            }
        }
        state.leaderboard?.let { board ->
            BlockingOverlay(visible = true) {
                MatchLeaderboard(board = board, gameArt = { MonoChaosArtwork(modifier = it) }, onPlayAgain = onClose)
            }
        }
        state.error?.let { MonoChaosErrorOverlay(it) }
    }
}

/** The two piles on the table. A card in flight to the discard pile shows there only once it lands. */
@Composable
private fun MonoChaosArena(state: MatchMonoChaosState, anchors: MonoAnchors, flights: MonoFlightState, send: (MatchMonoChaosIntent) -> Unit) {
    val match = state.match
    val dimens = YapNPlayTheme.dimens
    MonoTableArena(
        hue = state.topColour?.hue(),
        clockwise = !state.reversed,
        drawPileCount = match?.drawPileCount ?: 0,
        discardPileCount = match?.discardPileCount ?: 0,
        activeStackPenalty = state.activeStackPenalty,
        cardWidth = dimens.monoPileCardWidth,
        anchors = anchors,
        chaos = true,
        // The deck draws too (or takes the stack, like the Draw button).
        onDeckClick = { send(if (state.canStack) MatchMonoChaosIntent.AcceptStack else MatchMonoChaosIntent.DrawUntilPlayable) },
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = Spacing.Xs),
    ) {
        val topCard = match?.topCard
        val cover = flights.discardCover
        when {
            flights.coveringDiscard && cover != null -> MonoCardFace(look = cover, modifier = Modifier.fillMaxWidth())
            flights.coveringDiscard || topCard == null -> MonoEmptyPile(modifier = Modifier.fillMaxWidth())
            else -> MonoChaosCardView(card = topCard, modifier = Modifier.fillMaxWidth())
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
private fun MonoChaosHand(state: MatchMonoChaosState, send: (MatchMonoChaosIntent) -> Unit, anchors: MonoAnchors, flights: MonoFlightState) {
    val dimens = YapNPlayTheme.dimens
    val order = state.handOrder
    val width = if (state.hand.size > AppConstants.MONO_BIG_HAND) dimens.monoHandCardWidthSmall else dimens.monoHandCardWidth
    val spots = remember { mutableMapOf<Int, Rect>() }
    // Slots whose card is still in the air, matched by face.
    val arriving = flights.arrivingInHand.toMutableList()
    val hidden = order.filter { index -> arriving.remove(state.hand[index].look()) }.toSet()
    MonoHandRow(
        handSize = state.hand.size,
        cardWidth = width,
        modifier = Modifier.fillMaxWidth(),
    ) { slot ->
        // Shown sorted by colour; a tap still plays the card's real index.
        val index = order[slot]
        MonoChaosCardView(
            card = state.hand[index],
            isSelected = state.myTurn && index in state.playableIndexes,
            isPlayable = state.myTurn,
            onClick = if (state.myTurn) {
                {
                    spots[index]?.let { anchors.mark(MonoSpot.PlayedCard, it) }
                    send(MatchMonoChaosIntent.TapCard(index))
                }
            } else {
                null
            },
            modifier = Modifier
                .width(width)
                .alpha(if (index in hidden) 0f else 1f)
                .onGloballyPositioned { spots[index] = it.boundsInRoot() },
        )
    }
}

/**
 * The Draw button: it draws until something can be played. Under a stack
 * it takes the penalty instead ("Take +6"); after a draw the player may
 * also pass on the card they drew.
 */
@Composable
private fun MonoChaosDrawControls(state: MatchMonoChaosState, send: (MatchMonoChaosIntent) -> Unit) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        if (state.canPass) {
            MonoDrawButton(text = MonoStrings.monoPass, onClick = { send(MatchMonoChaosIntent.Pass) })
        }
        MonoDrawButton(
            text = if (state.canStack) MonoStrings.monoTakeButton(state.activeStackPenalty) else MonoStrings.monoDrawButton,
            onClick = { send(if (state.canStack) MatchMonoChaosIntent.AcceptStack else MatchMonoChaosIntent.DrawUntilPlayable) },
        )
    }
}

/** The Prism picker, shown when a Prism card leaves the colour open. */
@Composable
private fun MonoChaosColourPrompt(send: (MatchMonoChaosIntent) -> Unit) {
    val colors = YapNPlayTheme.colors
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        MonoColourPicker(
            options = MonoChaosConstants.Color.entries.map { it.name to it.hue().fill },
            onPick = { index -> send(MatchMonoChaosIntent.ChooseColor(MonoChaosConstants.Color.entries[index])) },
        )
    }
}

/** The Roulette: the victim names the colour they must draw to. */
@Composable
private fun MonoChaosRoulettePrompt(send: (MatchMonoChaosIntent) -> Unit) {
    val colors = YapNPlayTheme.colors
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        MonoColourPicker(
            options = MonoChaosConstants.Color.entries.map { it.name to it.hue().fill },
            onPick = { index -> send(MatchMonoChaosIntent.ChooseRouletteColor(MonoChaosConstants.Color.entries[index])) },
        )
    }
}

/** The Rule of 7: pick the opponent whose hand to trade with. */
@Composable
private fun MonoChaosSwapPrompt(state: MatchMonoChaosState, send: (MatchMonoChaosIntent) -> Unit) {
    val colors = YapNPlayTheme.colors
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        BasicText(
            text = MonoStrings.monoSwapBody,
            style = YapNPlayTheme.typography.caption.copy(
                color = colors.TextSecondary,
                textAlign = TextAlign.Center,
            ),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
            state.opponents.forEach { opponent ->
                BasicText(
                    text = opponent.name,
                    style = YapNPlayTheme.typography.label.copy(color = colors.TextPrimary),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .clip(YapNPlayTheme.shapes.button)
                        .background(colors.Panel)
                        .border(YapNPlayTheme.dimens.borderThin, colors.GlassShine, YapNPlayTheme.shapes.button)
                        .clickable { send(MatchMonoChaosIntent.ChooseSwapTarget(opponent.seat)) }
                        .padding(horizontal = Spacing.Md, vertical = Spacing.Sm),
                )
            }
        }
    }
}

/** The keep-playing vote: keep or end, with the tally and the countdown. */
@Composable
private fun MonoVotePrompt(vote: MonoVoteUi, onVote: (Boolean) -> Unit) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    BasicText(
        text = MonoStrings.monoVoteBody(vote.secondsLeft),
        style = typography.caption.copy(color = colors.TextSecondary, textAlign = TextAlign.Center),
    )
    BasicText(
        text = MonoStrings.monoVoteCount(vote.yes, vote.no, vote.voters),
        style = typography.label.copy(color = colors.TextPrimary),
    )
    if (vote.canVote) {
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
            TableGlassButton(
                text = MonoStrings.monoVoteEnd,
                onClick = { onVote(false) },
                fill = listOf(colors.Panel, colors.GlassSurface),
                textColor = colors.TextPrimary,
            )
            TableGlassButton(
                text = MonoStrings.monoVoteKeep,
                onClick = { onVote(true) },
                fill = listOf(colors.CtaGreenStart, colors.CtaGreenEnd),
                textColor = colors.CtaGreenContent,
            )
        }
    } else {
        BasicText(text = MonoStrings.monoVoteWaiting, style = typography.caption.copy(color = colors.TextSecondary))
    }
}

/** Covers the table when the match could not be opened. */
@Composable
private fun MonoChaosErrorOverlay(message: String) {
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

/** "🏆 Riya finishes 1st!": a gold glass banner over the table for a few seconds. */
@Composable
private fun MonoFinishBanner(text: String, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    BasicText(
        text = text,
        style = YapNPlayTheme.typography.title.copy(color = colors.ArenaCrown, fontWeight = FontWeight.Black, textAlign = TextAlign.Center),
        maxLines = 1,
        modifier = modifier
            .arenaGlass(YapNPlayTheme.shapes.pill, colors.ArenaCrown)
            .padding(horizontal = Spacing.Lg, vertical = Spacing.Sm),
    )
}
