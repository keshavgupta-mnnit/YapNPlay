package com.kglabs28.yapnplay.appengine.ui.screens.mono

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
import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants
import com.kglabs28.yapnplay.appengine.games.mono.ui.components.MonoCardView
import com.kglabs28.yapnplay.appengine.games.mono.ui.components.hue
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.MatchLeaderboard
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoAnchors
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoCallButton
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoCardFace
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoColourPicker
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoDrawButton
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
import com.kglabs28.yapnplay.appengine.ui.components.table.monoTableZones
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

    LaunchedEffect(viewModel, matchId) {
        viewModel?.onIntent(MatchMonoIntent.Load(matchId))
    }
    LaunchedEffect(viewModel) {
        viewModel?.effects?.collect { effect ->
            when (effect) {
                MatchMonoEffect.LeaveMatch -> onClose()
                MatchMonoEffect.OpenSettingsDialog -> Unit
                is MatchMonoEffect.Fly -> flights.launch(effect.flights)
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
                timerFraction = state.timerFraction,
                places = state.places,
                calledSeat = match?.monoCalledSeat,
                talking = state.talking,
            )
        }
        val (leftPods, topPods, rightPods) = monoTableZones(pods)
        val me = match?.me

        MonoTableShell(
            title = MonoStrings.monoTitle,
            subtitle = state.title,
            audience = state.audience,
            showAudience = state.audience.isNotEmpty(),
            spectatorLabel = MonoStrings.monoSpectatorCount(state.spectatorCount),
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
                state.awaitingColorChoice -> ({ MonoTablePrompt(MonoStrings.monoChooseColour) { MonoColourPrompt(send) } })
                state.awaitingSwapChoice -> ({ MonoTablePrompt(MonoStrings.monoSwapTitle) { MonoSwapPrompt(state = state, send = send) } })
                state.vote != null -> ({ MonoTablePrompt(MonoStrings.monoVoteTitle) { MonoVotePrompt(state.vote) { send(MatchMonoIntent.VoteContinue(it)) } } })
                else -> null
            },
            arena = { MonoArena(state = state, anchors = anchors, flights = flights, send = send) },
            localPod = {
                MonoLocalPod(
                    card = me?.let {
                        MonoUiMapper.card(
                            player = it,
                            currentSeat = currentSeat,
                            timerFraction = state.timerFraction,
                            places = state.places,
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
                    onToggleMic = { send(MatchMonoIntent.SetMic(!state.micOn)) },
                    onToggleAutoplay = { send(MatchMonoIntent.SetAutoplay(!state.autoplay)) },
                )
            },
            callButton = {
                // One button for both: it calls MONO when the user can, else CAUGHT on whoever is open.
                MonoCallButton(
                    onClick = { send(if (state.canCallMono) MatchMonoIntent.CallMono else MatchMonoIntent.Caught) },
                )
            },
            hand = { MonoHand(state = state, send = send, anchors = anchors, flights = flights) },
            drawButton = { MonoDrawControls(state = state, send = send) },
        )

        state.finishBanner?.let { MonoFinishBanner(it, Modifier.align(Alignment.Center)) }
        if (state.confirmingExit) {
            BlockingOverlay(visible = true, onDismiss = { send(MatchMonoIntent.CancelExit) }) {
                ModalCard(
                    title = MonoStrings.monoLeaveTitle,
                    subtitle = MonoStrings.monoLeaveBody,
                    actions = {
                        TableGlassButton(
                            text = MonoStrings.monoStay,
                            onClick = { send(MatchMonoIntent.CancelExit) },
                            fill = listOf(YapNPlayTheme.colors.Panel, YapNPlayTheme.colors.GlassSurface),
                            textColor = YapNPlayTheme.colors.TextPrimary,
                        )
                        TableGlassButton(
                            text = MonoStrings.monoLeave,
                            onClick = { send(MatchMonoIntent.ConfirmExit) },
                            fill = listOf(YapNPlayTheme.colors.ArenaCallFillTop, YapNPlayTheme.colors.ArenaCallFillBottom),
                            textColor = YapNPlayTheme.colors.TextPrimary,
                        )
                    },
                )
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
        cardWidth = dimens.monoPileCardWidth,
        anchors = anchors,
        // The deck draws too (or takes the stack, like the Draw button).
        onDeckClick = { send(if (state.canStack) MatchMonoIntent.AcceptStack else MatchMonoIntent.DrawCard) },
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
                .alpha(if (index in hidden) 0f else 1f)
                .onGloballyPositioned { spots[index] = it.boundsInRoot() },
        )
    }
}

/**
 * The Draw button; under a stack it takes the penalty instead ("Take +4").
 * After a draw the player may also pass on the card they drew.
 */
@Composable
private fun MonoDrawControls(state: MatchMonoState, send: (MatchMonoIntent) -> Unit) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        if (state.canPass) {
            MonoDrawButton(text = MonoStrings.monoPass, onClick = { send(MatchMonoIntent.Pass) })
        }
        MonoDrawButton(
            text = if (state.canStack) MonoStrings.monoTakeButton(state.activeStackPenalty) else MonoStrings.monoDrawButton,
            onClick = { send(if (state.canStack) MatchMonoIntent.AcceptStack else MatchMonoIntent.DrawCard) },
        )
    }
}

/** The Prism picker, shown when a Prism card leaves the colour open. */
@Composable
private fun MonoColourPrompt(send: (MatchMonoIntent) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        MonoColourPicker(
            options = MonoConstants.Color.entries.map { it.name to it.hue().fill },
            onPick = { index -> send(MatchMonoIntent.ChooseColor(MonoConstants.Color.entries[index])) },
        )
    }
}

/** The Rule of 7: pick the opponent whose hand to trade with. */
@Composable
private fun MonoSwapPrompt(state: MatchMonoState, send: (MatchMonoIntent) -> Unit) {
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
                        .clickable { send(MatchMonoIntent.ChooseSwapTarget(opponent.seat)) }
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
