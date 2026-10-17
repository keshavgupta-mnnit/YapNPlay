package com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.revealWhen
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppPermission
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.rememberPermissionRequester
import com.kglabs28.yapnplay.appengine.ui.components.common.rememberShareSheet
import com.kglabs28.yapnplay.appengine.ui.components.games.GameStage
import com.kglabs28.yapnplay.appengine.ui.components.lobby.HouseRulesPanel
import com.kglabs28.yapnplay.appengine.ui.components.lobby.LobbyGamePicker
import com.kglabs28.yapnplay.appengine.ui.components.lobby.LobbyMemberList
import com.kglabs28.yapnplay.appengine.ui.components.lobby.LobbyModalContent
import com.kglabs28.yapnplay.appengine.ui.components.lobby.LobbyPrimaryButton
import com.kglabs28.yapnplay.appengine.ui.components.lobby.LobbyTopBar
import com.kglabs28.yapnplay.appengine.ui.components.lobby.LobbyVoiceToggle
import com.kglabs28.yapnplay.appengine.ui.placeholders.CozyHomeBackground

/**
 * Play-room lobby on the frosted background. Same top bar, member list
 * and bottom bar in both phases; the right side shows the game picker
 * until the leader selects a game, then the game art plus its house
 * rules (and Change Game for the leader), full height. START/READY,
 * Mic and Speaker sit under the member list.
 * Menus, the rule sheet, leave confirmation and the countdown are
 * blocking modals.
 */
@Composable
fun PlayRoomLobbyScreen(
    roomId: String,
    onClose: () -> Unit,
    onOpenMatch: (gameId: String, matchId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayRoomLobbyViewModel? = null,
) {
    val state = viewModel?.state?.collectAsState()?.value ?: PlayRoomLobbyState()
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val send: (PlayRoomLobbyIntent) -> Unit = { viewModel?.onIntent(it) }
    val micPermission = rememberPermissionRequester(AppPermission.MICROPHONE)
    val share = rememberShareSheet()
    LaunchedEffect(viewModel, roomId) {
        viewModel?.onIntent(PlayRoomLobbyIntent.Load(roomId))
        viewModel?.effects?.collect { effect ->
            when (effect) {
                PlayRoomLobbyEffect.Close -> onClose()
                is PlayRoomLobbyEffect.OpenMatch -> onOpenMatch(effect.gameId, effect.matchId)
                PlayRoomLobbyEffect.RequestMicPermission ->
                    micPermission.request { send(PlayRoomLobbyIntent.MicPermissionResult(it)) }
                PlayRoomLobbyEffect.OpenAppSettings -> micPermission.openSettings()
                is PlayRoomLobbyEffect.ShareText -> share(effect.text)
            }
        }
    }

    // Started/stopped covers both leaving the screen and the app going to the background.
    LifecycleStartEffect(viewModel) {
        send(PlayRoomLobbyIntent.ScreenVisible(true))
        onStopOrDispose { send(PlayRoomLobbyIntent.ScreenVisible(false)) }
    }

    // House-rule description that slides; tapping it sets this.
    var slidingRule by remember { mutableStateOf<String?>(null) }
    CozyHomeBackground(
        // Any touch first stops the sliding description (a tap on a
        // description then starts it again when the finger lifts).
        modifier = modifier.pointerInput(Unit) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                slidingRule = null
            }
        },
        frosted = true,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                // Full screen: start at the edge, no camera-cutout gap.
                .padding(horizontal = Spacing.Md, vertical = Spacing.Sm)
                .revealWhen(state.loaded),
            verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
        ) {
            LobbyTopBar(
                roomName = state.roomName,
                description = state.roomDescription,
                roomIcon = state.roomIcon,
                showWaitingList = state.isAdmin,
                onBack = { send(PlayRoomLobbyIntent.Back) },
                onShareLink = { send(PlayRoomLobbyIntent.ShareLink) },
                onWaitingList = { send(PlayRoomLobbyIntent.OpenWaitingList) },
                onLeaderboard = { send(PlayRoomLobbyIntent.OpenLeaderboard) },
                onLeave = { send(PlayRoomLobbyIntent.LeaveClicked) },
            )

            // Left: members, then START + Mic + Speaker under them. Right:
            // the game area, full height down to the bottom.
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
            ) {
                Column(
                    modifier = Modifier
                        .weight(AppConstants.LOBBY_MEMBERS_WEIGHT)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
                ) {
                    LobbyMemberList(
                        members = state.members,
                        countLabel = state.membersLabel,
                        onMemberClick = { send(PlayRoomLobbyIntent.MemberClicked(it)) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        LobbyPrimaryButton(
                            button = state.primary,
                            onClick = { send(PlayRoomLobbyIntent.PrimaryAction) },
                            modifier = Modifier.weight(1f),
                        )
                        LobbyVoiceToggle(
                            icon = Icons.Rounded.Mic,
                            label = Strings.lobbyMic,
                            on = state.micOn,
                            accent = colors.Pink,
                            onToggle = { send(PlayRoomLobbyIntent.ToggleMic(micPermission.isGranted())) },
                        )
                        LobbyVoiceToggle(
                            icon = Icons.AutoMirrored.Rounded.VolumeUp,
                            label = Strings.lobbySpeaker,
                            on = state.speakerOn,
                            accent = colors.Cyan,
                            onToggle = { send(PlayRoomLobbyIntent.ToggleSpeaker) },
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .weight(1f - AppConstants.LOBBY_MEMBERS_WEIGHT)
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
                ) {
                    val game = state.selectedGame
                    if (game == null) {
                        LobbyGamePicker(
                            games = state.games,
                            focusedIndex = state.focusedIndex,
                            viewMode = state.viewMode,
                            canSelect = state.isLeader,
                            onFocusChange = { send(PlayRoomLobbyIntent.FocusGame(it)) },
                            onSelect = { send(PlayRoomLobbyIntent.SelectGame(it)) },
                            onToggleViewMode = { send(PlayRoomLobbyIntent.ToggleViewMode) },
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        GameStage(
                            game = game,
                            modifier = Modifier
                                .weight(AppConstants.LOBBY_GAME_WEIGHT)
                                .fillMaxHeight(),
                        )
                        Column(
                            modifier = Modifier
                                .weight(AppConstants.LOBBY_RULES_WEIGHT)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
                        ) {
                            HouseRulesPanel(
                                rules = state.houseRules,
                                editable = state.isLeader,
                                onToggle = { send(PlayRoomLobbyIntent.ToggleHouseRule(it)) },
                                onShowRuleSheet = { send(PlayRoomLobbyIntent.ShowRuleSheet) },
                                slidingRuleKey = slidingRule,
                                onSlideRule = { slidingRule = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                            )
                            if (state.isLeader) {
                                // Belongs to the game, so game-gold rather than menu purple.
                                GlassPillButton(
                                    text = Strings.lobbyChangeGame,
                                    onClick = { send(PlayRoomLobbyIntent.ChangeGame) },
                                    leadingIcon = Icons.Rounded.SwapHoriz,
                                    accent = colors.StartButtonGlow,
                                    contentColor = colors.StartButtonSub,
                                    height = dimens.ctaHeight,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                }
            }
        }

        val modal = state.modal
        // Menus, the rule sheet and confirmations close on a tap outside;
        // leave, mic permission and the countdown need their buttons.
        val dismissable = modal is LobbyModal.MemberMenu ||
            modal is LobbyModal.ConfirmMemberAction ||
            modal == LobbyModal.RuleSheet
        BlockingOverlay(
            visible = modal != null,
            onDismiss = if (dismissable) ({ send(PlayRoomLobbyIntent.DismissModal) }) else null,
        ) {
            if (modal != null) {
                LobbyModalContent(
                    modal = modal,
                    gameName = state.selectedGame?.name.orEmpty(),
                    fixedRules = state.fixedRules,
                    onMenuOption = { userId, option -> send(PlayRoomLobbyIntent.MenuOptionChosen(userId, option)) },
                    onConfirmMemberAction = { send(PlayRoomLobbyIntent.ConfirmMemberAction) },
                    onConfirmLeave = { send(PlayRoomLobbyIntent.ConfirmLeave) },
                    onCancelCountdown = { send(PlayRoomLobbyIntent.CancelCountdown) },
                    onOpenSettings = { send(PlayRoomLobbyIntent.OpenAppSettings) },
                    onDismiss = { send(PlayRoomLobbyIntent.DismissModal) },
                )
            }
        }
    }
}
