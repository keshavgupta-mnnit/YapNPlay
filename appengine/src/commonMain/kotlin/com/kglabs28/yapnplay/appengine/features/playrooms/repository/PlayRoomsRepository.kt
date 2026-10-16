package com.kglabs28.yapnplay.appengine.features.playrooms.repository

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoom

/**
 * The rooms the user belongs to, and joining one by code. Implemented by
 * PlayRoomsRepositoryImpl over remote + local managers; a backend
 * implementation replaces the API client without touching callers.
 */
interface PlayRoomsRepository {
    suspend fun getMyRooms(): AppResult<List<PlayRoom>>
    suspend fun joinRoom(code: String): AppResult<PlayRoom>
}
