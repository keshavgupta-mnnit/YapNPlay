package com.kglabs28.yapnplay.appengine.features.playrooms.data.remote

import com.kglabs28.yapnplay.appengine.features.playrooms.data.remote.dto.PlayRoomDto

/**
 * Rooms endpoints. Shapes match docs/backend-api-contract.md.
 */
interface PlayRoomsApi {
    suspend fun getMyRooms(): List<PlayRoomDto>
    suspend fun joinRoom(code: String): PlayRoomDto
}
