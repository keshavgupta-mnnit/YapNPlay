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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoColourPicker
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoEmptyPile
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoHandRow
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoLocalPod
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoPromptModal
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoPlayerPodUi
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoTableArena
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoTableShell
import com.kglabs28.yapnplay.appengine.ui.components.table.TableGlassButton
import com.kglabs28.yapnplay.appengine.ui.components.table.fill
import com.kglabs28.yapnplay.appengine.ui.components.table.monoTableZones
import com.kglabs28.yapnplay.appengine.ui.placeholders.CozyHomeBackground

/**
 * Standard MONO table: the players' pods around the felt, the draw and
 * discard piles in the middle, the user's pod, the MONO! callout, the
 * hand and the draw button along the bottom. This file only assembles
 * components — every rule lives in the engine.
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

    LaunchedEffect(viewModel, matchId) {
        viewModel?.onIntent(MatchMonoIntent.Load(matchId))
    }
    LaunchedEffect(viewModel) {
        viewModel?.effects?.collect { effect ->
            when (effect) {
                MatchMonoEffect.LeaveMatch -> onClose()
                MatchMonoEffect.OpenSettingsDialog -> Unit
            }
        }
    }
    // Started/stopped covers leaving the screen and the app going to the background.
    LifecycleStartEffect(viewModel) {
        send(MatchMonoIntent.ScreenVisible(true))
        onStopOrDispose { send(MatchMonoIntent.ScreenVisible(false)) }
    }

    CozyHomeBackground(modifier = modifier, frosted = true) {
        val match = state.match
        val currentSeat = match?.currentSeat ?: 0
        val pods = state.opponents.map { opponent ->
            MonoUiMapper.card(
                player = opponent,
                currentSeat = currentSeat,
                timerFraction = state.timerFraction,
                places = state.places,
                calledSeat = match?.monoCalledSeat,
            )
        }
        val (leftPods, topPods, rightPods) = monoTableZones(pods)
        val me = match?.me

        MonoTableShell(
            title = MonoStrings.monoTitle,
            subtitle = state.title,
            audience = state.audience,
            showAudience = state.audience.isNotEmpty(),
            spectatorCount = state.spectatorCount.toString(),
            spectatorLabel = MonoStrings.monoSpectators,
            elapsed = state.elapsed,
            turnLabel = state.turnClock,
            turnIsMine = state.myTurn,
            onSettings = { send(MatchMonoIntent.OpenSettings) },
            onExit = { send(MatchMonoIntent.ExitClicked) },
            leftPods = leftPods,
            topPods = topPods,
            rightPods = rightPods,
            arena = { MonoArena(state = state) },
            localPod = {
                MonoLocalPod(
                    card = me?.let {
                        MonoUiMapper.card(
                            player = it,
                            currentSeat = currentSeat,
                            timerFraction = state.timerFraction,
                            places = state.places,
                            calledSeat = match?.monoCalledSeat,
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
            callButton = { MonoCallButton(state = state, send = send) },
            hand = { MonoHand(state = state, send = send) },
            drawButton = { MonoDrawButton(state = state, send = send) },
        )

        if (state.awaitingColorChoice) MonoPromptModal(MonoStrings.monoChooseColour) { MonoColourPrompt(send) }
        if (state.awaitingSwapChoice) MonoPromptModal(MonoStrings.monoSwapTitle) { MonoSwapPrompt(state = state, send = send) }
        if (state.finished) MonoResultOverlay(state, onClose)
        state.error?.let { MonoErrorOverlay(it) }
    }
}

/** The felt the piles sit on, with the draw and discard piles centred. */
@Composable
private fun MonoArena(state: MatchMonoState) {
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
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = Spacing.Xs),
    ) {
        val topCard = match?.topCard
        if (topCard == null) {
            MonoEmptyPile(modifier = Modifier.fillMaxWidth())
        } else {
            MonoCardView(
                card = topCard,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/** The user's hand, fanned. */
@Composable
private fun MonoHand(state: MatchMonoState, send: (MatchMonoIntent) -> Unit) {
    val dimens = YapNPlayTheme.dimens
    MonoHandRow(
        handSize = state.hand.size,
        cardWidth = dimens.monoHandCardWidth,
        modifier = Modifier.fillMaxWidth(),
    ) { index ->
        MonoCardView(
            card = state.hand[index],
            isSelected = state.selectedCardIndex == index,
            // A card the table would refuse is drawn pushed back.
            isPlayable = index in state.playableIndexes,
            onClick = { send(MatchMonoIntent.TapCard(index)) },
            modifier = Modifier.width(dimens.monoHandCardWidth),
        )
    }
}

/** The MONO! callout, lit only while the window is open. */
@Composable
private fun MonoCallButton(state: MatchMonoState, send: (MatchMonoIntent) -> Unit) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Column(
        modifier = Modifier.width(dimens.monoCallWidth),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Always on the table, as in the design; it only lights up when a call is open.
        PillButton(
            text = MonoStrings.monoCallMono,
            onClick = { send(MatchMonoIntent.CallMono) },
            enabled = state.canCallMono,
            trailingIcon = null,
            gradient = listOf(colors.Error, colors.Warning),
            contentColor = colors.TextPrimary,
            height = dimens.monoCallHeight,
            modifier = Modifier.width(dimens.monoCallWidth),
        )
        // A stack on the user (stacking house rule): take the penalty instead of stacking.
        if (state.canStack) {
            PillButton(
                text = MonoStrings.monoTakePenalty(state.activeStackPenalty),
                onClick = { send(MatchMonoIntent.AcceptStack) },
                gradient = listOf(colors.Warning, colors.Error),
                contentColor = colors.Background,
                height = dimens.monoCallHeight,
                modifier = Modifier.width(dimens.monoCallWidth),
            )
        }
    }
}

/** The draw button; it is the only move when nothing can be played. */
@Composable
private fun MonoDrawButton(state: MatchMonoState, send: (MatchMonoIntent) -> Unit) {
    val colors = YapNPlayTheme.colors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        TableGlassButton(
            text = MonoStrings.monoDrawCard,
            onClick = { send(MatchMonoIntent.DrawCard) },
            fill = listOf(colors.Panel, colors.GlassSurface),
            textColor = colors.TextPrimary,
            // Under a stack the only moves are stacking or taking the penalty.
            enabled = state.myTurn && !state.canStack,
        )
        // After a draw the player may decline the card they drew.
        if (state.canPass) {
            TableGlassButton(
                text = MonoStrings.monoPass,
                onClick = { send(MatchMonoIntent.Pass) },
                fill = listOf(colors.Panel, colors.GlassSurface),
                textColor = colors.TextSecondary,
            )
        }
    }
}

/** The Prism picker, shown when a Prism card leaves the colour open. */
@Composable
private fun MonoColourPrompt(send: (MatchMonoIntent) -> Unit) {
    val colors = YapNPlayTheme.colors
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

/** Who won, once someone has emptied their hand. */
@Composable
private fun MonoResultOverlay(state: MatchMonoState, onClose: () -> Unit) {
    val winner = state.winnerSeat
    val won = winner != null && winner == state.match?.me?.seat
    BlockingOverlay(visible = true) {
        ModalCard(
            title = if (won) {
                MonoStrings.monoYouWin
            } else {
                MonoStrings.monoWinner(state.match?.player(winner ?: -1)?.name.orEmpty())
            },
            subtitle = MonoStrings.monoLeaveWhenDone,
            actions = {
                TableGlassButton(
                    text = MonoStrings.monoClose,
                    onClick = onClose,
                    fill = listOf(YapNPlayTheme.colors.CtaGreenStart, YapNPlayTheme.colors.CtaGreenEnd),
                    textColor = YapNPlayTheme.colors.CtaGreenContent,
                )
            },
        )
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

