package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import com.kglabs28.yapnplay.appengine.ui.components.table.TableGlassButton
import com.kglabs28.yapnplay.appengine.ui.components.table.TableSpectatorMicButton
import com.kglabs28.yapnplay.appengine.ui.components.table.TableTopBar
import com.kglabs28.yapnplay.appengine.ui.placeholders.CozyHomeBackground
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.CityBazaarModalActions
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.CityBazaarModalContent
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.CityBazaarBoardView
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.cityBazaarDicePalette
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs.CbMoneyToast
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs.CityBazaarSidePanel
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.players.CityBazaarCardColumn
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.players.CityBazaarMyControls
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.players.CityBazaarOwnedStacks
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.players.CityBazaarPlayerCard
import kotlinx.coroutines.launch

/**
 * City Bazaar table (design): top bar (game, spectators in room matches,
 * match clock, settings, exit); the isometric board with two dice in the
 * middle (2D / 3D switch in the top bar); every other player's pod on the
 * left, the user's pod top-right with Mic, Autoplay and Roll Dice, their
 * cities stacked by colour under it, then My Cities and Trade. Tapping a
 * player or a tile opens its info over the left column.
 * Tapping the Luck or Shagun Chest pile shows its 2–12 table. Balance-sheet dialogs for each decision; short balance
 * sheets for rent, taxes and cards.
 */
@Composable
fun CityBazaarMatchScreen(
    matchId: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CityBazaarMatchViewModel? = null,
) {
    val state = viewModel?.state?.collectAsState()?.value ?: CityBazaarMatchState()
    val send: (CityBazaarMatchIntent) -> Unit = { viewModel?.onIntent(it) }
    val micPermission = rememberPermissionRequester(AppPermission.MICROPHONE)
    val die1 = rememberGlassDiceState()
    val die2 = rememberGlassDiceState()
    LaunchedEffect(viewModel, matchId) {
        viewModel?.onIntent(CityBazaarMatchIntent.Load(matchId))
        viewModel?.effects?.collect { effect ->
            when (effect) {
                CityBazaarMatchEffect.Close -> onClose()
                CityBazaarMatchEffect.RequestMicPermission ->
                    micPermission.request { send(CityBazaarMatchIntent.MicPermissionResult(it)) }
                CityBazaarMatchEffect.OpenAppSettings -> micPermission.openSettings()
            }
        }
    }
    // Started/stopped covers both leaving the screen and the app going to the background.
    LifecycleStartEffect(viewModel) {
        send(CityBazaarMatchIntent.ScreenVisible(true))
        onStopOrDispose { send(CityBazaarMatchIntent.ScreenVisible(false)) }
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
                onSettings = { send(CityBazaarMatchIntent.OpenSettings) },
                onExit = { send(CityBazaarMatchIntent.ExitClicked) },
                // The view to switch to.
                switchLabel = if (state.threeD) Strings.cbView2d else Strings.cbView3d,
                onSwitch = { send(CityBazaarMatchIntent.ToggleView) },
            )
            BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
                val side = maxWidth * AppConstants.CB_SIDE_COLUMN_WEIGHT
                val palette = cityBazaarDicePalette(state.dice.colour)
                val colors = YapNPlayTheme.colors
                val smoke = listOf(colors.LudoSmokeTop, colors.LudoSmokeBottom)
                // Side buttons match the pod's Mic / Autoplay / Roll rows.
                val buttonSize = Modifier.padding(horizontal = Spacing.Xs).height(YapNPlayTheme.dimens.cbControlHeight)
                CityBazaarBoardView(
                    tiles = state.tiles,
                    tokens = state.tokens,
                    onTileClick = { send(CityBazaarMatchIntent.TileClicked(it)) },
                    onDeckClick = { send(CityBazaarMatchIntent.OpenDeck(it)) },
                    highlight = state.highlight,
                    threeD = state.threeD,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxHeight()
                        .width(maxWidth - side * 2),
                ) { left, right ->
                    // Tap the dice to roll (no press highlight), when it's the user's roll.
                    val tap = if (state.canRoll) Modifier.clickable(interactionSource = null, indication = null) { send(CityBazaarMatchIntent.Roll) } else Modifier
                    GlassDice(state = die1, palette = palette, modifier = left.then(tap))
                    GlassDice(state = die2, palette = palette, modifier = right.then(tap))
                }
                // Every other player on the left.
                CityBazaarCardColumn(
                    cards = state.leftCards,
                    onToggleHear = { send(CityBazaarMatchIntent.ToggleHear(it)) },
                    onCardClick = { send(CityBazaarMatchIntent.PlayerClicked(it)) },
                    modifier = Modifier.align(Alignment.CenterStart).width(side),
                )
                // The user on the right: their pod, their cities by colour, then My Cities and Trade.
                Column(
                    modifier = Modifier.align(Alignment.CenterEnd).width(side).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
                ) {
                    state.rightCards.forEach { card ->
                        CityBazaarPlayerCard(
                            card = card,
                            onToggleHear = { send(CityBazaarMatchIntent.ToggleHear(card.seat)) },
                            onClick = { send(CityBazaarMatchIntent.PlayerClicked(card.seat)) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    state.myCard?.let { card ->
                        CityBazaarPlayerCard(
                            card = card,
                            onClick = { send(CityBazaarMatchIntent.PlayerClicked(card.seat)) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            CityBazaarMyControls(
                                canRoll = state.canRoll,
                                rollLabel = state.rollLabel,
                                autoplay = state.autoplay,
                                micOn = state.micOn,
                                onRoll = { send(CityBazaarMatchIntent.Roll) },
                                onToggleAutoplay = { send(CityBazaarMatchIntent.ToggleAutoplay) },
                                onToggleMic = { send(CityBazaarMatchIntent.ToggleMic(micPermission.isGranted())) },
                            )
                        }
                    }
                    CityBazaarOwnedStacks(
                        stacks = state.stacks,
                        onOpen = { send(CityBazaarMatchIntent.OpenStack(it)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.weight(1f))
                    if (!state.spectating) {
                        TableGlassButton(
                            text = Strings.cbMyCities,
                            onClick = { send(CityBazaarMatchIntent.OpenMyCities) },
                            fill = smoke,
                            textColor = colors.TextPrimary,
                            enabled = state.stacks.isNotEmpty(),
                            modifier = buttonSize,
                        )
                        TableGlassButton(
                            text = Strings.cbTrade,
                            onClick = { send(CityBazaarMatchIntent.Trade) },
                            fill = smoke,
                            textColor = colors.TextPrimary,
                            modifier = buttonSize,
                        )
                    }
                }
                if (state.spectating) {
                    TableSpectatorMicButton(
                        micOn = state.micOn,
                        onToggle = { send(CityBazaarMatchIntent.ToggleMic(micPermission.isGranted())) },
                        modifier = Modifier.align(Alignment.BottomCenter),
                    )
                }
                state.panel?.let { panel ->
                    // Any touch outside the panel closes it.
                    Box(Modifier.fillMaxSize().clickable(interactionSource = null, indication = null) { send(CityBazaarMatchIntent.DismissPanel) })
                    CityBazaarSidePanel(
                        panel = panel,
                        onPropertyClick = { send(CityBazaarMatchIntent.PanelPropertyClicked(it)) },
                        modifier = Modifier.align(Alignment.TopStart).heightIn(max = maxHeight),
                    )
                }
                state.toast?.let { CbMoneyToast(toast = it, modifier = Modifier.align(Alignment.TopCenter)) }
            }
        }

        val modal = state.modal
        BlockingOverlay(
            visible = modal != null,
            // Turn decisions, an auction, a vote and the final ranking need an answer.
            onDismiss = if (modal == null || modal.needsAnswer) null else ({ send(CityBazaarMatchIntent.DismissModal) }),
        ) {
            if (modal != null) {
                CityBazaarModalContent(
                    modal = modal,
                    actions = CityBazaarModalActions(
                        onDismiss = { send(CityBazaarMatchIntent.DismissModal) },
                        onBuy = { send(CityBazaarMatchIntent.Buy) },
                        onDecline = { send(CityBazaarMatchIntent.Decline) },
                        onConfirmBuild = { send(CityBazaarMatchIntent.ConfirmBuild) },
                        onSkipBuild = { send(CityBazaarMatchIntent.SkipBuild) },
                        onRoll = { send(CityBazaarMatchIntent.Roll) },
                        onPayJailFine = { send(CityBazaarMatchIntent.PayJailFine) },
                        onStayInJail = { send(CityBazaarMatchIntent.StayInJail) },
                        onToggleSale = { send(CityBazaarMatchIntent.ToggleSale(it)) },
                        onConfirmSale = { send(CityBazaarMatchIntent.ConfirmSale) },
                        onStartAuction = { send(CityBazaarMatchIntent.StartAuction) },
                        onBid = { send(CityBazaarMatchIntent.Bid) },
                        onPassAuction = { send(CityBazaarMatchIntent.PassAuction) },
                        onDeclareBankruptcy = { send(CityBazaarMatchIntent.DeclareBankruptcy) },
                        onConfirmLeave = { send(CityBazaarMatchIntent.ConfirmLeave) },
                        onDone = { send(CityBazaarMatchIntent.Done) },
                        onOpenAppSettings = { send(CityBazaarMatchIntent.OpenAppSettings) },
                        onToggleEffects = { send(CityBazaarMatchIntent.ToggleEffects) },
                        onToggleMusic = { send(CityBazaarMatchIntent.ToggleMusic) },
                        onOpenReport = { send(CityBazaarMatchIntent.OpenReport) },
                        onSelectReportPerson = { send(CityBazaarMatchIntent.SelectReportPerson(it)) },
                        onSubmitReport = { send(CityBazaarMatchIntent.SubmitReport) },
                        onVote = { send(CityBazaarMatchIntent.Vote(it)) },
                        onWatchAd = { send(CityBazaarMatchIntent.WatchAd) },
                    ),
                )
            }
        }
    }
}
