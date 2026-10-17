package com.kglabs28.yapnplay.appengine.features.playrooms.data.remote

import com.kglabs28.yapnplay.appengine.protocol.MyRooms
import com.kglabs28.yapnplay.appengine.protocol.RoomPreview
import com.kglabs28.yapnplay.appengine.protocol.RoomSummary

/**
 * Play-room calls. Bodies are the shared contract types in `protocol/`
 * (see docs/backend-api-contract.md).
 */
interface PlayRoomsApi {
    /** `GET /rooms/mine` */
    suspend fun getMyRooms(): MyRooms

    /** `GET /rooms/preview/{code}`: what the room is before joining. */
    suspend fun previewRoom(code: String): RoomPreview

    /**
     * Join by code. In production this is the WebSocket room-channel
     * message `JoinRoom(code)`, answered by a `RoomSnapshot`.
     */
    suspend fun joinRoom(code: String): RoomSummary
}
