package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleStartEffect
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.revealWhen
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.games.elements.dice.ui.GlassDice
import com.kglabs28.yapnplay.appengine.games.elements.dice.ui.rememberGlassDiceState
import com.kglabs28.yapnplay.appengine.ui.components.common.AppPermission
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay
import com.kglabs28.yapnplay.appengine.ui.components.common.rememberPermissionRequester
import com.kglabs28.yapnplay.appengine.ui.components.table.TableSpectatorMicButton
import com.kglabs28.yapnplay.appengine.ui.components.table.TableTopBar
import com.kglabs28.yapnplay.appengine.ui.placeholders.CozyHomeBackground
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.CityBaazarModalActions
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.CityBaazarModalContent
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.actions.CityBaazarActionButton
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.board.CityBaazarBoardView
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.board.cityBaazarDicePalette
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.dialogs.CbMoneyToast
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.players.CityBaazarCardColumn
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.players.CityBaazarMyControls
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.players.CityBaazarOwnedStacks
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.players.CityBaazarPlayerCard
import kotlinx.coroutines.launch

/**
 * City Baazar table (design): top bar (game, spectators in room matches,
 * match clock, settings, exit); the isometric board with two dice in the
 * middle; other players' pods on both sides, the user's pod bottom-left
 * with Mic, Autoplay and Roll Dice; the user's cities stacked by colour
 * at the bottom; My Cities and Trade bottom-right. Balance-sheet dialogs for each decision; short balance
 * sheets for rent, taxes and cards.
 */
@Composable
fun CityBaazarMatchScreen(
    matchId: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CityBaazarMatchViewModel? = null,
) {
    val state = viewModel?.state?.collectAsState()?.value ?: CityBaazarMatchState()
    val send: (CityBaazarMatchIntent) -> Unit = { viewModel?.onIntent(it) }
    val micPermission = rememberPermissionRequester(AppPermission.MICROPHONE)
    val die1 = rememberGlassDiceState()
    val die2 = rememberGlassDiceState()
    LaunchedEffect(viewModel, matchId) {
        viewModel?.onIntent(CityBaazarMatchIntent.Load(matchId))
        viewModel?.effects?.collect { effect ->
            when (effect) {
                CityBaazarMatchEffect.Close -> onClose()
                CityBaazarMatchEffect.RequestMicPermission ->
                    micPermission.request { send(CityBaazarMatchIntent.MicPermissionResult(it)) }
                CityBaazarMatchEffect.OpenAppSettings -> micPermission.openSettings()
            }
        }
    }
    // Started/stopped covers both leaving the screen and the app going to the background.
    LifecycleStartEffect(viewModel) {
        send(CityBaazarMatchIntent.ScreenVisible(true))
        onStopOrDispose { send(CityBaazarMatchIntent.ScreenVisible(false)) }
    }
    LaunchedEffect(state.dice.rollId) {
        if (state.dice.rollId > 0) {
            launch { die1.roll(state.dice.die1) }
            launch { die2.roll(state.dice.die2) }
        }
    }

    CozyHomeBackground(modifier = modifier, frosted = true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.Md, vertical = Spacing.Xs)
                .revealWhen(state.loaded),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            TableTopBar(
                title = state.title,
                subtitle = state.subtitle,
                audience = state.audience,
                showAudience = state.roomMatch,
                spectatorCount = state.spectatorCount,
                spectatorLabel = state.spectatorLabel,
                elapsed = state.elapsed,
                onSettings = { send(CityBaazarMatchIntent.OpenSettings) },
                onExit = { send(CityBaazarMatchIntent.ExitClicked) },
            )
            BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
                val side = maxWidth * AppConstants.CB_SIDE_COLUMN_WEIGHT
                val palette = cityBaazarDicePalette(state.dice.colour)
                CityBaazarBoardView(
                    tiles = state.tiles,
                    tokens = state.tokens,
                    onTileClick = { send(CityBaazarMatchIntent.TileClicked(it)) },
                    highlight = state.highlight,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxHeight()
                        .width(maxWidth - side * 2),
                ) { left, right ->
                    // Tap the dice to roll (no press highlight), when it's the user's roll.
                    val tap = if (state.canRoll) Modifier.clickable(interactionSource = null, indication = null) { send(CityBaazarMatchIntent.Roll) } else Modifier
                    GlassDice(state = die1, palette = palette, modifier = left.then(tap))
                    GlassDice(state = die2, palette = palette, modifier = right.then(tap))
                }
                CityBaazarCardColumn(
                    cards = state.leftCards,
                    onToggleHear = { send(CityBaazarMatchIntent.ToggleHear(it)) },
                    modifier = Modifier.align(Alignment.CenterStart).width(side),
                ) {
                    state.myCard?.let { card ->
                        CityBaazarPlayerCard(card = card, modifier = Modifier.fillMaxWidth()) {
                            CityBaazarMyControls(
                                canRoll = state.canRoll,
                                rollLabel = state.rollLabel,
                                autoplay = state.autoplay,
                                micOn = state.micOn,
                                onRoll = { send(CityBaazarMatchIntent.Roll) },
                                onToggleAutoplay = { send(CityBaazarMatchIntent.ToggleAutoplay) },
                                onToggleMic = { send(CityBaazarMatchIntent.ToggleMic(micPermission.isGranted())) },
                            )
                        }
                    }
                }
                CityBaazarCardColumn(
                    cards = state.rightCards,
                    onToggleHear = { send(CityBaazarMatchIntent.ToggleHear(it)) },
                    modifier = Modifier.align(Alignment.CenterEnd).width(side),
                ) {
                    if (!state.spectating) {
                        CityBaazarActionButton(
                            text = Strings.cbMyCities,
                            accent = YapNPlayTheme.colors.CbGain,
                            enabled = state.stacks.isNotEmpty(),
                            onClick = { send(CityBaazarMatchIntent.OpenMyCities) },
                        )
                        CityBaazarActionButton(
                            text = Strings.cbTrade,
                            accent = YapNPlayTheme.colors.CbCash,
                            onClick = { send(CityBaazarMatchIntent.Trade) },
                        )
                    }
                }
                if (state.spectating) {
                    TableSpectatorMicButton(
                        micOn = state.micOn,
                        onToggle = { send(CityBaazarMatchIntent.ToggleMic(micPermission.isGranted())) },
                        modifier = Modifier.align(Alignment.BottomCenter),
                    )
                }
                // The user's cities, stacked by colour, under the board.
                CityBaazarOwnedStacks(
                    stacks = state.stacks,
                    onOpen = { send(CityBaazarMatchIntent.OpenStack(it)) },
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
                state.toast?.let { CbMoneyToast(toast = it, modifier = Modifier.align(Alignment.TopCenter)) }
            }
        }

        val modal = state.modal
        BlockingOverlay(
            visible = modal != null,
            // Turn decisions, a vote and the final ranking need an answer.
            onDismiss = if (modal == null || modal.needsAnswer) null else ({ send(CityBaazarMatchIntent.DismissModal) }),
        ) {
            if (modal != null) {
                CityBaazarModalContent(
                    modal = modal,
                    actions = CityBaazarModalActions(
                        onDismiss = { send(CityBaazarMatchIntent.DismissModal) },
                        onBuy = { send(CityBaazarMatchIntent.Buy) },
                        onDecline = { send(CityBaazarMatchIntent.Decline) },
                        onConfirmBuild = { send(CityBaazarMatchIntent.ConfirmBuild) },
                        onSkipBuild = { send(CityBaazarMatchIntent.SkipBuild) },
                        onRoll = { send(CityBaazarMatchIntent.Roll) },
                        onPayJailFine = { send(CityBaazarMatchIntent.PayJailFine) },
                        onStayInJail = { send(CityBaazarMatchIntent.StayInJail) },
                        onToggleSale = { send(CityBaazarMatchIntent.ToggleSale(it)) },
                        onConfirmSale = { send(CityBaazarMatchIntent.ConfirmSale) },
                        onStartAuction = { send(CityBaazarMatchIntent.StartAuction) },
                        onBid = { send(CityBaazarMatchIntent.Bid) },
                        onPassAuction = { send(CityBaazarMatchIntent.PassAuction) },
                        onDeclareBankruptcy = { send(CityBaazarMatchIntent.DeclareBankruptcy) },
                        onConfirmLeave = { send(CityBaazarMatchIntent.ConfirmLeave) },
                        onDone = { send(CityBaazarMatchIntent.Done) },
                        onOpenAppSettings = { send(CityBaazarMatchIntent.OpenAppSettings) },
                        onToggleEffects = { send(CityBaazarMatchIntent.ToggleEffects) },
                        onToggleMusic = { send(CityBaazarMatchIntent.ToggleMusic) },
                        onOpenReport = { send(CityBaazarMatchIntent.OpenReport) },
                        onSelectReportPerson = { send(CityBaazarMatchIntent.SelectReportPerson(it)) },
                        onSubmitReport = { send(CityBaazarMatchIntent.SubmitReport) },
                        onVote = { send(CityBaazarMatchIntent.Vote(it)) },
                        onWatchAd = { send(CityBaazarMatchIntent.WatchAd) },
                    ),
                )
            }
        }
    }
}
