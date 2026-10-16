package com.kglabs28.yapnplay.appengine.features.playrooms.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoom
import com.kglabs28.yapnplay.appengine.features.playrooms.repository.PlayRoomsRepository

/**
 * Joins a room by its code. Rejects anything that is not exactly
 * [AppConstants.ROOM_CODE_LENGTH] characters before touching the network.
 */
class JoinPlayRoomByCodeUseCase(private val repository: PlayRoomsRepository) {

    suspend operator fun invoke(code: String): AppResult<PlayRoom> {
        val trimmed = code.trim()
        if (trimmed.length != AppConstants.ROOM_CODE_LENGTH) {
            return AppResult.Failure(YapNPlayError.Validation("room_code"))
        }
        return repository.joinRoom(trimmed)
    }
}
