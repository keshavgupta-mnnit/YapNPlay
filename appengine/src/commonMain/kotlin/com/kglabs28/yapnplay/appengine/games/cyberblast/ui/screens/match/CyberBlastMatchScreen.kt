package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.screens.match

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.remember
import com.kglabs28.yapnplay.appengine.games.cyberblast.utils.CyberCardFit
import com.kglabs28.yapnplay.appengine.ui.components.table.monoScaled
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.LifecycleStartEffect
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.revealWhen
import com.kglabs28.yapnplay.appengine.core.ui.CyberBlastStrings
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.games.cyberblast.ui.components.CyberBanner
import com.kglabs28.yapnplay.appengine.games.cyberblast.ui.components.CyberBoardCanvas
import com.kglabs28.yapnplay.appengine.games.cyberblast.ui.components.CyberBombButton
import com.kglabs28.yapnplay.appengine.games.cyberblast.ui.components.CyberDirectionPad
import com.kglabs28.yapnplay.appengine.games.cyberblast.ui.components.CyberMiniMap
import androidx.compose.foundation.layout.size
import com.kglabs28.yapnplay.appengine.games.cyberblast.ui.components.CyberPlayerCard
import com.kglabs28.yapnplay.appengine.ui.components.common.AppPermission
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockBackPress
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.rememberPermissionRequester
import com.kglabs28.yapnplay.appengine.ui.components.common.verticalScrollWithBar
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.MatchLeaderboard
import com.kglabs28.yapnplay.appengine.ui.components.moderation.EndVoteCard
import com.kglabs28.yapnplay.appengine.ui.components.moderation.MatchSettingsCard
import com.kglabs28.yapnplay.appengine.ui.components.seat.EndGameConfirmDialog
import com.kglabs28.yapnplay.appengine.ui.components.seat.LeaveMatchDialog
import com.kglabs28.yapnplay.appengine.ui.components.seat.SeatHandoverDialog
import com.kglabs28.yapnplay.appengine.ui.components.table.ArenaTopBar
import com.kglabs28.yapnplay.appengine.ui.components.table.TableSpectatorVoiceButtons
import com.kglabs28.yapnplay.appengine.ui.components.table.forSeat
import com.kglabs28.yapnplay.appengine.ui.components.table.tableSeatTints
import com.kglabs28.yapnplay.appengine.ui.placeholders.CyberBlastArtwork
import com.kglabs28.yapnplay.appengine.ui.placeholders.GameArenaBackground
import com.kglabs28.yapnplay.appengine.ui.screens.emojigift.EmojiGiftLayer
import com.kglabs28.yapnplay.appengine.ui.screens.emojigift.EmojiGiftViewModel

/**
 * The Cyber Blast table: the top bar, then the arena in the middle with
 * half the players' cards on each side (the user's first, top left). Under
 * the left cards the round pad, under the right ones the bomb button.
 */
@Composable
fun CyberBlastMatchScreen(
    matchId: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CyberBlastMatchViewModel? = null,
    emojiGiftViewModel: EmojiGiftViewModel? = null,
) {
    val state = viewModel?.state?.collectAsState()?.value ?: CyberBlastMatchState()
    val send: (CyberBlastMatchIntent) -> Unit = { viewModel?.onIntent(it) }
    val micPermission = rememberPermissionRequester(AppPermission.MICROPHONE)

    BlockBackPress(enabled = true, onBack = { send(if (state.leaderboard != null) CyberBlastMatchIntent.Done else CyberBlastMatchIntent.ExitClicked) })
    LaunchedEffect(viewModel, matchId) {
        viewModel?.onIntent(CyberBlastMatchIntent.Load(matchId))
        viewModel?.effects?.collect { effect ->
            when (effect) {
                CyberBlastMatchEffect.Close -> onClose()
                CyberBlastMatchEffect.RequestMicPermission -> micPermission.request { send(CyberBlastMatchIntent.MicPermissionResult(it)) }
                CyberBlastMatchEffect.OpenAppSettings -> micPermission.openSettings()
            }
        }
    }
    LifecycleStartEffect(viewModel) {
        send(CyberBlastMatchIntent.ScreenVisible(true))
        onStopOrDispose { send(CyberBlastMatchIntent.ScreenVisible(false)) }
    }

    GameArenaBackground(modifier = modifier) {
        EmojiGiftLayer(matchId = matchId, viewModel = emojiGiftViewModel) {
            CyberBlastTable(state = state, send = send, micGranted = { micPermission.isGranted() })
        }
        // The countdown, "GO!" and "You're out" in the middle of the whole screen.
        if (state.banner.isNotEmpty()) Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CyberBanner(text = state.banner) }
        val modal = state.modal
        val needsAnswer = modal is CyberModal.EndVote || modal is CyberModal.Seat
        BlockingOverlay(visible = modal != null, onDismiss = if (needsAnswer) null else ({ send(CyberBlastMatchIntent.DismissModal) })) {
            when (modal) {
                is CyberModal.Settings -> MatchSettingsCard(
                    effectsOn = modal.effectsOn,
                    musicOn = modal.musicOn,
                    onToggleEffects = { send(CyberBlastMatchIntent.ToggleEffects) },
                    onToggleMusic = { send(CyberBlastMatchIntent.ToggleMusic) },
                    onEndGame = if (modal.canProposeEnd) ({ send(CyberBlastMatchIntent.ProposeEnd) }) else null,
                    endGameLabel = TableStrings.endGameButton,
                    onClose = { send(CyberBlastMatchIntent.DismissModal) },
                    rules = CyberBlastStrings.blastRules,
                )
                is CyberModal.Leave -> LeaveMatchDialog(
                    onStay = { send(CyberBlastMatchIntent.DismissModal) },
                    onLeave = { send(CyberBlastMatchIntent.ConfirmLeave) },
                    message = modal.dialog.message,
                    options = modal.dialog.options,
                    selectedIndex = modal.dialog.selectedIndex,
                    onSelect = { send(CyberBlastMatchIntent.PickSeatTaker(it)) },
                    countdown = modal.dialog.countdown,
                )
                CyberModal.MicPermissionNeeded -> MicPermissionCard(send)
                CyberModal.ConfirmEnd -> EndGameConfirmDialog(onCancel = { send(CyberBlastMatchIntent.DismissModal) }, onEnd = { send(CyberBlastMatchIntent.ConfirmEnd) })
                is CyberModal.EndVote -> EndVoteCard(modal.vote, onVote = { send(CyberBlastMatchIntent.VoteEnd(it)) })
                is CyberModal.Seat -> SeatHandoverDialog(modal.modal, onAction = { send(CyberBlastMatchIntent.Seat(it)) })
                null -> Unit
            }
        }
        val board = state.leaderboard
        BlockingOverlay(visible = board != null) {
            if (board != null) {
                MatchLeaderboard(board = board, gameArt = { CyberBlastArtwork(modifier = it) }, onPlayAgain = { send(CyberBlastMatchIntent.Done) })
            }
        }
    }
}

@Composable
private fun CyberBlastTable(state: CyberBlastMatchState, send: (CyberBlastMatchIntent) -> Unit, micGranted: () -> Boolean) {
    val tints = tableSeatTints()
    val camera = remember { CyberCameraState() }
    val tint: (Int) -> Color = { tints.forSeat(it) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.Md, vertical = Spacing.Xs)
            .revealWhen(state.loaded),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        ArenaTopBar(
            title = state.title,
            subtitle = state.subtitle,
            audience = state.audience,
            showAudience = state.showAudience,
            spectatorCount = state.spectatorCount,
            elapsed = state.elapsed,
            onSettings = { send(CyberBlastMatchIntent.OpenSettings) },
            onExit = { send(CyberBlastMatchIntent.ExitClicked) },
            onToggleHearSpectator = { send(CyberBlastMatchIntent.ToggleHearSpectator(it)) },
        )
        BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
        val dimens = YapNPlayTheme.dimens
        // The other cards share the height left above the bomb button, and set the right column's width.
        val cardsRoom = maxHeight - dimens.blastBombColumnHeight - Spacing.Xs
        val scale = CyberCardFit.scale(cardsRoom, state.rightCards.size, dimens.blastCardHeight, Spacing.Xxs)
        val rightWidth = maxOf(dimens.blastSideWidth * scale, dimens.blastBombColumnWidth)
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
            // Left: the user's card, the Mini-map under it, the pad at the bottom (a spectator: their voice buttons).
            Column(
                modifier = Modifier.width(YapNPlayTheme.dimens.blastSideWidth).fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                state.leftCards.forEach { card -> SeatCard(card, state, send, tint, micGranted, Modifier.fillMaxWidth()) }
                if (state.miniMap) {
                    CyberMiniMap(feed = state.feed, seatColours = tints, modifier = Modifier.fillMaxWidth().height(YapNPlayTheme.dimens.blastMiniMapHeight))
                }
                Spacer(modifier = Modifier.weight(1f))
                if (state.spectating) {
                    TableSpectatorVoiceButtons(
                        micOn = state.micOn,
                        speakerOn = state.speakerOn,
                        onToggleMic = { send(CyberBlastMatchIntent.ToggleMic(micGranted())) },
                        onToggleSpeaker = { send(CyberBlastMatchIntent.ToggleSpeaker) },
                    )
                } else {
                    CyberDirectionPad(
                        onDir = {
                            // Moving brings the camera back to the user's character.
                            if (it != null) camera.follow()
                            send(CyberBlastMatchIntent.Steer(it))
                        },
                        enabled = state.canPlay,
                    )
                }
            }
            // Middle: the arena, as big as the space between the panels allows.
            Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                CyberBoardCanvas(feed = state.feed, camera = camera, seatColours = tints, modifier = Modifier.fillMaxSize())
            }
            // Right: everyone else's cards, shrunk to fit above the bomb button; the column is only as wide as they are, against the right edge.
            Column(
                modifier = Modifier.width(rightWidth).fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
                horizontalAlignment = Alignment.End,
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.Xxs), horizontalAlignment = Alignment.End) {
                    state.rightCards.forEach { card -> SeatCard(card, state, send, tint, micGranted, Modifier.width(dimens.blastSideWidth).monoScaled(scale)) }
                }
                if (!state.spectating) CyberBombButton(onDrop = { send(CyberBlastMatchIntent.DropBomb) }, colour = tint(state.mySeat), seatColours = tints, enabled = state.canPlay)
            }
        }
        }
    }
}

/** One player's card at this table. */
@Composable
private fun SeatCard(
    card: CyberCardUi,
    state: CyberBlastMatchState,
    send: (CyberBlastMatchIntent) -> Unit,
    tint: (Int) -> Color,
    micGranted: () -> Boolean,
    modifier: Modifier = Modifier,
) {
    CyberPlayerCard(
        card = card,
        tint = tint(card.seat),
        onToggleHear = { send(CyberBlastMatchIntent.ToggleHear(card.seat)) },
        onLongPress = { send(CyberBlastMatchIntent.PlayerLongPressed(card.seat)) },
        micOn = state.micOn,
        onToggleMic = if (card.isMe) ({ send(CyberBlastMatchIntent.ToggleMic(micGranted())) }) else null,
        modifier = modifier,
    )
}

@Composable
private fun MicPermissionCard(send: (CyberBlastMatchIntent) -> Unit) {
    val colors = YapNPlayTheme.colors
    ModalCard(
        title = Strings.micPermissionTitle,
        subtitle = Strings.micPermissionBody,
        onClose = { send(CyberBlastMatchIntent.DismissModal) },
        actions = {
            PillButton(
                text = Strings.micPermissionOpenSettings,
                onClick = { send(CyberBlastMatchIntent.OpenAppSettings) },
                gradient = listOf(colors.CtaGreenStart, colors.CtaGreenEnd),
                contentColor = colors.CtaGreenContent,
                trailingIcon = null,
            )
        },
    )
}
