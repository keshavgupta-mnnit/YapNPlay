package com.kglabs28.yapnplay.appengine.ui.screens.playrooms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
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
    onOpenRoom: () -> Unit,
    onCreateRoom: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayRoomsViewModel? = null,
) {
    val state = viewModel?.state?.collectAsState()?.value ?: PlayRoomsState()
    LaunchedEffect(viewModel) {
        viewModel?.onIntent(PlayRoomsIntent.Load)
        viewModel?.effects?.collect { effect ->
            when (effect) {
                is PlayRoomsEffect.OpenRoom -> onOpenRoom()
                PlayRoomsEffect.OpenCreateRoom -> onCreateRoom()
                PlayRoomsEffect.OpenAllRooms -> Unit
            }
        }
    }

    Row(
        modifier = modifier.fillMaxSize(),
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
                isError = state.joinError,
                onCodeChange = { viewModel?.onIntent(PlayRoomsIntent.RoomCodeChanged(it)) },
                onJoin = { viewModel?.onIntent(PlayRoomsIntent.JoinRoom) },
                modifier = Modifier
                    .weight(AppConstants.ROOMS_JOIN_CARD_WEIGHT)
                    .fillMaxWidth(),
            )
        }
        Column(
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
}
