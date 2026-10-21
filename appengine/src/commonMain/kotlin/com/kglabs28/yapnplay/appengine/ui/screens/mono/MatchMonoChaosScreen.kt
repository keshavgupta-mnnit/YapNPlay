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
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants
import com.kglabs28.yapnplay.appengine.games.monochaos.ui.components.MonoChaosCardView
import com.kglabs28.yapnplay.appengine.games.monochaos.ui.components.hue
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

    LaunchedEffect(viewModel, matchId) {
        viewModel?.onIntent(MatchMonoChaosIntent.Load(matchId))
    }
    LaunchedEffect(viewModel) {
        viewModel?.effects?.collect { effect ->
            when (effect) {
                MatchMonoChaosEffect.LeaveMatch -> onClose()
                MatchMonoChaosEffect.OpenSettingsDialog -> Unit
            }
        }
    }
    LifecycleStartEffect(viewModel) {
        send(MatchMonoChaosIntent.ScreenVisible(true))
        onStopOrDispose { send(MatchMonoChaosIntent.ScreenVisible(false)) }
    }

    CozyHomeBackground(modifier = modifier, frosted = true) {
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
            )
        }
        val (leftPods, topPods, rightPods) = monoTableZones(pods)
        val me = match?.me

        MonoTableShell(
            title = MonoStrings.monoChaosTitle,
            subtitle = state.title,
            audience = state.audience,
            showAudience = state.audience.isNotEmpty(),
            spectatorCount = state.spectatorCount.toString(),
            spectatorLabel = MonoStrings.monoSpectators,
            elapsed = state.elapsed,
            turnLabel = state.turnClock,
            turnIsMine = state.myTurn,
            onSettings = { send(MatchMonoChaosIntent.OpenSettings) },
            onExit = { send(MatchMonoChaosIntent.ExitClicked) },
            leftPods = leftPods,
            topPods = topPods,
            rightPods = rightPods,
            arena = { MonoChaosArena(state = state) },
            localPod = {
                MonoLocalPod(
                    card = me?.let {
                        MonoUiMapper.chaosCard(
                            player = it,
                            currentSeat = currentSeat,
                            timerFraction = state.timerFraction,
                            places = state.places,
                            eliminated = state.eliminated,
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
                    onToggleMic = { send(MatchMonoChaosIntent.SetMic(!state.micOn)) },
                    onToggleAutoplay = { send(MatchMonoChaosIntent.SetAutoplay(!state.autoplay)) },
                )
            },
            callButton = { MonoChaosActionButtons(state = state, send = send) },
            hand = { MonoChaosHand(state = state, send = send) },
            drawButton = { MonoChaosDrawButton(state = state, send = send) },
        )

        if (state.awaitingColorChoice) MonoPromptModal(MonoStrings.monoChooseColour) { MonoChaosColourPrompt(send) }
        if (state.awaitingRouletteChoice) MonoPromptModal(MonoStrings.monoChooseRouletteColour) { MonoChaosRoulettePrompt(send) }
        if (state.awaitingSwapChoice) MonoPromptModal(MonoStrings.monoSwapTitle) { MonoChaosSwapPrompt(state = state, send = send) }
        if (state.finished) MonoChaosResultOverlay(state, onClose)
        state.error?.let { MonoChaosErrorOverlay(it) }
    }
}

/** The felt the piles sit on, with the draw and discard piles centred. */
@Composable
private fun MonoChaosArena(state: MatchMonoChaosState) {
    val match = state.match
    val dimens = YapNPlayTheme.dimens
    MonoTableArena(
        hue = state.topColour?.hue(),
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
            MonoChaosCardView(
                card = topCard,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/** The user's hand, fanned. */
@Composable
private fun MonoChaosHand(state: MatchMonoChaosState, send: (MatchMonoChaosIntent) -> Unit) {
    val dimens = YapNPlayTheme.dimens
    MonoHandRow(
        handSize = state.hand.size,
        cardWidth = dimens.monoHandCardWidth,
        modifier = Modifier.fillMaxWidth(),
    ) { index ->
        MonoChaosCardView(
            card = state.hand[index],
            isSelected = state.selectedCardIndex == index,
            isPlayable = index in state.playableIndexes,
            onClick = { send(MatchMonoChaosIntent.TapCard(index)) },
            modifier = Modifier.width(dimens.monoHandCardWidth),
        )
    }
}

/** The MONO! callout and the button that takes the penalty on the table. */
@Composable
private fun MonoChaosActionButtons(state: MatchMonoChaosState, send: (MatchMonoChaosIntent) -> Unit) {
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
            onClick = { send(MatchMonoChaosIntent.CallMono) },
            enabled = state.canCallMono,
            trailingIcon = null,
            gradient = listOf(colors.Error, colors.Warning),
            contentColor = colors.TextPrimary,
            height = dimens.monoCallHeight,
            modifier = Modifier.width(dimens.monoCallWidth),
        )
        if (state.canStack) {
            PillButton(
                text = MonoStrings.monoTakePenalty(state.activeStackPenalty),
                onClick = { send(MatchMonoChaosIntent.AcceptStack) },
                gradient = listOf(colors.Warning, colors.Error),
                contentColor = colors.Background,
                height = dimens.monoCallHeight,
                modifier = Modifier.width(dimens.monoCallWidth),
            )
        }
    }
}

/** The draw button: it draws until something can be played. */
@Composable
private fun MonoChaosDrawButton(state: MatchMonoChaosState, send: (MatchMonoChaosIntent) -> Unit) {
    val colors = YapNPlayTheme.colors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        TableGlassButton(
            text = MonoStrings.monoDrawCard,
            onClick = { send(MatchMonoChaosIntent.DrawUntilPlayable) },
            fill = listOf(colors.Panel, colors.GlassSurface),
            textColor = colors.TextPrimary,
            // Under a stack the only moves are stacking or taking the penalty.
            enabled = state.myTurn && !state.canStack,
        )
        // After drawing, the player may decline the card they drew.
        if (state.canPass) {
            TableGlassButton(
                text = MonoStrings.monoPass,
                onClick = { send(MatchMonoChaosIntent.Pass) },
                fill = listOf(colors.Panel, colors.GlassSurface),
                textColor = colors.TextSecondary,
            )
        }
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

/** Who won, once someone has emptied their hand or been left standing. */
@Composable
private fun MonoChaosResultOverlay(state: MatchMonoChaosState, onClose: () -> Unit) {
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

