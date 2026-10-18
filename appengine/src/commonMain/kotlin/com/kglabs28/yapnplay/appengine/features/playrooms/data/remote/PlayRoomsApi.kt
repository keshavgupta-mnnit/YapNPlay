package com.kglabs28.yapnplay.appengine.features.playrooms.data.remote

import com.kglabs28.yapnplay.appengine.protocol.MyRooms
import com.kglabs28.yapnplay.appengine.protocol.JoinRoomResult
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
     * message `JoinRoom(code)`: a non-member gets PENDING_APPROVAL (the
     * admin approves from the Waiting List), a member gets JOINED.
     */
    suspend fun joinRoom(code: String): JoinRoomResult

    /** WebSocket `CreateRoom(name?)`: the new room's id (the user is its member, leader and admin). */
    suspend fun createRoom(name: String?): String
}
