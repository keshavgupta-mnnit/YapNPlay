package com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.revealWhen
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.LudoMatchModalContent
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.board.LudoBoard
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.players.LudoCardColumn
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.topbar.LudoMatchTopBar
import com.kglabs28.yapnplay.appengine.ui.components.common.AppPermission
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay
import com.kglabs28.yapnplay.appengine.ui.components.common.rememberPermissionRequester
import com.kglabs28.yapnplay.appengine.games.elements.dice.ui.GlassDice
import com.kglabs28.yapnplay.appengine.games.elements.dice.ui.rememberGlassDiceState
import com.kglabs28.yapnplay.appengine.ui.placeholders.CozyHomeBackground

/**
 * Royal Ludo table: top bar, player cards in the four corners (the
 * user's bottom-left with Mic, Autoplay and Roll Dice) and the board
 * with the glass dice in the middle. (The mic toggle returns later.)
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
                pingLabel = state.pingLabel,
                pingQuality = state.pingQuality,
                onSettings = { send(RoyalLudoMatchIntent.OpenSettings) },
                onExit = { send(RoyalLudoMatchIntent.ExitClicked) },
            )
            // The board takes the whole width; the diamond is widest in the
            // middle, between the top and bottom cards, which float over
            // the empty corners at each side.
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                LudoBoard(
                    pieces = state.pieces,
                    rotation = state.rotation,
                    onPieceClick = { send(RoyalLudoMatchIntent.PieceClicked(it.seat, it.piece)) },
                    modifier = Modifier.fillMaxSize(),
                ) { slot ->
                    GlassDice(
                        state = dice,
                        modifier = slot.clickable(enabled = state.canRoll) { send(RoyalLudoMatchIntent.Roll) },
                    )
                }
                LudoCardColumn(
                    top = state.cards[BoardCorner.TOP_LEFT],
                    bottom = state.cards[BoardCorner.BOTTOM_LEFT],
                    canRoll = state.canRoll,
                    autoplay = state.autoplay,
                    micOn = state.micOn,
                    onRoll = { send(RoyalLudoMatchIntent.Roll) },
                    onToggleAutoplay = { send(RoyalLudoMatchIntent.ToggleAutoplay) },
                    onToggleMic = { send(RoyalLudoMatchIntent.ToggleMic(micPermission.isGranted())) },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxWidth(AppConstants.LUDO_CARD_COLUMN_WEIGHT),
                )
                LudoCardColumn(
                    top = state.cards[BoardCorner.TOP_RIGHT],
                    bottom = state.cards[BoardCorner.BOTTOM_RIGHT],
                    canRoll = state.canRoll,
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
        }

        val modal = state.modal
        BlockingOverlay(
            visible = modal != null,
            onDismiss = if (modal is LudoMatchModal.Result) null else ({ send(RoyalLudoMatchIntent.DismissModal) }),
        ) {
            if (modal != null) {
                LudoMatchModalContent(
                    modal = modal,
                    onConfirmLeave = { send(RoyalLudoMatchIntent.ConfirmLeave) },
                    onDismiss = { send(RoyalLudoMatchIntent.DismissModal) },
                    onDone = { send(RoyalLudoMatchIntent.Done) },
                    onOpenSettings = { send(RoyalLudoMatchIntent.OpenAppSettings) },
                )
            }
        }
    }
}
