package com.kglabs28.yapnplay.appengine.features.playrooms.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoomPreview
import com.kglabs28.yapnplay.appengine.features.playrooms.repository.PlayRoomsRepository

/**
 * Looks a room up by code before joining, for the "Join this room?"
 * dialog (invite link or typed code). Bad codes fail before the network.
 */
class PreviewPlayRoomUseCase(private val repository: PlayRoomsRepository) {

    suspend operator fun invoke(code: String): AppResult<PlayRoomPreview> {
        val trimmed = code.trim()
        if (trimmed.length != AppConstants.ROOM_CODE_LENGTH || !trimmed.all { it.isDigit() }) {
            return AppResult.Failure(YapNPlayError.Validation(JoinPlayRoomByCodeUseCase.FIELD_ROOM_CODE))
        }
        return repository.previewRoom(trimmed)
    }
}
