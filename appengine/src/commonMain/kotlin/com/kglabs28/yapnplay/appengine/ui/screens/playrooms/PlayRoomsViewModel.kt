package com.kglabs28.yapnplay.appengine.ui.screens.playrooms

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.TextUtils
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.GetPlayRoomsScreenDataUseCase
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.JoinPlayRoomByCodeUseCase
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.PreviewPlayRoomUseCase

/**
 * Rooms tab: loads rooms + online friends once, owns the join-code
 * field, and turns taps into navigation effects.
 */
class PlayRoomsViewModel(
    private val getPlayRoomsScreenData: GetPlayRoomsScreenDataUseCase,
    private val joinPlayRoomByCode: JoinPlayRoomByCodeUseCase,
    private val previewPlayRoom: PreviewPlayRoomUseCase,
    dispatchers: DispatcherProvider,
) : MviViewModel<PlayRoomsState, PlayRoomsIntent, PlayRoomsEffect>(PlayRoomsState(), dispatchers) {

    override suspend fun handleIntent(intent: PlayRoomsIntent) {
        when (intent) {
            PlayRoomsIntent.Load -> load()
            is PlayRoomsIntent.RoomCodeChanged -> setState {
                copy(
                    roomCode = TextUtils.roomCode(intent.code, AppConstants.ROOM_CODE_LENGTH),
                    joinError = null,
                )
            }
            PlayRoomsIntent.JoinRoom -> preview(state.value.roomCode)
            is PlayRoomsIntent.PreviewInvite -> {
                val code = TextUtils.roomCode(intent.code, AppConstants.ROOM_CODE_LENGTH)
                setState { copy(roomCode = code, joinError = null) }
                preview(code)
            }
            PlayRoomsIntent.ConfirmJoin -> state.value.joinPrompt?.let {
                setState { copy(joinPrompt = null) }
                join(it.code)
            }
            PlayRoomsIntent.DismissJoinPrompt -> setState { copy(joinPrompt = null) }
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

    /**
     * Looks the code up and asks "Join this room?". A room you already
     * belong to just opens.
     */
    private suspend fun preview(code: String) {
        if (state.value.isJoining) return
        state.value.rooms.firstOrNull { it.code == code }?.let {
            emitEffect(PlayRoomsEffect.OpenRoom(it.id))
            return
        }
        setState { copy(isJoining = true, joinError = null) }
        when (val result = previewPlayRoom(code)) {
            is AppResult.Success -> {
                val room = result.data
                setState {
                    copy(
                        isJoining = false,
                        joinPrompt = JoinPromptUi(
                            code = room.code,
                            title = Strings.roomsJoinPromptTitle(room.name),
                            details = Strings.roomsJoinPromptDetails(room.memberCount, room.onlineCount),
                            description = room.description,
                        ),
                    )
                }
            }
            is AppResult.Failure -> setState { copy(isJoining = false, joinError = errorMessage(result.error)) }
        }
    }

    private suspend fun join(code: String) {
        if (state.value.isJoining) return
        setState { copy(isJoining = true, joinError = null) }
        when (val result = joinPlayRoomByCode(code, state.value.rooms)) {
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
            is AppResult.Failure -> setState { copy(isJoining = false, joinError = errorMessage(result.error)) }
        }
    }

    private fun errorMessage(error: YapNPlayError): String = when (error) {
        is YapNPlayError.Validation -> Strings.roomsJoinInvalidCode(AppConstants.ROOM_CODE_LENGTH)
        is YapNPlayError.LimitReached -> Strings.roomsJoinLimit(error.limit)
        is YapNPlayError.NotFound -> Strings.roomsJoinNotFound
        else -> Strings.roomsJoinFailed
    }
}
