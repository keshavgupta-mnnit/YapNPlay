package com.kglabs28.yapnplay.appengine.ui.screens.playrooms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import androidx.compose.material.icons.rounded.HourglassTop
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.input.pointer.pointerInput
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockBackPress
import com.kglabs28.yapnplay.appengine.ui.components.playrooms.RoomCodeKeypad
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay
import com.kglabs28.yapnplay.appengine.ui.components.playrooms.JoinRoomPromptCard
import com.kglabs28.yapnplay.appengine.ui.components.playrooms.CreateRoomHeroCard
import com.kglabs28.yapnplay.appengine.ui.components.playrooms.JoinRoomCard
import com.kglabs28.yapnplay.appengine.ui.components.playrooms.OnlineFriendsPanel
import com.kglabs28.yapnplay.appengine.ui.components.playrooms.YourRoomsPanel

/**
 * Rooms tab, split 60:40: create + join on the left, the user's rooms
 * and online friends on the right. Renders inside the shared tab frame.
 */
@Composable
fun PlayRoomsScreen(
    onOpenRoom: (String) -> Unit,
    onCreateRoom: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayRoomsViewModel? = null,
    inviteCode: String? = null,
    onInviteConsumed: () -> Unit = {},
) {
    val state = viewModel?.state?.collectAsState()?.value ?: PlayRoomsState()
    // An invite link was opened: ask "Join this room?" for its code, once.
    LaunchedEffect(inviteCode, viewModel) {
        if (inviteCode != null && viewModel != null) {
            viewModel.onIntent(PlayRoomsIntent.PreviewInvite(inviteCode))
            onInviteConsumed()
        }
    }
    LaunchedEffect(viewModel) {
        viewModel?.onIntent(PlayRoomsIntent.Load)
        viewModel?.effects?.collect { effect ->
            when (effect) {
                is PlayRoomsEffect.OpenRoom -> onOpenRoom(effect.roomId)
                PlayRoomsEffect.OpenCreateRoom -> onCreateRoom()
                PlayRoomsEffect.OpenAllRooms -> Unit
            }
        }
    }

    // Keypad open: a tap anywhere outside the code card and keypad (or
    // Back) hides it and brings rooms + friends back.
    val keypadOpen = state.keypadOpen
    BlockBackPress(enabled = keypadOpen, onBack = { viewModel?.onIntent(PlayRoomsIntent.CloseKeypad) })
    Row(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(keypadOpen) {
                if (!keypadOpen) return@pointerInput
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    if (!down.isConsumed) viewModel?.onIntent(PlayRoomsIntent.CloseKeypad)
                }
            },
        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        Column(
            modifier = Modifier
                .weight(AppConstants.ROOMS_LEFT_COLUMN_WEIGHT)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
        ) {
            CreateRoomHeroCard(
                onCreateRoom = { viewModel?.onIntent(PlayRoomsIntent.CreateRoom) },
                modifier = Modifier
                    .weight(AppConstants.ROOMS_CREATE_CARD_WEIGHT)
                    .fillMaxWidth(),
            )
            JoinRoomCard(
                code = state.roomCode,
                isJoining = state.isJoining,
                errorMessage = state.joinError,
                keypadOpen = state.keypadOpen,
                onOpenKeypad = { viewModel?.onIntent(PlayRoomsIntent.OpenKeypad) },
                onJoin = { viewModel?.onIntent(PlayRoomsIntent.JoinRoom) },
                modifier = Modifier
                    .weight(AppConstants.ROOMS_JOIN_CARD_WEIGHT)
                    .fillMaxWidth()
                    .keepsKeypadOpen(),
            )
        }
        if (state.keypadOpen) {
            RoomCodeKeypad(
                onDigit = { viewModel?.onIntent(PlayRoomsIntent.KeypadDigit(it)) },
                onBackspace = { viewModel?.onIntent(PlayRoomsIntent.KeypadBackspace) },
                onJoin = { viewModel?.onIntent(PlayRoomsIntent.JoinRoom) },
                onClose = { viewModel?.onIntent(PlayRoomsIntent.CloseKeypad) },
                joinEnabled = state.roomCode.length == AppConstants.ROOM_CODE_LENGTH && !state.isJoining,
                modifier = Modifier
                    .weight(AppConstants.ROOMS_RIGHT_COLUMN_WEIGHT)
                    .fillMaxHeight()
                    .keepsKeypadOpen(),
            )
        } else Column(
            modifier = Modifier
                .weight(AppConstants.ROOMS_RIGHT_COLUMN_WEIGHT)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
        ) {
            YourRoomsPanel(
                rooms = state.rooms,
                loaded = state.loaded,
                onOpenRoom = { viewModel?.onIntent(PlayRoomsIntent.OpenRoom(it)) },
                onSeeAll = { viewModel?.onIntent(PlayRoomsIntent.SeeAllRooms) },
                modifier = Modifier
                    .weight(AppConstants.ROOMS_LIST_PANEL_WEIGHT)
                    .fillMaxWidth(),
            )
            OnlineFriendsPanel(
                friends = state.onlineFriends,
                loaded = state.loaded,
                modifier = Modifier
                    .weight(AppConstants.ROOMS_FRIENDS_PANEL_WEIGHT)
                    .fillMaxWidth(),
            )
        }
    }

    val requestSent = state.requestSent
    BlockingOverlay(
        visible = requestSent != null,
        onDismiss = { viewModel?.onIntent(PlayRoomsIntent.DismissRequestSent) },
    ) {
        if (requestSent != null) {
            ModalCard(
                title = requestSent.title,
                subtitle = requestSent.body,
                onClose = { viewModel?.onIntent(PlayRoomsIntent.DismissRequestSent) },
                actions = {
                    GlassPillButton(
                        text = Strings.roomsRequestSentOk,
                        onClick = { viewModel?.onIntent(PlayRoomsIntent.DismissRequestSent) },
                        leadingIcon = Icons.Rounded.HourglassTop,
                        height = YapNPlayTheme.dimens.ctaHeight,
                    )
                },
            )
        }
    }

    val prompt = state.joinPrompt
    BlockingOverlay(visible = prompt != null) {
        if (prompt != null) {
            JoinRoomPromptCard(
                title = prompt.title,
                details = prompt.details,
                description = prompt.description,
                onJoin = { viewModel?.onIntent(PlayRoomsIntent.ConfirmJoin) },
                onCancel = { viewModel?.onIntent(PlayRoomsIntent.DismissJoinPrompt) },
            )
        }
    }
}

/** Touches here don't count as "outside", so the keypad stays open. */
private fun Modifier.keepsKeypadOpen(): Modifier = pointerInput(Unit) {
    awaitEachGesture { awaitFirstDown(requireUnconsumed = false).consume() }
}
