package com.kglabs28.yapnplay.appengine.features.playrooms.usecase

import com.kglabs28.yapnplay.appengine.appmanager.AppManager
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.JoinOutcome
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoom
import com.kglabs28.yapnplay.appengine.features.playrooms.repository.PlayRoomsRepository

/**
 * Joins a play room by its code, checking what the app can know up front:
 * the code has exactly [AppConstants.ROOM_CODE_LENGTH] digits; a room the
 * user already belongs to just opens; a new room must not exceed the
 * remote room limit ([AppManager.maxRoomsPerUser]). Joining someone
 * else's room sends a request the admin approves. The server checks
 * the same rules again — it has the final say.
 */
class JoinPlayRoomByCodeUseCase(
    private val repository: PlayRoomsRepository,
    private val appManager: AppManager,
) {

    suspend operator fun invoke(code: String, joinedRooms: List<PlayRoom>): AppResult<JoinOutcome> {
        val trimmed = code.trim()
        if (trimmed.length != AppConstants.ROOM_CODE_LENGTH || !trimmed.all { it.isDigit() }) {
            return AppResult.Failure(YapNPlayError.Validation(FIELD_ROOM_CODE))
        }
        joinedRooms.firstOrNull { it.code == trimmed }?.let { return AppResult.Success(JoinOutcome.Joined(it)) }
        val limit = appManager.maxRoomsPerUser()
        if (joinedRooms.size >= limit) {
            return AppResult.Failure(YapNPlayError.LimitReached(limit))
        }
        return repository.joinRoom(trimmed)
    }

    companion object {
        const val FIELD_ROOM_CODE = "room_code"
    }
}
