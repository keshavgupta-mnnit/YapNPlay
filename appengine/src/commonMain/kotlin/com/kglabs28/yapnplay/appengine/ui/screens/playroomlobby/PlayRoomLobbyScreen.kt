package com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MeetingRoom
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Speaker
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.revealWhen
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppPermission
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay
import com.kglabs28.yapnplay.appengine.ui.components.common.rememberPermissionRequester
import com.kglabs28.yapnplay.appengine.ui.components.common.rememberShareSheet
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton
import com.kglabs28.yapnplay.appengine.ui.components.games.GameShowcaseCard
import com.kglabs28.yapnplay.appengine.ui.components.lobby.HouseRulesPanel
import com.kglabs28.yapnplay.appengine.ui.components.lobby.LobbyGamePicker
import com.kglabs28.yapnplay.appengine.ui.components.lobby.LobbyMemberList
import com.kglabs28.yapnplay.appengine.ui.components.lobby.LobbyModalContent
import com.kglabs28.yapnplay.appengine.ui.components.lobby.LobbyPrimaryButton
import com.kglabs28.yapnplay.appengine.ui.components.lobby.LobbyTopBar
import com.kglabs28.yapnplay.appengine.ui.components.lobby.VoiceToggleCard
import com.kglabs28.yapnplay.appengine.ui.placeholders.CozyHomeBackground

/**
 * Play-room lobby. Same top bar, member list and bottom bar in both
 * phases; the middle shows the game picker until the leader selects a
 * game, then the game art plus its house rules (and Change Game for the
 * leader). Menus, the rule sheet, leave confirmation and the countdown
 * are blocking modals over the frosted app.
 */
@Composable
fun PlayRoomLobbyScreen(
    roomId: String,
    onClose: () -> Unit,
    onOpenMatch: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayRoomLobbyViewModel? = null,
) {
    val state = viewModel?.state?.collectAsState()?.value ?: PlayRoomLobbyState()
    val colors = YapNPlayTheme.colors
    val send: (PlayRoomLobbyIntent) -> Unit = { viewModel?.onIntent(it) }
    val micPermission = rememberPermissionRequester(AppPermission.MICROPHONE)
    val share = rememberShareSheet()
    LaunchedEffect(viewModel, roomId) {
        viewModel?.onIntent(PlayRoomLobbyIntent.Load(roomId))
        viewModel?.effects?.collect { effect ->
            when (effect) {
                PlayRoomLobbyEffect.Close -> onClose()
                is PlayRoomLobbyEffect.OpenMatch -> onOpenMatch()
                PlayRoomLobbyEffect.RequestMicPermission ->
                    micPermission.request { send(PlayRoomLobbyIntent.MicPermissionResult(it)) }
                PlayRoomLobbyEffect.OpenAppSettings -> micPermission.openSettings()
                is PlayRoomLobbyEffect.ShareText -> share(effect.text)
            }
        }
    }

    CozyHomeBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                .padding(horizontal = Spacing.Md, vertical = Spacing.Sm)
                .revealWhen(state.loaded),
            verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
        ) {
            LobbyTopBar(
                roomName = state.roomName,
                description = state.roomDescription,
                showWaitingList = state.isAdmin,
                onBack = { send(PlayRoomLobbyIntent.Back) },
                onWaitingList = { send(PlayRoomLobbyIntent.OpenWaitingList) },
                onLeaderboard = { send(PlayRoomLobbyIntent.OpenLeaderboard) },
                onShareLink = { send(PlayRoomLobbyIntent.ShareLink) },
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
            ) {
                LobbyMemberList(
                    members = state.members,
                    countLabel = state.membersLabel,
                    onMemberClick = { send(PlayRoomLobbyIntent.MemberClicked(it)) },
                    modifier = Modifier
                        .weight(AppConstants.LOBBY_MEMBERS_WEIGHT)
                        .fillMaxHeight(),
                )
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
                        modifier = Modifier
                            .weight(AppConstants.LOBBY_GAME_WEIGHT + AppConstants.LOBBY_RULES_WEIGHT)
                            .fillMaxHeight(),
                    )
                } else {
                    GameShowcaseCard(
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
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        )
                        if (state.isLeader) {
                            PillButton(
                                text = Strings.lobbyChangeGame,
                                onClick = { send(PlayRoomLobbyIntent.ChangeGame) },
                                gradient = listOf(colors.RoomsSmallButton, colors.RoomsPanel),
                                contentColor = colors.TextPrimary,
                                rimColor = colors.SocialZoneAccent,
                                leadingIcon = Icons.Rounded.SwapHoriz,
                                trailingIcon = null,
                                textStyle = YapNPlayTheme.typography.sectionTitle,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(YapNPlayTheme.dimens.lobbyBarHeight),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
            ) {
                LobbyPrimaryButton(
                    button = state.primary,
                    onClick = { send(PlayRoomLobbyIntent.PrimaryAction) },
                    modifier = Modifier.weight(AppConstants.LOBBY_PRIMARY_WEIGHT),
                )
                VoiceToggleCard(
                    icon = Icons.Rounded.Mic,
                    label = Strings.lobbyMic,
                    on = state.micOn,
                    accent = colors.SocialZoneAccent,
                    onToggle = { send(PlayRoomLobbyIntent.ToggleMic(micPermission.isGranted())) },
                )
                VoiceToggleCard(
                    icon = Icons.Rounded.Speaker,
                    label = Strings.lobbySpeaker,
                    on = state.speakerOn,
                    accent = colors.Cyan,
                    onToggle = { send(PlayRoomLobbyIntent.ToggleSpeaker) },
                )
                Spacer(modifier = Modifier.weight(1f - AppConstants.LOBBY_PRIMARY_WEIGHT))
                PillButton(
                    text = Strings.lobbyLeaveRoom,
                    onClick = { send(PlayRoomLobbyIntent.LeaveClicked) },
                    gradient = listOf(colors.RoomsSmallButton, colors.RoomsPanel),
                    contentColor = colors.Error,
                    rimColor = colors.Error,
                    height = YapNPlayTheme.dimens.lobbyBarHeight,
                    leadingIcon = Icons.Rounded.MeetingRoom,
                    trailingIcon = null,
                    textStyle = YapNPlayTheme.typography.sectionTitle,
                )
            }
        }

        val modal = state.modal
        BlockingOverlay(visible = modal != null) {
            if (modal != null) {
                LobbyModalContent(
                    modal = modal,
                    gameName = state.selectedGame?.name.orEmpty(),
                    fixedRules = state.fixedRules,
                    onMenuOption = { userId, option -> send(PlayRoomLobbyIntent.MenuOptionChosen(userId, option)) },
                    onConfirmLeave = { send(PlayRoomLobbyIntent.ConfirmLeave) },
                    onCancelCountdown = { send(PlayRoomLobbyIntent.CancelCountdown) },
                    onOpenSettings = { send(PlayRoomLobbyIntent.OpenAppSettings) },
                    onDismiss = { send(PlayRoomLobbyIntent.DismissModal) },
                )
            }
        }
    }
}
