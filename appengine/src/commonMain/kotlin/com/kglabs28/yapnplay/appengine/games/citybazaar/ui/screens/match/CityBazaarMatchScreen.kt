package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match

import com.kglabs28.yapnplay.appengine.ui.placeholders.CityBazaarArtwork
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarColour
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.CbMoneyFlight
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.geometry.Offset
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.players.CbBankButton
import com.kglabs28.yapnplay.appengine.ui.components.common.rememberShareSheet
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.MatchLeaderboard
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs.CbTransactionNotice
import com.kglabs28.yapnplay.appengine.ui.components.table.TableHistoryDialog
import com.kglabs28.yapnplay.appengine.ui.components.table.TableFeedSnackbar
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.players.CbCashLine
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.tint
import com.kglabs28.yapnplay.appengine.ui.components.table.TableCardDensity
import com.kglabs28.yapnplay.appengine.ui.components.table.TablePlayerCard
import com.kglabs28.yapnplay.appengine.ui.components.table.PlayerFriendDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.compose.LifecycleStartEffect
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.revealWhen
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.games.elements.dice.ui.GlassDice
import com.kglabs28.yapnplay.appengine.games.elements.dice.ui.rememberGlassDiceState
import com.kglabs28.yapnplay.appengine.ui.components.common.AppPermission
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockBackPress
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
import kotlinx.coroutines.launch

/**
 * City Bazaar table (design): top bar (game, spectators in room matches,
 * match clock, 2D / 3D switch, settings, exit); the board with two dice in
 * the middle; every other player's pod on the left (sized to fit however
 * many there are); the user's pod top-right with Mic, Autoplay and Roll
 * Dice, then My Properties and their properties stacked by colour.
 * Tapping a player, a tile, a pile or a stack opens its info in the left
 * column. Balance-sheet dialogs for each decision; short balance sheets
 * for rent, taxes and cards.
 */
@Composable
fun CityBazaarMatchScreen(
    matchId: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CityBazaarMatchViewModel? = null,
) {
    val state = viewModel?.state?.collectAsState()?.value ?: CityBazaarMatchState()
    // Where each card (and the bank) is, for the coin animation when money popups are off.
    val anchors = remember { mutableStateMapOf<Int, Offset>() }
    // Player colours for the coloured names in the snackbar and history.
    val playerTints = CityBazaarColour.entries.map { it.tint() }
    var boardOrigin by remember { mutableStateOf(Offset.Zero) }
    val send: (CityBazaarMatchIntent) -> Unit = { viewModel?.onIntent(it) }
    val micPermission = rememberPermissionRequester(AppPermission.MICROPHONE)
    val die1 = rememberGlassDiceState()
    val share = rememberShareSheet()
    val die2 = rememberGlassDiceState()

    // Back at the table is the same as the exit button (asks first).
    BlockBackPress(enabled = true, onBack = { send(CityBazaarMatchIntent.ExitClicked) })
    LaunchedEffect(viewModel, matchId) {
        viewModel?.onIntent(CityBazaarMatchIntent.Load(matchId))
        viewModel?.effects?.collect { effect ->
            when (effect) {
                CityBazaarMatchEffect.Close -> onClose()
                CityBazaarMatchEffect.RequestMicPermission ->
                    micPermission.request { send(CityBazaarMatchIntent.MicPermissionResult(it)) }
                CityBazaarMatchEffect.OpenAppSettings -> micPermission.openSettings()
                is CityBazaarMatchEffect.ShareText -> share(effect.text)
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
                onHistory = { send(CityBazaarMatchIntent.OpenHistory) },
                // As wide as the player cards below.
                titleWidthFraction = AppConstants.CB_SIDE_COLUMN_WEIGHT,
            )
            val panelOpen = state.panel != null
            BoxWithConstraints(
                modifier = Modifier
                    .onGloballyPositioned { boardOrigin = it.positionInRoot() }
                    .weight(1f)
                    .fillMaxWidth()
                    // A touch outside the info panel closes it, and still does what it touched (Trade, a tile…).
                    .pointerInput(panelOpen) {
                        if (!panelOpen) return@pointerInput
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                            if (down.position.x > size.width * AppConstants.CB_SIDE_COLUMN_WEIGHT) send(CityBazaarMatchIntent.DismissPanel)
                        }
                    },
            ) {
                val side = maxWidth * AppConstants.CB_SIDE_COLUMN_WEIGHT
                val palette = cityBazaarDicePalette(state.dice.colour)
                val colors = YapNPlayTheme.colors
                val smoke = listOf(colors.LudoSmokeTop, colors.LudoSmokeBottom)
                // Side buttons as wide as the user's pod, as tall as its Mic / Autoplay / Roll rows.
                val buttonSize = Modifier.fillMaxWidth().height(YapNPlayTheme.dimens.cbControlHeight)
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
                    GlassDice(state = die1, palette = palette, grounded = false, modifier = left.then(tap))
                    GlassDice(state = die2, palette = palette, grounded = false, modifier = right.then(tap))
                }
                // Every other player on the left (hidden while the info panel takes their place).
                if (state.panel == null) {
                    CityBazaarCardColumn(
                        cards = state.leftCards,
                        onToggleHear = { send(CityBazaarMatchIntent.ToggleHear(it)) },
                        onCardClick = { send(CityBazaarMatchIntent.PlayerClicked(it)) },
                        onCardLongPress = { send(CityBazaarMatchIntent.PlayerLongPressed(it)) },
                        cardModifier = { seat -> Modifier.onGloballyPositioned { anchors[seat] = it.boundsInRoot().center } },
                        modifier = Modifier.align(Alignment.CenterStart).width(side),
                    )
                }
                // The user on the right: their pod, My Properties, then their properties by colour.
                Column(
                    modifier = Modifier.align(Alignment.CenterEnd).width(side).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
                ) {
                    state.rightCards.forEach { card ->
                        TablePlayerCard(
                            card = card,
                            tint = card.colour.tint(),
                            faded = card.out,
                            badge = card.status,
                            avatarSize = YapNPlayTheme.dimens.cbCardAvatar,
                            density = TableCardDensity.DENSE,
                            details = { CbCashLine(cash = card.cash, compact = false) },
                            onToggleHear = { send(CityBazaarMatchIntent.ToggleHear(card.seat)) },
                            onClick = { send(CityBazaarMatchIntent.PlayerClicked(card.seat)) },
                            onLongPress = { send(CityBazaarMatchIntent.PlayerLongPressed(card.seat)) },
                            modifier = Modifier.fillMaxWidth().onGloballyPositioned { anchors[card.seat] = it.boundsInRoot().center },
                        )
                    }
                    // The bank: above the user's own card: its money and every property nobody owns.
                    CbBankButton(onClick = { send(CityBazaarMatchIntent.OpenBank) }, modifier = buttonSize.onGloballyPositioned { anchors[BANK_ANCHOR] = it.boundsInRoot().center })
                    state.myCard?.let { card ->
                        TablePlayerCard(
                            card = card,
                            tint = card.colour.tint(),
                            faded = card.out,
                            badge = card.status,
                            avatarSize = YapNPlayTheme.dimens.cbCardAvatar,
                            density = TableCardDensity.DENSE,
                            details = { CbCashLine(cash = card.cash, compact = false) },
                            onClick = { send(CityBazaarMatchIntent.PlayerClicked(card.seat)) },
                            modifier = Modifier.fillMaxWidth().onGloballyPositioned { anchors[card.seat] = it.boundsInRoot().center },
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
                    if (!state.spectating) {
                        TableGlassButton(
                            text = Strings.cbMyCities,
                            onClick = { send(CityBazaarMatchIntent.OpenMyCities) },
                            fill = smoke,
                            textColor = colors.TextPrimary,
                            enabled = state.stacks.isNotEmpty(),
                            modifier = buttonSize,
                        )
                    }
                    CityBazaarOwnedStacks(
                        stacks = state.stacks,
                        onOpen = { send(CityBazaarMatchIntent.OpenStack(it)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                if (state.spectating) {
                    TableSpectatorMicButton(
                        micOn = state.micOn,
                        onToggle = { send(CityBazaarMatchIntent.ToggleMic(micPermission.isGranted())) },
                        modifier = Modifier.align(Alignment.BottomCenter),
                    )
                }
                state.panel?.let { panel ->
                    CityBazaarSidePanel(
                        panel = panel,
                        onPropertyClick = { send(CityBazaarMatchIntent.PanelPropertyClicked(it)) },
                        modifier = Modifier.align(Alignment.CenterStart).width(side).fillMaxHeight(),
                    )
                }
                state.toast?.let { CbMoneyToast(toast = it, modifier = Modifier.align(Alignment.TopCenter)) }
                state.transaction?.let { CbTransactionNotice(tx = it, modifier = Modifier.align(Alignment.Center)) }
                state.moneyFlight?.let { flight ->
                    val from = anchors[flight.fromSeat ?: BANK_ANCHOR]
                    val to = anchors[flight.toSeat ?: BANK_ANCHOR]
                    if (from != null && to != null) CbMoneyFlight(flight = flight, from = from - boardOrigin, to = to - boardOrigin)
                }
                state.feedSnack?.let { TableFeedSnackbar(line = it, playerTints = playerTints, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = Spacing.Sm)) }
            }
        }

        val board = state.leaderboard
        BlockingOverlay(visible = board != null) {
            if (board != null) {
                MatchLeaderboard(
                    board = board,
                    gameArt = { CityBazaarArtwork(modifier = it) },
                    onPlayAgain = { send(CityBazaarMatchIntent.Done) },
                )
            }
        }

        BlockingOverlay(visible = state.historyOpen, onDismiss = { send(CityBazaarMatchIntent.DismissHistory) }) {
            if (state.historyOpen) {
                TableHistoryDialog(title = Strings.tableHistoryTitle, lines = state.history, playerTints = playerTints, onClose = { send(CityBazaarMatchIntent.DismissHistory) })
            }
        }

        val friendAction = state.friendAction
        BlockingOverlay(visible = friendAction != null, onDismiss = { send(CityBazaarMatchIntent.DismissFriendAction) }) {
            if (friendAction != null) {
                PlayerFriendDialog(
                    action = friendAction,
                    onConfirm = { send(CityBazaarMatchIntent.ConfirmFriendAction) },
                    onCancel = { send(CityBazaarMatchIntent.DismissFriendAction) },
                )
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
                        onVoteExtension = { send(CityBazaarMatchIntent.VoteExtension(it)) },
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
                        onToggleMoneyPopups = { send(CityBazaarMatchIntent.ToggleMoneyPopups) },
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

/** The bank's key among the card positions. */
private const val BANK_ANCHOR = -1
