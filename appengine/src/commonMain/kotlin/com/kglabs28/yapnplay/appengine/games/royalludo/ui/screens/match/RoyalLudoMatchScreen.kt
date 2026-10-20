package com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match

import com.kglabs28.yapnplay.appengine.ui.placeholders.RoyalLudoArtwork
import com.kglabs28.yapnplay.appengine.ui.components.common.rememberShareSheet
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.MatchLeaderboard
import com.kglabs28.yapnplay.appengine.ui.components.table.PlayerFriendDialog
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockBackPress
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
import com.kglabs28.yapnplay.appengine.core.designsystem.revealWhen
import com.kglabs28.yapnplay.appengine.games.elements.dice.ui.GlassDice
import com.kglabs28.yapnplay.appengine.games.elements.dice.ui.rememberGlassDiceState
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.LudoMatchModalActions
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.LudoMatchModalContent
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.board.LudoBoard
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.board.ludoDicePalette
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.players.LudoMyCard
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.players.LudoOpponentsColumn
import com.kglabs28.yapnplay.appengine.ui.components.table.TableSpectatorMicButton
import com.kglabs28.yapnplay.appengine.ui.components.table.TableTopBar
import com.kglabs28.yapnplay.appengine.ui.components.common.AppPermission
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay
import com.kglabs28.yapnplay.appengine.ui.components.common.rememberPermissionRequester
import com.kglabs28.yapnplay.appengine.ui.components.seat.SeatOfferBanner
import com.kglabs28.yapnplay.appengine.ui.placeholders.CozyHomeBackground

/**
 * Royal Ludo table: top bar (game, spectators, match clock, settings,
 * exit); the
 * board, as large as fits, with the glass dice (in the roller's colour)
 * on the centre plate; the user's card on the right with Mic, Autoplay and
 * Roll Dice; the other players on the left in turn order, each with a
 * round button to mute them for the user only. A banner shows a seat
 * someone left while it is offered. Music plays while the table is on
 * screen; in the background the server plays for the user.
 */
@Composable
fun RoyalLudoMatchScreen(
    matchId: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoyalLudoMatchViewModel? = null,
) {
    val state = viewModel?.state?.collectAsState()?.value ?: RoyalLudoMatchState()
    val send: (RoyalLudoMatchIntent) -> Unit = { viewModel?.onIntent(it) }
    val micPermission = rememberPermissionRequester(AppPermission.MICROPHONE)
    val dice = rememberGlassDiceState()
    val share = rememberShareSheet()
    // Back at the table is the same as the exit button (asks first).
    BlockBackPress(enabled = true, onBack = { send(RoyalLudoMatchIntent.ExitClicked) })
    LaunchedEffect(viewModel, matchId) {
        viewModel?.onIntent(RoyalLudoMatchIntent.Load(matchId))
        viewModel?.effects?.collect { effect ->
            when (effect) {
                RoyalLudoMatchEffect.Close -> onClose()
                RoyalLudoMatchEffect.RequestMicPermission ->
                    micPermission.request { send(RoyalLudoMatchIntent.MicPermissionResult(it)) }
                RoyalLudoMatchEffect.OpenAppSettings -> micPermission.openSettings()
                is RoyalLudoMatchEffect.ShareText -> share(effect.text)
            }
        }
    }
    // Started/stopped covers both leaving the screen and the app going to the background.
    LifecycleStartEffect(viewModel) {
        send(RoyalLudoMatchIntent.ScreenVisible(true))
        onStopOrDispose { send(RoyalLudoMatchIntent.ScreenVisible(false)) }
    }
    LaunchedEffect(state.dice.rollId) {
        if (state.dice.rollId > 0) dice.roll(state.dice.value)
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
                onSettings = { send(RoyalLudoMatchIntent.OpenSettings) },
                onExit = { send(RoyalLudoMatchIntent.ExitClicked) },
            )
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                LudoBoard(
                    pieces = state.pieces,
                    rotation = state.rotation,
                    onPieceClick = { send(RoyalLudoMatchIntent.PieceClicked(it.seat, it.piece)) },
                    places = state.basePlaces,
                    // Right of the other players' column, clear of the user's card.
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = maxWidth * AppConstants.LUDO_CARD_COLUMN_WEIGHT)
                        .fillMaxHeight()
                        .width(maxWidth * if (state.spectating) AppConstants.LUDO_SPECTATOR_BOARD_WIDTH_WEIGHT else AppConstants.LUDO_BOARD_WIDTH_WEIGHT),
                ) { slot ->
                    // Hidden while a piece is chosen and moved (so taps reach the
                    // pieces); takes taps only when the user can roll, with no
                    // press highlight.
                    if (state.dice.visible) {
                        GlassDice(
                            state = dice,
                            palette = ludoDicePalette(state.dice.colour),
                            modifier = if (state.canRoll) {
                                slot.clickable(interactionSource = null, indication = null) { send(RoyalLudoMatchIntent.Roll) }
                            } else {
                                slot
                            },
                        )
                    }
                }
                state.seatBanner?.let { text ->
                    SeatOfferBanner(text = text, modifier = Modifier.align(Alignment.TopCenter))
                }
                state.myCard?.let { card ->
                    LudoMyCard(
                        card = card,
                        canRoll = state.canRoll,
                        moveHint = state.moveHint,
                        autoplay = state.autoplay,
                        micOn = state.micOn,
                        onRoll = { send(RoyalLudoMatchIntent.Roll) },
                        onToggleAutoplay = { send(RoyalLudoMatchIntent.ToggleAutoplay) },
                        onToggleMic = { send(RoyalLudoMatchIntent.ToggleMic(micPermission.isGranted())) },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxWidth(AppConstants.LUDO_CARD_COLUMN_WEIGHT),
                    )
                }
                // Playing: the others on the left. Watching: two on each side, by house.
                LudoOpponentsColumn(
                    cards = if (state.spectating) state.leftCards else state.opponents,
                    onToggleHear = { send(RoyalLudoMatchIntent.ToggleHear(it)) },
                    onLongPress = { send(RoyalLudoMatchIntent.PlayerLongPressed(it)) },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxWidth(AppConstants.LUDO_CARD_COLUMN_WEIGHT),
                )
                if (state.spectating) {
                    // Below the board's bottom corner, in the middle.
                    TableSpectatorMicButton(
                        micOn = state.micOn,
                        onToggle = { send(RoyalLudoMatchIntent.ToggleMic(micPermission.isGranted())) },
                        modifier = Modifier.align(Alignment.BottomCenter),
                    )
                    LudoOpponentsColumn(
                        cards = state.rightCards,
                        onToggleHear = { send(RoyalLudoMatchIntent.ToggleHear(it)) },
                        onLongPress = { send(RoyalLudoMatchIntent.PlayerLongPressed(it)) },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxWidth(AppConstants.LUDO_CARD_COLUMN_WEIGHT),
                    )
                }
            }
        }

        val board = state.leaderboard
        BlockingOverlay(visible = board != null) {
            if (board != null) {
                MatchLeaderboard(
                    board = board,
                    gameArt = { RoyalLudoArtwork(modifier = it) },
                    onPlayAgain = { send(RoyalLudoMatchIntent.Done) },
                )
            }
        }

        val friendAction = state.friendAction
        BlockingOverlay(visible = friendAction != null, onDismiss = { send(RoyalLudoMatchIntent.DismissFriendAction) }) {
            if (friendAction != null) {
                PlayerFriendDialog(
                    action = friendAction,
                    onConfirm = { send(RoyalLudoMatchIntent.ConfirmFriendAction) },
                    onCancel = { send(RoyalLudoMatchIntent.DismissFriendAction) },
                )
            }
        }

        val modal = state.modal
        BlockingOverlay(
            visible = modal != null,
            // The final ranking, a vote and seat decisions need an answer (the
            // timed ones close themselves when their time runs out).
            onDismiss = if (modal is LudoMatchModal.Result || modal is LudoMatchModal.Vote || modal is LudoMatchModal.SeatInvite ||
                modal is LudoMatchModal.SeatOfferPrompt || modal is LudoMatchModal.LeaderSeatChoice || modal is LudoMatchModal.AbsenceCheck
            ) {
                null
            } else {
                { send(RoyalLudoMatchIntent.DismissModal) }
            },
        ) {
            if (modal != null) {
                LudoMatchModalContent(
                    modal = modal,
                    actions = LudoMatchModalActions(
                        onDismiss = { send(RoyalLudoMatchIntent.DismissModal) },
                        onConfirmLeave = { send(RoyalLudoMatchIntent.ConfirmLeave) },
                        onDone = { send(RoyalLudoMatchIntent.Done) },
                        onOpenAppSettings = { send(RoyalLudoMatchIntent.OpenAppSettings) },
                        onToggleEffects = { send(RoyalLudoMatchIntent.ToggleEffects) },
                        onToggleMusic = { send(RoyalLudoMatchIntent.ToggleMusic) },
                        onOpenReport = { send(RoyalLudoMatchIntent.OpenReport) },
                        onSelectReportPerson = { send(RoyalLudoMatchIntent.SelectReportPerson(it)) },
                        onSubmitReport = { send(RoyalLudoMatchIntent.SubmitReport) },
                        onVote = { send(RoyalLudoMatchIntent.Vote(it)) },
                        onWatchAd = { send(RoyalLudoMatchIntent.WatchAd) },
                        onTakeSeat = { send(RoyalLudoMatchIntent.TakeSeat(it)) },
                        onDeclineSeat = { send(RoyalLudoMatchIntent.DeclineSeat(it)) },
                        onPickSeatTaker = { send(RoyalLudoMatchIntent.PickSeatTaker(it)) },
                        onAnswerSeatOffer = { send(RoyalLudoMatchIntent.AnswerSeatOffer(it)) },
                        onInviteToSeat = { send(RoyalLudoMatchIntent.InviteToSeat) },
                        onOfferSeatToBot = { send(RoyalLudoMatchIntent.OfferSeatToBot) },
                        onSpectate = { send(RoyalLudoMatchIntent.Spectate) },
                        onStillHere = { send(RoyalLudoMatchIntent.StillHere) },
                    ),
                )
            }
        }
    }
}
