package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match

import com.kglabs28.yapnplay.appengine.ui.components.table.tableCardMuteRoom
import com.kglabs28.yapnplay.appengine.core.designsystem.AdaptiveScale
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.compose.LifecycleStartEffect
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.revealWhen
import com.kglabs28.yapnplay.appengine.core.ui.CityBazaarStrings
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarColour
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.CityBazaarModalActions
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.CityBazaarModalContent
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.CbMoneyFlight
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.CityBazaarBoardView
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.CityBazaarIsoProjection
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.cityBazaarDicePalette
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.tint
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs.CbMoneyToast
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs.CbTransactionNotice
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.dialogs.CityBazaarSidePanel
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.players.CbBankButton
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.players.CbCashLine
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.players.CityBazaarCardColumn
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.players.CityBazaarOwnedStacks
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.players.CityRoyaleSeatRing
import com.kglabs28.yapnplay.appengine.games.elements.dice.ui.GlassDice
import com.kglabs28.yapnplay.appengine.games.elements.dice.ui.GlassDiceState
import com.kglabs28.yapnplay.appengine.games.elements.dice.ui.rememberGlassDiceState
import com.kglabs28.yapnplay.appengine.ui.components.common.AppPermission
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockBackPress
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay
import com.kglabs28.yapnplay.appengine.ui.components.common.rememberPermissionRequester
import com.kglabs28.yapnplay.appengine.ui.components.common.rememberShareSheet
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.MatchLeaderboard
import com.kglabs28.yapnplay.appengine.ui.components.table.ArenaDiceButton
import com.kglabs28.yapnplay.appengine.ui.components.table.ArenaSeatSwitches
import com.kglabs28.yapnplay.appengine.ui.components.table.ArenaTopBar
import com.kglabs28.yapnplay.appengine.ui.components.table.PlayerFriendDialog
import com.kglabs28.yapnplay.appengine.ui.components.table.TableCardDensity
import com.kglabs28.yapnplay.appengine.ui.components.table.TableFeedSnackbar
import com.kglabs28.yapnplay.appengine.ui.components.table.TableGlassButton
import com.kglabs28.yapnplay.appengine.ui.components.table.TableHistoryDialog
import com.kglabs28.yapnplay.appengine.ui.components.table.TablePlayerCard
import com.kglabs28.yapnplay.appengine.ui.components.table.TableSeatPlan
import com.kglabs28.yapnplay.appengine.ui.components.table.TableSpectatorVoiceButtons
import com.kglabs28.yapnplay.appengine.ui.components.table.monoScaled
import com.kglabs28.yapnplay.appengine.ui.placeholders.CityBazaarArtwork
import com.kglabs28.yapnplay.appengine.ui.placeholders.GameArenaBackground
import kotlinx.coroutines.launch

/**
 * City Royale table (arena design): top bar (game, spectators in room matches,
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
    // Player colours for the coloured names in the history.
    val playerTints = CityBazaarColour.entries.map { it.tint() }
    val send: (CityBazaarMatchIntent) -> Unit = { viewModel?.onIntent(it) }
    val micPermission = rememberPermissionRequester(AppPermission.MICROPHONE)
    val die1 = rememberGlassDiceState()
    val share = rememberShareSheet()
    val die2 = rememberGlassDiceState()

    // Back at the table is the same as the exit button (asks first); once the leaderboard is up the match is over, so back just leaves.
    BlockBackPress(enabled = true, onBack = { send(if (state.leaderboard != null) CityBazaarMatchIntent.Done else CityBazaarMatchIntent.ExitClicked) })
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

    GameArenaBackground(modifier = modifier) {
        CityBazaarMatchContent(state = state, send = send, die1 = die1, die2 = die2, micGranted = { micPermission.isGranted() })

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
                TableHistoryDialog(title = TableStrings.tableHistoryTitle, lines = state.history, playerTints = playerTints, onClose = { send(CityBazaarMatchIntent.DismissHistory) })
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
                        onVoteEnd = { send(CityBazaarMatchIntent.VoteEnd(it)) },
                        onProposeEnd = { send(CityBazaarMatchIntent.ProposeEnd) },
                        onConfirmEnd = { send(CityBazaarMatchIntent.ConfirmEnd) },
                        onStayInJail = { send(CityBazaarMatchIntent.StayInJail) },
                        onToggleSale = { send(CityBazaarMatchIntent.ToggleSale(it)) },
                        onConfirmSale = { send(CityBazaarMatchIntent.ConfirmSale) },
                        onStartAuction = { send(CityBazaarMatchIntent.StartAuction) },
                        onBid = { send(CityBazaarMatchIntent.Bid) },
                        onPassAuction = { send(CityBazaarMatchIntent.PassAuction) },
                        onDeclareBankruptcy = { send(CityBazaarMatchIntent.DeclareBankruptcy) },
                        onConfirmLeave = { send(CityBazaarMatchIntent.ConfirmLeave) },
                        onPickSeatTaker = { send(CityBazaarMatchIntent.PickSeatTaker(it)) },
                        onSeat = { send(CityBazaarMatchIntent.Seat(it)) },
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
                    // In 3D, raised clear of the bottom bar.
                    modifier = if (state.threeD) Modifier.offset(y = -YapNPlayTheme.dimens.cb3dDialogLift) else Modifier,
                )
            }
        }
    }
}

/**
 * The table itself: the top bar, the board, every player's card, the
 * user's controls, the info panel and the passing notes (everything but
 * the dialogs).
 */
@Composable
fun CityBazaarMatchContent(
    state: CityBazaarMatchState,
    send: (CityBazaarMatchIntent) -> Unit,
    die1: GlassDiceState,
    die2: GlassDiceState,
    micGranted: () -> Boolean,
    modifier: Modifier = Modifier,
) {
    // Where each card (and the bank) is, for the coin animation when money popups are off.
    val anchors = remember { mutableStateMapOf<Int, Offset>() }
    // Player colours for the coloured names in the snackbar and history.
    val playerTints = CityBazaarColour.entries.map { it.tint() }
    var boardOrigin by remember { mutableStateOf(Offset.Zero) }
    // The info panel's right edge (px), so a touch past it closes the panel.
    var panelEdge by remember { mutableStateOf(0f) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.Md, vertical = Spacing.Xs)
            .revealWhen(state.loaded),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        ArenaTopBar(
            title = state.title,
            subtitle = state.subtitle,
            audience = state.audience,
            showAudience = state.roomMatch,
            spectatorCount = state.spectatorCount,
            elapsed = state.elapsed,
            onSettings = { send(CityBazaarMatchIntent.OpenSettings) },
            onExit = { send(CityBazaarMatchIntent.ExitClicked) },
            logo = { CityBazaarArtwork(modifier = it) },
            // The view to switch to.
            switchLabel = if (state.threeD) CityBazaarStrings.cbView2d else CityBazaarStrings.cbView3d,
            onSwitch = { send(CityBazaarMatchIntent.ToggleView) },
            onHistory = { send(CityBazaarMatchIntent.OpenHistory) },
            onToggleHearSpectator = { send(CityBazaarMatchIntent.ToggleHearSpectator(it)) },
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
                        if (down.position.x > panelEdge) send(CityBazaarMatchIntent.DismissPanel)
                    }
                },
        ) {
            val side = maxWidth * AppConstants.CB_SIDE_COLUMN_WEIGHT
            val density = LocalDensity.current
            // The 3D diamond's corners where the board draws it (it sits a little below the top).
            val drop = with(density) { YapNPlayTheme.dimens.cb3dBoardDrop.toPx() }
            // On a tablet the 3D board is drawn smaller (in its centre), so the side cards and the info panel have room beside it.
            val shrink = 1f - AdaptiveScale.forTablet(phone = 1f, tablet = TABLET_BOARD_SCALE)
            val boardInset = DpSize(maxWidth * shrink / 2, (maxHeight - YapNPlayTheme.dimens.cb3dBoardDrop) * shrink / 2)
            val insetX = with(density) { boardInset.width.toPx() }
            val insetY = with(density) { boardInset.height.toPx() }
            val tips = CityBazaarIsoProjection.tips(Size(constraints.maxWidth - insetX * 2, constraints.maxHeight - drop - insetY * 2)).map { it + Offset(insetX, insetY) }
            // In 3D the info panel reaches to just short of the board's left corner; in 2D it is the side column.
            val panelWidth = if (state.threeD) {
                with(density) { (tips[0].x.toDp() - YapNPlayTheme.dimens.cb3dPanelGap).coerceAtLeast(side) }
            } else {
                side
            }
            // In 3D the snackbar is centred in the band between the top bar and the board's top corner.
            val boardTopCorner = with(density) { (tips[1].y + drop).toDp() }
            panelEdge = with(density) { panelWidth.toPx() }
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
                    .width(if (state.threeD) maxWidth - boardInset.width * 2 else maxWidth - side * 2)
                    .padding(
                        top = if (state.threeD) YapNPlayTheme.dimens.cb3dBoardDrop + boardInset.height else YapNPlayTheme.dimens.zero,
                        bottom = if (state.threeD) boardInset.height else YapNPlayTheme.dimens.zero,
                    ),
            ) { left, right ->
                // Tap the dice to roll (no press highlight), when it's the user's roll.
                val tap = if (state.canRoll) Modifier.clickable(interactionSource = null, indication = null) { send(CityBazaarMatchIntent.Roll) } else Modifier
                GlassDice(state = die1, palette = palette, grounded = false, modifier = left.then(tap))
                GlassDice(state = die2, palette = palette, grounded = false, modifier = right.then(tap))
            }
            if (state.threeD) {
                CityRoyale3dSeats(state = state, send = send, micGranted = micGranted, anchors = anchors, boardInset = boardInset)
            } else {
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
                            ArenaSeatSwitches(
                                micOn = state.micOn,
                                autoplay = state.autoplay,
                                onToggleMic = { send(CityBazaarMatchIntent.ToggleMic(micGranted())) },
                                onToggleAutoplay = { send(CityBazaarMatchIntent.ToggleAutoplay) },
                            showAutoplay = state.myCard?.out != true,
                            )
                        }
                    }
                    if (!state.spectating) {
                        TableGlassButton(
                            text = CityBazaarStrings.cbMyCities,
                            onClick = { send(CityBazaarMatchIntent.OpenMyCities) },
                            fill = smoke,
                            textColor = colors.TextPrimary,
                            enabled = state.stacks.isNotEmpty(),
                            modifier = buttonSize,
                        )
                    }
                    // Five colours to a row, as wide as My Properties, so every colour stays in sight.
                    CityBazaarOwnedStacks(
                        stacks = state.stacks,
                        onOpen = { send(CityBazaarMatchIntent.OpenStack(it)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (!state.spectating) {
                        Spacer(Modifier.weight(1f))
                        // Roll Dice: the big dice at the bottom right.
                        ArenaDiceButton(
                            label = state.rollLabel,
                            enabled = state.canRoll,
                            onClick = { send(CityBazaarMatchIntent.Roll) },
                            modifier = Modifier.align(Alignment.End),
                        )
                    }
                }
            }
            if (state.spectating) {
                TableSpectatorVoiceButtons(
                    micOn = state.micOn,
                    speakerOn = state.speakerOn,
                    onToggleMic = { send(CityBazaarMatchIntent.ToggleMic(micGranted())) },
                    onToggleSpeaker = { send(CityBazaarMatchIntent.ToggleSpeaker) },
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
            state.panel?.let { panel ->
                CityBazaarSidePanel(
                    panel = panel,
                    onPropertyClick = { send(CityBazaarMatchIntent.PanelPropertyClicked(it)) },
                    modifier = Modifier.align(Alignment.CenterStart).width(panelWidth).fillMaxHeight(),
                )
            }
            state.toast?.let { CbMoneyToast(toast = it, modifier = Modifier.align(Alignment.TopCenter)) }
            state.transaction?.let {
                val lift = if (state.threeD) YapNPlayTheme.dimens.cb3dDialogLift else YapNPlayTheme.dimens.zero
                CbTransactionNotice(tx = it, modifier = Modifier.align(Alignment.Center).offset(y = -lift))
            }
            state.moneyFlight?.let { flight ->
                val from = anchors[flight.fromSeat ?: BANK_ANCHOR]
                val to = anchors[flight.toSeat ?: BANK_ANCHOR]
                if (from != null && to != null) CbMoneyFlight(flight = flight, from = from - boardOrigin, to = to - boardOrigin)
            }
            state.feedSnack?.let {
                if (state.threeD) {
                    // Centred on the board's top corner (the board is centred), halfway down to it.
                    Box(modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth().height(boardTopCorner), contentAlignment = Alignment.Center) {
                        TableFeedSnackbar(line = it, playerTints = playerTints)
                    }
                } else {
                    TableFeedSnackbar(line = it, playerTints = playerTints, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = Spacing.Sm))
                }
            }
        }
    }
}

/**
 * The 3D table's seats, round the diamond (as in the design). Along the
 * bottom, split by the diamond's bottom tip: on the left the bank, My
 * Properties and the user's cities by colour; on the right the user's
 * card, Mic, Auto and Roll Dice (the die above its label). The bank, the
 * switches, My Properties and the colour stacks share one height; the card
 * and the die stand taller. Everyone else is seated round the board,
 * clockwise from the user, each where the board leaves room
 * ([CityRoyaleSeatRing]).
 */
@Composable
private fun BoxScope.CityRoyale3dSeats(
    state: CityBazaarMatchState,
    send: (CityBazaarMatchIntent) -> Unit,
    micGranted: () -> Boolean,
    anchors: MutableMap<Int, Offset>,
    boardInset: DpSize,
) {
    val dimens = YapNPlayTheme.dimens
    val colors = YapNPlayTheme.colors
    val rowHeight = dimens.arenaSwitchMax
    // Every other player's card the same size: bigger when few play, smaller as the table fills.
    val seatScale = TableSeatPlan.scale(state.seatsInTurnOrder.size)
    CityRoyaleSeatRing(
        cards = state.seatsInTurnOrder,
        boardDrop = dimens.cb3dBoardDrop,
        // A watcher has no bar, only the mic at the bottom centre (the seats stay at the sides).
        barHeight = if (state.spectating) Spacing.Sm else dimens.cb3dBarHeight,
        // The snackbar shows at the top centre: seats there keep below it.
        topReserve = dimens.cb3dSnackReserve,
        watching = state.spectating,
        boardInset = boardInset,
    ) { card ->
        TablePlayerCard(
            card = card,
            tint = card.colour.tint(),
            faded = card.out,
            badge = card.status,
            avatarSize = dimens.cb3dSeatAvatar,
            density = TableCardDensity.DENSE,
            details = { CbCashLine(cash = card.cash, compact = false) },
            onToggleHear = { send(CityBazaarMatchIntent.ToggleHear(card.seat)) },
            onClick = { send(CityBazaarMatchIntent.PlayerClicked(card.seat)) },
            onLongPress = { send(CityBazaarMatchIntent.PlayerLongPressed(card.seat)) },
            modifier = Modifier.onGloballyPositioned { anchors[card.seat] = it.boundsInRoot().center }// A seat with a mute button is wider: the button sits outside its name pill.
                .monoScaled(seatScale).width(dimens.cb3dSeatWidth + if (!card.isMe && card.canSpeak) tableCardMuteRoom(dimens.cb3dSeatAvatar) else dimens.zero),
        )
    }

    // Bottom left: the bank, My Properties, the cities by colour (a watcher has no bar: the seats use the room).
    if (!state.spectating) Row(
        // A little off the bottom edge, so the tag under the user's card (In Jail…) isn't cut.
        modifier = Modifier.align(Alignment.BottomStart).padding(bottom = Spacing.Xxs),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        CbBankButton(
            onClick = { send(CityBazaarMatchIntent.OpenBank) },
            modifier = Modifier.height(rowHeight).onGloballyPositioned { anchors[BANK_ANCHOR] = it.boundsInRoot().center },
        )
        TableGlassButton(
            text = CityBazaarStrings.cbMyCities,
            onClick = { send(CityBazaarMatchIntent.OpenMyCities) },
            fill = listOf(colors.LudoSmokeTop, colors.LudoSmokeBottom),
            textColor = colors.TextPrimary,
            enabled = state.stacks.isNotEmpty(),
            modifier = Modifier.height(rowHeight).width(dimens.cb3dPropertiesWidth),
        )
        if (state.stacks.isNotEmpty()) {
            CityBazaarOwnedStacks(
                stacks = state.stacks,
                onOpen = { send(CityBazaarMatchIntent.OpenStack(it)) },
                // Squares just under the row's height, taking only the room they need; centred on the row.
                cardSize = rowHeight - dimens.cbStackOffset * STACK_LAYERS,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
        }
    }
    // Bottom right: the user's card, Mic, Auto, Roll Dice — on the bottom edge, level with the bank's row.
    state.myCard?.let { card ->
        Row(
            modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = Spacing.Xxs),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
        ) {
            // The card and its Mic / Auto buttons share one middle line; the taller dice stands on the bottom.
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
                TablePlayerCard(
                    card = card,
                    tint = card.colour.tint(),
                    faded = card.out,
                    badge = card.status,
                    avatarSize = dimens.cbCardAvatar,
                    density = TableCardDensity.DENSE,
                    details = { CbCashLine(cash = card.cash, compact = false) },
                    onClick = { send(CityBazaarMatchIntent.PlayerClicked(card.seat)) },
                    modifier = Modifier.width(dimens.cb3dSeatWidth).onGloballyPositioned { anchors[card.seat] = it.boundsInRoot().center },
                )
                ArenaSeatSwitches(
                    micOn = state.micOn,
                    autoplay = state.autoplay,
                    onToggleMic = { send(CityBazaarMatchIntent.ToggleMic(micGranted())) },
                    onToggleAutoplay = { send(CityBazaarMatchIntent.ToggleAutoplay) },
                    showAutoplay = state.myCard?.out != true,
                    gap = Spacing.Xs,
                )
            }
            ArenaDiceButton(
                label = state.rollLabel,
                enabled = state.canRoll,
                onClick = { send(CityBazaarMatchIntent.Roll) },
            )
        }
    }
}

/** The deepest colour stack's offset layers (4 cities), kept inside the bar's row height. */
private const val STACK_LAYERS = 3

/** The bank's key among the card positions. */
private const val BANK_ANCHOR = -1

/** On a tablet, the 3D board's size × what it would fill. */
private const val TABLET_BOARD_SCALE = 0.9f
