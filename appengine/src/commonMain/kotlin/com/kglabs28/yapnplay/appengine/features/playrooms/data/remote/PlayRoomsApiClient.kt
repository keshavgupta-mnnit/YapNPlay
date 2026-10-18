package com.kglabs28.yapnplay.appengine.features.playrooms.data.remote

import com.kglabs28.yapnplay.appengine.core.network.BackendPaths
import com.kglabs28.yapnplay.appengine.core.network.RealtimeConnection
import com.kglabs28.yapnplay.appengine.protocol.CreateRoom
import com.kglabs28.yapnplay.appengine.protocol.JoinRoom
import com.kglabs28.yapnplay.appengine.protocol.JoinRoomResult
import com.kglabs28.yapnplay.appengine.protocol.MyRooms
import com.kglabs28.yapnplay.appengine.protocol.RoomPreview
import com.kglabs28.yapnplay.appengine.protocol.RoomSnapshot
import com.kglabs28.yapnplay.appengine.protocol.transport.Channel
import com.kglabs28.yapnplay.appengine.protocol.transport.MessageType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

/**
 * Play rooms: the list and previews over HTTP; joining and creating over
 * the WebSocket ROOM channel (they change who is present where).
 */
class PlayRoomsApiClient(
    private val client: HttpClient,
    private val realtime: RealtimeConnection,
) : PlayRoomsApi {

    override suspend fun getMyRooms(): MyRooms = client.get(BackendPaths.MY_ROOMS).body()

    override suspend fun previewRoom(code: String): RoomPreview = client.get(BackendPaths.roomPreview(code)).body()

    override suspend fun joinRoom(code: String): JoinRoomResult {
        val reply = realtime.request(Channel.ROOM, MessageType.JOIN_ROOM, JoinRoom.serializer(), JoinRoom(code))
        return realtime.decode(reply, JoinRoomResult.serializer())
    }

    override suspend fun createRoom(name: String?): String {
        val reply = realtime.request(Channel.ROOM, MessageType.CREATE_ROOM, CreateRoom.serializer(), CreateRoom(name))
        return realtime.decode(reply, RoomSnapshot.serializer()).room.id
    }
}
