package com.kglabs28.yapnplay.appengine.ui.screens.playrooms

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.TextUtils
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.GetPlayRoomsScreenDataUseCase
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.JoinPlayRoomByCodeUseCase

/**
 * Rooms tab: loads rooms + online friends once, owns the join-code
 * field, and turns taps into navigation effects.
 */
class PlayRoomsViewModel(
    private val getPlayRoomsScreenData: GetPlayRoomsScreenDataUseCase,
    private val joinPlayRoomByCode: JoinPlayRoomByCodeUseCase,
    dispatchers: DispatcherProvider,
) : MviViewModel<PlayRoomsState, PlayRoomsIntent, PlayRoomsEffect>(PlayRoomsState(), dispatchers) {

    override suspend fun handleIntent(intent: PlayRoomsIntent) {
        when (intent) {
            PlayRoomsIntent.Load -> load()
            is PlayRoomsIntent.RoomCodeChanged -> setState {
                copy(
                    roomCode = TextUtils.roomCode(intent.code, AppConstants.ROOM_CODE_LENGTH),
                    joinError = false,
                )
            }
            PlayRoomsIntent.JoinRoom -> join()
            PlayRoomsIntent.CreateRoom -> emitEffect(PlayRoomsEffect.OpenCreateRoom)
            is PlayRoomsIntent.OpenRoom -> emitEffect(PlayRoomsEffect.OpenRoom(intent.roomId))
            PlayRoomsIntent.SeeAllRooms -> emitEffect(PlayRoomsEffect.OpenAllRooms)
        }
    }

    private suspend fun load() {
        val data = getPlayRoomsScreenData()
        setState {
            copy(rooms = data.rooms, onlineFriends = data.onlineFriends, loaded = true)
        }
    }

    private suspend fun join() {
        if (state.value.isJoining) return
        setState { copy(isJoining = true, joinError = false) }
        when (val result = joinPlayRoomByCode(state.value.roomCode)) {
            is AppResult.Success -> {
                val room = result.data
                setState {
                    copy(
                        rooms = listOf(room) + rooms.filterNot { it.id == room.id },
                        roomCode = "",
                        isJoining = false,
                    )
                }
                emitEffect(PlayRoomsEffect.OpenRoom(room.id))
            }
            is AppResult.Failure -> setState { copy(isJoining = false, joinError = true) }
        }
    }
}
