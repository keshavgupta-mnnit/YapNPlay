package com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match

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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.topbar.LudoMatchTopBar
import com.kglabs28.yapnplay.appengine.ui.components.common.AppPermission
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay
import com.kglabs28.yapnplay.appengine.ui.components.common.rememberPermissionRequester
import com.kglabs28.yapnplay.appengine.ui.placeholders.CozyHomeBackground

/**
 * Royal Ludo table: top bar (game, spectators, match clock, settings,
 * exit); the
 * board, as large as fits, with the glass dice (in the roller's colour)
 * on the centre plate; the user's card bottom-right with Mic, Autoplay and
 * Roll Dice; the other players on the left in turn order, each with a
 * round button to mute them for the user only. Music plays while the
 * table is on screen.
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
    LaunchedEffect(viewModel, matchId) {
        viewModel?.onIntent(RoyalLudoMatchIntent.Load(matchId))
        viewModel?.effects?.collect { effect ->
            when (effect) {
                RoyalLudoMatchEffect.Close -> onClose()
                RoyalLudoMatchEffect.RequestMicPermission ->
                    micPermission.request { send(RoyalLudoMatchIntent.MicPermissionResult(it)) }
                RoyalLudoMatchEffect.OpenAppSettings -> micPermission.openSettings()
            }
        }
    }
    DisposableEffect(viewModel) {
        send(RoyalLudoMatchIntent.ScreenVisible(true))
        onDispose { send(RoyalLudoMatchIntent.ScreenVisible(false)) }
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
            LudoMatchTopBar(
                title = state.title,
                subtitle = state.subtitle,
                audience = state.audience,
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
                        .width(maxWidth * AppConstants.LUDO_BOARD_WIDTH_WEIGHT),
                ) { slot ->
                    if (state.dice.visible) {
                        GlassDice(
                            state = dice,
                            palette = ludoDicePalette(state.dice.colour),
                            modifier = slot.clickable(enabled = state.canRoll) { send(RoyalLudoMatchIntent.Roll) },
                        )
                    }
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
                            .align(Alignment.BottomEnd)
                            .fillMaxWidth(AppConstants.LUDO_CARD_COLUMN_WEIGHT),
                    )
                }
                LudoOpponentsColumn(
                    cards = state.opponents,
                    onToggleHear = { send(RoyalLudoMatchIntent.ToggleHear(it)) },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxWidth(AppConstants.LUDO_CARD_COLUMN_WEIGHT),
                )
            }
        }

        val modal = state.modal
        BlockingOverlay(
            visible = modal != null,
            // The final ranking and a vote prompt need an answer (the vote
            // closes itself when its time runs out).
            onDismiss = if (modal is LudoMatchModal.Result || modal is LudoMatchModal.Vote) null else ({ send(RoyalLudoMatchIntent.DismissModal) }),
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
                    ),
                )
            }
        }
    }
}
