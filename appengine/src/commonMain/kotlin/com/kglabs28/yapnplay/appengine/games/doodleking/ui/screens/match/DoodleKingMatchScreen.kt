package com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match

import com.kglabs28.yapnplay.appengine.ui.placeholders.DoodleKingArtwork
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.components.DoodleGuessFeedback
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.components.DoodleStatusPill
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.components.DoodleCardMarkIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.kglabs28.yapnplay.appengine.ui.components.seat.SeatHandoverDialog
import com.kglabs28.yapnplay.appengine.ui.components.seat.KeepPlayingDialog
import com.kglabs28.yapnplay.appengine.ui.components.seat.EndGameConfirmDialog
import com.kglabs28.yapnplay.appengine.ui.components.moderation.EndVoteCard
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.compose.LifecycleStartEffect
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.revealWhen
import com.kglabs28.yapnplay.appengine.core.ui.DoodleStrings
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.components.DoodleControls
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.components.DoodleMyCard
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.components.DoodleCanvasView
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.components.DoodleFeedPanel
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.components.DoodlePickCard
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.components.DoodleRankPanel
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.components.DoodleRevealCard
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.components.doodleSeatTints
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.components.forSeat
import com.kglabs28.yapnplay.appengine.ui.components.common.AppPermission
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockBackPress
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.rememberPermissionRequester
import com.kglabs28.yapnplay.appengine.ui.components.common.verticalScrollWithBar
import com.kglabs28.yapnplay.appengine.ui.components.keyboard.LocalAppKeyboard
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.MatchLeaderboard
import com.kglabs28.yapnplay.appengine.ui.components.moderation.MatchSettingsCard
import com.kglabs28.yapnplay.appengine.ui.components.seat.LeaveMatchDialog
import com.kglabs28.yapnplay.appengine.ui.components.table.ArenaTopBar
import com.kglabs28.yapnplay.appengine.ui.components.table.TableCardDensity
import com.kglabs28.yapnplay.appengine.ui.components.table.TablePlayerCard
import com.kglabs28.yapnplay.appengine.ui.components.table.TableSpectatorVoiceButtons
import com.kglabs28.yapnplay.appengine.ui.components.table.arenaGlass
import com.kglabs28.yapnplay.appengine.ui.placeholders.GameArenaBackground

/**
 * The Doodle King table. Left: everyone's card, always in seat order.
 * Centre: whose turn it is, the time left and the front-facing drawing
 * board. Right: this turn's guesses over the live leaderboard. Bottom: the
 * user's bar (tools and word while drawing; hint and guess box while
 * guessing). With the keyboard open the table folds into a short strip
 * above it: a small live drawing, the time and hint, the latest guesses
 * and the guess box.
 */
@Composable
fun DoodleKingMatchScreen(
    matchId: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DoodleKingMatchViewModel? = null,
) {
    val state = viewModel?.state?.collectAsState()?.value ?: DoodleKingMatchState()
    val send: (DoodleKingMatchIntent) -> Unit = { viewModel?.onIntent(it) }
    val micPermission = rememberPermissionRequester(AppPermission.MICROPHONE)

    // Back is the exit button (asks first); once the match is over it just leaves.
    BlockBackPress(enabled = true, onBack = { send(if (state.leaderboard != null) DoodleKingMatchIntent.Done else DoodleKingMatchIntent.ExitClicked) })
    LaunchedEffect(viewModel, matchId) {
        viewModel?.onIntent(DoodleKingMatchIntent.Load(matchId))
        viewModel?.effects?.collect { effect ->
            when (effect) {
                DoodleKingMatchEffect.Close -> onClose()
                DoodleKingMatchEffect.RequestMicPermission -> micPermission.request { send(DoodleKingMatchIntent.MicPermissionResult(it)) }
                DoodleKingMatchEffect.OpenAppSettings -> micPermission.openSettings()
            }
        }
    }
    LifecycleStartEffect(viewModel) {
        send(DoodleKingMatchIntent.ScreenVisible(true))
        onStopOrDispose { send(DoodleKingMatchIntent.ScreenVisible(false)) }
    }

    GameArenaBackground(modifier = modifier) {
        DoodleKingTable(state = state, send = send, micGranted = { micPermission.isGranted() })

        val pick = state.pick
        BlockingOverlay(visible = pick != null) {
            if (pick != null) DoodlePickCard(pick = pick, onPick = { send(DoodleKingMatchIntent.PickWord(it)) })
        }

        val modal = state.modal
        // Votes and seat offers need an answer; the rest close on a tap outside.
        val needsAnswer = modal is DoodleModal.EndVote || modal is DoodleModal.KeepPlaying || modal is DoodleModal.Seat
        BlockingOverlay(visible = modal != null, onDismiss = if (needsAnswer) null else ({ send(DoodleKingMatchIntent.DismissModal) })) {
            when (modal) {
                is DoodleModal.Settings -> MatchSettingsCard(
                    effectsOn = modal.effectsOn,
                    musicOn = modal.musicOn,
                    onToggleEffects = { send(DoodleKingMatchIntent.ToggleEffects) },
                    onToggleMusic = { send(DoodleKingMatchIntent.ToggleMusic) },
                    onEndGame = if (modal.canProposeEnd) ({ send(DoodleKingMatchIntent.ProposeEnd) }) else null,
                    endGameLabel = TableStrings.endGameButton,
                    onClose = { send(DoodleKingMatchIntent.DismissModal) },
                    rules = DoodleStrings.dkRules,
                )
                is DoodleModal.Leave -> LeaveMatchDialog(
                    onStay = { send(DoodleKingMatchIntent.DismissModal) },
                    onLeave = { send(DoodleKingMatchIntent.ConfirmLeave) },
                    message = modal.dialog.message,
                    options = modal.dialog.options,
                    selectedIndex = modal.dialog.selectedIndex,
                    onSelect = { send(DoodleKingMatchIntent.PickSeatTaker(it)) },
                    countdown = modal.dialog.countdown,
                )
                DoodleModal.MicPermissionNeeded -> MicPermissionCard(send)
                DoodleModal.ConfirmEnd -> EndGameConfirmDialog(onCancel = { send(DoodleKingMatchIntent.DismissModal) }, onEnd = { send(DoodleKingMatchIntent.ConfirmEnd) })
                is DoodleModal.EndVote -> EndVoteCard(modal.vote, onVote = { send(DoodleKingMatchIntent.VoteEnd(it)) })
                is DoodleModal.KeepPlaying -> KeepPlayingDialog(modal.vote, onVote = { send(DoodleKingMatchIntent.VoteKeepPlaying(it)) })
                is DoodleModal.Seat -> SeatHandoverDialog(modal.modal, onAction = { send(DoodleKingMatchIntent.Seat(it)) })
                null -> Unit
            }
        }

        val board = state.leaderboard
        BlockingOverlay(visible = board != null) {
            if (board != null) {
                MatchLeaderboard(
                    board = board,
                    gameArt = { DoodleKingArtwork(modifier = it) },
                    onPlayAgain = { send(DoodleKingMatchIntent.Done) },
                )
            }
        }
    }
}

/**
 * The table. While the user types a guess it folds into a strip above the
 * app's (short) keyboard: a bigger live drawing with the time under it, and
 * the turn's guesses as tall as the drawing with the answer to the last
 * guess under them. The keyboard shows the guess itself, as letter slots.
 */
@Composable
private fun DoodleKingTable(state: DoodleKingMatchState, send: (DoodleKingMatchIntent) -> Unit, micGranted: () -> Boolean) {
    val dimens = YapNPlayTheme.dimens
    val tints = doodleSeatTints()
    val seatTint: (Int) -> Color = { tints.forSeat(it) }
    val keyboard = LocalAppKeyboard.current
    // The guess box's keyboard belongs to the table: it stays open while the table folds into its strip.
    val owner = remember { Any() }
    DisposableEffect(owner) { onDispose { keyboard.close(owner) } }
    var guess by rememberSaveable { mutableStateOf("") }
    // The last answer shows on the box until the user types again.
    var toneId by remember { mutableIntStateOf(-1) }
    val feedback = state.feedback
    LaunchedEffect(feedback?.id) {
        if (feedback == null) return@LaunchedEffect
        toneId = feedback.id
        if (feedback.kind == DoodleGuessResult.RIGHT) {
            // Got it: back to the table, and the box takes no more typing.
            keyboard.close(owner)
            guess = ""
        } else {
            keyboard.setError(owner, true)
        }
    }
    // A new turn: an empty box.
    LaunchedEffect(state.board.drawingId) { guess = "" }
    val guessing = state.barMode == DoodleBarMode.GUESS
    // Nothing left to guess (the turn ended, or they got it): the keyboard goes.
    LaunchedEffect(guessing) { if (!guessing) keyboard.close(owner) }
    val appKeys = keyboard.isOpenFor(owner)
    val phoneKeys = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val typing = guessing && (appKeys || phoneKeys)
    val submit = { if (guess.isNotBlank()) send(DoodleKingMatchIntent.SendGuess(guess)) }
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        // The app's keys cover the bottom: the strip fits above them, so nothing needs lifting.
        val room = if (typing && appKeys) maxHeight - dimens.appKeyboardCompactHeight - dimens.appKeyboardGap else maxHeight
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(room)
                .imePadding()
                .padding(horizontal = Spacing.Md, vertical = Spacing.Xs)
                .revealWhen(state.loaded),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            if (typing) {
                TypingStrip(state = state, seatTint = seatTint, modifier = Modifier.weight(1f))
            } else {
                ArenaTopBar(
                    title = state.title,
                    subtitle = state.subtitle,
                    audience = state.audience,
                    showAudience = state.showAudience,
                    spectatorCount = state.spectatorCount,
                    elapsed = state.elapsed,
                    onSettings = { send(DoodleKingMatchIntent.OpenSettings) },
                    onExit = { send(DoodleKingMatchIntent.ExitClicked) },
                    onToggleHearSpectator = { send(DoodleKingMatchIntent.ToggleHearSpectator(it)) },
                )
                TableMiddle(
                    state = state,
                    send = send,
                    seatTint = seatTint,
                    micGranted = micGranted,
                    controls = {
                        DoodleControls(
                            state = state,
                            guess = guess,
                            onGuessChange = {
                                guess = it
                                toneId = -1
                            },
                            onSubmitGuess = submit,
                            keyboardOwner = owner,
                            guessTone = feedback?.takeIf { it.id == toneId }?.kind,
                            send = send,
                        )
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/**
 * Left: everyone else's cards in seat order, the user's own (with its mic and word) at the bottom.
 * Centre: the status pill, the board, and the user's controls under it. Right: the leaderboard
 * on top and the turn's guesses under it, the whole height.
 */
@Composable
private fun TableMiddle(
    state: DoodleKingMatchState,
    send: (DoodleKingMatchIntent) -> Unit,
    seatTint: (Int) -> Color,
    micGranted: () -> Boolean,
    controls: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = YapNPlayTheme.dimens
    val colors = YapNPlayTheme.colors
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
        Column(modifier = Modifier.width(dimens.doodleLeftWidth).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().verticalScrollWithBar(),
                verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
            ) {
                state.leftCards.forEach { card ->
                    TablePlayerCard(
                        card = card,
                        tint = seatTint(card.seat),
                        faded = card.left,
                        badge = card.tag,
                        avatarSize = dimens.doodleCardAvatar,
                        density = TableCardDensity.COMPACT,
                        details = { BasicText(text = card.score, style = YapNPlayTheme.typography.caption.copy(color = colors.ArenaCrown)) },
                        // A brush while they draw, a tick once they guessed it.
                        pillEnd = { DoodleCardMarkIcon(card.mark) },
                        onToggleHear = { send(DoodleKingMatchIntent.ToggleHear(card.seat)) },
                        onLongPress = { send(DoodleKingMatchIntent.PlayerLongPressed(card.seat)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            state.myCard?.let { card ->
                DoodleMyCard(
                    card = card,
                    tint = seatTint(card.seat),
                    word = state.myWord,
                    wordGuessed = state.myWordGuessed,
                    micOn = state.micOn,
                    micLocked = state.micLocked,
                    onToggleMic = { send(DoodleKingMatchIntent.ToggleMic(micGranted())) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Column(modifier = Modifier.weight(1f).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
            DoodleStatusPill(status = state.status, timeLeft = state.timeLeft, urgent = state.timeUrgent, seatTint = seatTint)
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                DoodleCanvasView(board = state.board, canDraw = state.barMode == DoodleBarMode.DRAW, modifier = Modifier.fillMaxHeight())
                state.reveal?.let { DoodleRevealCard(reveal = it, seatTint = seatTint) }
            }
            if (state.spectating) {
                TableSpectatorVoiceButtons(
                    micOn = state.micOn,
                    speakerOn = state.speakerOn,
                    onToggleMic = { send(DoodleKingMatchIntent.ToggleMic(micGranted())) },
                    onToggleSpeaker = { send(DoodleKingMatchIntent.ToggleSpeaker) },
                )
            } else {
                controls()
            }
        }
        Column(modifier = Modifier.width(dimens.doodleSideWidth).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
            DoodleRankPanel(rows = state.ranks, seatTint = seatTint, modifier = Modifier.weight(1f).fillMaxWidth())
            DoodleFeedPanel(rows = state.feed, seatTint = seatTint, modifier = Modifier.weight(FEED_WEIGHT).fillMaxWidth())
        }
    }
}

/**
 * While typing: the live drawing on the left with the time centred under it; the turn's guesses
 * on the right, as tall as the drawing, with the answer to the last guess centred under them.
 */
@Composable
private fun TypingStrip(state: DoodleKingMatchState, seatTint: (Int) -> Color, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
        Column(modifier = Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
            DoodleCanvasView(board = state.board, canDraw = false, modifier = Modifier.weight(1f))
            TimeChip(timeLeft = state.timeLeft, urgent = state.timeUrgent)
        }
        Column(modifier = Modifier.weight(1f).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
            DoodleFeedPanel(rows = state.feed, seatTint = seatTint, modifier = Modifier.weight(1f).fillMaxWidth())
            Box(modifier = Modifier.height(YapNPlayTheme.dimens.doodleStatusHeight), contentAlignment = Alignment.Center) {
                DoodleGuessFeedback(state)
            }
        }
    }
}

@Composable
private fun TimeChip(timeLeft: String, urgent: Boolean) {
    val colors = YapNPlayTheme.colors
    Row(
        modifier = Modifier
            .height(YapNPlayTheme.dimens.doodleStatusHeight)
            .arenaGlass(RoundedCornerShape(YapNPlayTheme.dimens.ludoGlossRadius))
            .padding(horizontal = Spacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        AppIcon(imageVector = Icons.Rounded.Schedule, tint = if (urgent) colors.Error else colors.TextPrimary, size = YapNPlayTheme.dimens.iconSmall)
        BasicText(text = timeLeft, style = YapNPlayTheme.typography.label.copy(color = if (urgent) colors.Error else colors.TextPrimary))
    }
}

@Composable
private fun MicPermissionCard(send: (DoodleKingMatchIntent) -> Unit) {
    val colors = YapNPlayTheme.colors
    ModalCard(
        title = Strings.micPermissionTitle,
        subtitle = Strings.micPermissionBody,
        onClose = { send(DoodleKingMatchIntent.DismissModal) },
        actions = {
            PillButton(
                text = Strings.micPermissionOpenSettings,
                onClick = { send(DoodleKingMatchIntent.OpenAppSettings) },
                gradient = listOf(colors.CtaGreenStart, colors.CtaGreenEnd),
                contentColor = colors.CtaGreenContent,
                trailingIcon = null,
            )
        },
    )
}

/** The guesses take a little more of the right column than the leaderboard. */
private const val FEED_WEIGHT = 1.3f
