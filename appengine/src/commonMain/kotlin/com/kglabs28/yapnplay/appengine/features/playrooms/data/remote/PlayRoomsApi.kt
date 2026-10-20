package com.kglabs28.yapnplay.appengine.features.playrooms.data.remote

import com.kglabs28.yapnplay.appengine.protocol.CreateRoom
import com.kglabs28.yapnplay.appengine.protocol.InviteToRoomBody
import com.kglabs28.yapnplay.appengine.protocol.InviteToRoomReply
import com.kglabs28.yapnplay.appengine.protocol.MyRooms
import com.kglabs28.yapnplay.appengine.protocol.RoomInfo
import com.kglabs28.yapnplay.appengine.protocol.RoomInvites
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

    /**
     * WebSocket `CreateRoom(name?, icon?, inviteUserIds)`: the new room (the
     * user is its member, leader and admin); the friends get invites.
     */
    suspend fun createRoom(body: CreateRoom): RoomInfo

    /** `POST /rooms/{roomId}/invites` */
    suspend fun inviteToRoom(roomId: String, body: InviteToRoomBody): InviteToRoomReply

    /** `GET /rooms/invites` */
    suspend fun getRoomInvites(): RoomInvites

    /** `POST /rooms/invites/{inviteId}/accept` */
    suspend fun acceptRoomInvite(inviteId: String): RoomSummary

    /** `POST /rooms/invites/{inviteId}/decline` */
    suspend fun declineRoomInvite(inviteId: String)
}
