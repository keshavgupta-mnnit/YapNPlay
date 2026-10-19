package com.kglabs28.yapnplay.appengine.ui.screens.mono

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.LifecycleStartEffect
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants
import com.kglabs28.yapnplay.appengine.games.monochaos.ui.components.MonoChaosCardView
import com.kglabs28.yapnplay.appengine.ui.components.PrimaryButton
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoColourPicker
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoHandRow
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoTableCentre
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoTableGlow
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoTableSurface
import com.kglabs28.yapnplay.appengine.ui.components.table.TableGlassButton
import com.kglabs28.yapnplay.appengine.ui.components.table.TableTopBar
import com.kglabs28.yapnplay.appengine.ui.components.table.monoChaosCardColour
import com.kglabs28.yapnplay.appengine.ui.placeholders.CozyHomeBackground

/**
 * MONO: Complete Chaos table: the same table as Standard MONO — the
 * players' cards across the top, the felt with the piles in the middle,
 * the hand and actions below — with stacking and the MONO! callout.
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.Md, vertical = Spacing.Xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            TableTopBar(
                title = Strings.monoChaosTitle,
                subtitle = state.title,
                audience = state.audience,
                showAudience = state.audience.isNotEmpty(),
                spectatorCount = state.spectatorCount.toString(),
                spectatorLabel = Strings.ludoSpectators,
                elapsed = state.elapsed,
                onSettings = { send(MatchMonoChaosIntent.OpenSettings) },
                onExit = { send(MatchMonoChaosIntent.ExitClicked) },
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
            ) {
                state.opponents.forEach { opponent ->
                    MonoPlayerCard(
                        card = MonoUiMapper.chaosCard(
                            player = opponent,
                            currentSeat = state.match?.currentSeat ?: 0,
                            timerFraction = state.timerFraction,
                            places = state.places,
                            eliminated = state.eliminated,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            MonoChaosCentre(state)
            MonoChaosHandSection(state = state, send = send)
            if (state.awaitingColorChoice) MonoChaosColourPrompt(send)
            if (state.finished) MonoChaosResultOverlay(state, onClose)
            state.error?.let { MonoChaosErrorOverlay(it) }
        }
    }
}

/** The felt the piles sit on, with the draw and discard piles centred. */
@Composable
private fun ColumnScope.MonoChaosCentre(state: MatchMonoChaosState) {
    val match = state.match
    MonoTableSurface(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(vertical = Spacing.Xs),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            MonoTableGlow(modifier = Modifier.fillMaxSize())
            MonoTableCentre(
                drawPileCount = match?.drawPileCount ?: 0,
                discardPileCount = match?.discardPileCount ?: 0,
                activeStackPenalty = state.activeStackPenalty,
                faceCard = {
                    val topCard = match?.topCard
                    if (topCard == null) {
                        BasicText(
                            text = EMPTY_CARD,
                            style = YapNPlayTheme.typography.title.copy(color = YapNPlayTheme.colors.TextDisabled),
                        )
                    } else {
                        MonoChaosCardView(
                            card = topCard,
                            shownColour = match.topCardColor,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                },
            )
        }
    }
}

/** The user's hand, with the actions the table currently allows. */
@Composable
private fun MonoChaosHandSection(state: MatchMonoChaosState, send: (MatchMonoChaosIntent) -> Unit) {
    val colors = YapNPlayTheme.colors
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        BasicText(
            text = Strings.monoHandCount(state.hand.size),
            style = YapNPlayTheme.typography.caption.copy(
                color = colors.TextSecondary,
                textAlign = TextAlign.Center,
            ),
        )
        MonoHandRow(
            handSize = state.hand.size,
            modifier = Modifier.fillMaxWidth(),
            card = { index ->
                MonoChaosCardView(
                    card = state.hand[index],
                    isSelected = state.selectedCardIndex == index,
                    // A card the table would refuse is drawn pushed back.
                    isPlayable = index in state.playableIndexes,
                    onClick = { send(MatchMonoChaosIntent.TapCard(index)) },
                    modifier = Modifier.width(YapNPlayTheme.dimens.monoHandCardWidth),
                )
            },
        )
        MonoChaosActions(state = state, send = send)
    }
}

/** DRAW, TAKE (the stack) and the MONO! callout. */
@Composable
private fun MonoChaosActions(state: MatchMonoChaosState, send: (MatchMonoChaosIntent) -> Unit) {
    val colors = YapNPlayTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        TableGlassButton(
            text = Strings.monoDraw,
            onClick = { send(MatchMonoChaosIntent.DrawUntilPlayable) },
            fill = listOf(colors.Raised, colors.Panel),
            textColor = colors.TextPrimary,
            enabled = state.myTurn,
            modifier = Modifier.weight(1f),
        )
        if (state.activeStackPenalty > 0) {
            TableGlassButton(
                text = Strings.monoTakePenalty,
                onClick = { send(MatchMonoChaosIntent.AcceptStack) },
                fill = listOf(colors.Warning, colors.Error),
                textColor = colors.Background,
                enabled = state.myTurn,
                modifier = Modifier.weight(1f),
            )
        }
    }
    if (state.canCallMono) {
        PrimaryButton(
            text = Strings.monoCallMono,
            onClick = { send(MatchMonoChaosIntent.CallMono) },
            modifier = Modifier.fillMaxWidth(),
            containerColor = colors.Error,
            contentColor = colors.TextPrimary,
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
        BasicText(
            text = Strings.monoChooseColour,
            style = YapNPlayTheme.typography.caption.copy(
                color = colors.TextSecondary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
        )
        MonoColourPicker(
            options = MonoChaosConstants.Color.entries.map { it.name to monoChaosCardColour(it) },
            onPick = { index -> send(MatchMonoChaosIntent.ChooseColor(MonoChaosConstants.Color.entries[index])) },
        )
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
                Strings.monoYouWin()
            } else {
                Strings.monoWinner(state.match?.player(winner ?: -1)?.name.orEmpty())
            },
            subtitle = Strings.monoLeaveWhenDone,
            actions = {
                PrimaryButton(text = Strings.cbClose, onClick = onClose)
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

private const val EMPTY_CARD = "–"