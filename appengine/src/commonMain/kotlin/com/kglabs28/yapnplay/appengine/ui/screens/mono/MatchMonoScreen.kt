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
import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants
import com.kglabs28.yapnplay.appengine.games.mono.ui.components.MonoCardView
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
import com.kglabs28.yapnplay.appengine.ui.components.table.monoCardColour
import com.kglabs28.yapnplay.appengine.ui.placeholders.CozyHomeBackground

/**
 * Standard MONO table: the players' cards across the top, the felt with
 * the draw and discard piles in the middle, the user's hand and actions
 * along the bottom. This file only assembles components — every rule
 * lives in the engine.
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.Md, vertical = Spacing.Xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            TableTopBar(
                title = Strings.monoTitle,
                subtitle = state.title,
                audience = state.audience,
                showAudience = state.audience.isNotEmpty(),
                spectatorCount = state.spectatorCount.toString(),
                spectatorLabel = Strings.ludoSpectators,
                elapsed = state.elapsed,
                onSettings = { send(MatchMonoIntent.OpenSettings) },
                onExit = { send(MatchMonoIntent.ExitClicked) },
            )
            // Every seat's card, in turn order from the user's own.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
            ) {
                state.opponents.forEach { opponent ->
                    MonoPlayerCard(
                        card = MonoUiMapper.card(
                            player = opponent,
                            currentSeat = state.match?.currentSeat ?: 0,
                            timerFraction = state.timerFraction,
                            places = state.places,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            MonoCentre(state)
            MonoHandSection(state = state, send = send)
            if (state.awaitingColorChoice) MonoColourPrompt(send)
            if (state.finished) MonoResultOverlay(state, onClose)
            state.error?.let { MonoErrorOverlay(it) }
        }
    }
}

/** The felt the piles sit on, with the draw and discard piles centred. */
@Composable
private fun ColumnScope.MonoCentre(state: MatchMonoState) {
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
                        MonoCardView(
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
private fun MonoHandSection(state: MatchMonoState, send: (MatchMonoIntent) -> Unit) {
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
                MonoCardView(
                    card = state.hand[index],
                    isSelected = state.selectedCardIndex == index,
                    // A card the table would refuse is drawn pushed back.
                    isPlayable = index in state.playableIndexes,
                    onClick = { send(MatchMonoIntent.TapCard(index)) },
                    modifier = Modifier.width(YapNPlayTheme.dimens.monoHandCardWidth),
                )
            },
        )
        MonoActions(state = state, send = send)
    }
}

/** DRAW, TAKE (when a penalty is on the table) and the MONO! callout. */
@Composable
private fun MonoActions(state: MatchMonoState, send: (MatchMonoIntent) -> Unit) {
    val colors = YapNPlayTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        TableGlassButton(
            text = Strings.monoDraw,
            onClick = { send(MatchMonoIntent.DrawCard) },
            fill = listOf(colors.Raised, colors.Panel),
            textColor = colors.TextPrimary,
            enabled = state.myTurn,
            modifier = Modifier.weight(1f),
        )
        if (state.activeStackPenalty > 0) {
            TableGlassButton(
                text = Strings.monoTakePenalty,
                onClick = { send(MatchMonoIntent.AcceptStack) },
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
            onClick = { send(MatchMonoIntent.CallMono) },
            modifier = Modifier.fillMaxWidth(),
            containerColor = colors.Error,
            contentColor = colors.TextPrimary,
        )
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
        BasicText(
            text = Strings.monoChooseColour,
            style = YapNPlayTheme.typography.caption.copy(
                color = colors.TextSecondary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
        )
        MonoColourPicker(
            options = MonoConstants.Color.entries.map { it.name to monoCardColour(it) },
            onPick = { index -> send(MatchMonoIntent.ChooseColor(MonoConstants.Color.entries[index])) },
        )
    }
}

/** Who won, once someone has emptied their hand. */
@Composable
private fun MonoResultOverlay(state: MatchMonoState, onClose: () -> Unit) {
    val winner = state.winnerSeat
    val won = winner != null && winner == state.match?.me?.seat
    BlockingOverlay(visible = true) {
        ModalCard(
            title = if (won) Strings.monoYouWin() else Strings.monoWinner(state.match?.player(winner ?: -1)?.name.orEmpty()),
            subtitle = Strings.monoLeaveWhenDone,
            actions = {
                PrimaryButton(text = Strings.cbClose, onClick = onClose)
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

private const val EMPTY_CARD = "–"