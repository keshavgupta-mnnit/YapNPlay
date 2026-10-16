package com.kglabs28.yapnplay.appengine.features.playrooms.data.remote

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.FakeDataProvider
import com.kglabs28.yapnplay.appengine.protocol.MyRooms
import com.kglabs28.yapnplay.appengine.protocol.RoomSummary
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.delay

/**
 * Play-rooms client. TODAY: serves from [FakeDataProvider] with a
 * network-like delay so the UI behaves exactly as it will in production.
 *
 * LATER (backend ready): uncomment the real calls below, delete the fake
 * `delay + return` lines. The interface stays, so remote manager,
 * repository, view model and screen are untouched.
 */
class PlayRoomsApiClient(
    private val client: HttpClient,
) : PlayRoomsApi {

    override suspend fun getMyRooms(): MyRooms {
        // --- production path (uncomment when backend is live) ---
        // return client.get(AppConstants.SERVER_BASE_URL + "/rooms/mine").body()

        // --- fake path (delete when switching to production) ---
        delay(FakeDataProvider.NETWORK_DELAY_MS)
        return FakeDataProvider.PlayRooms.myRooms
    }

    override suspend fun joinRoom(code: String): RoomSummary {
        // --- production path (needs the WebSocket connection manager, Phase 12) ---
        // val snapshot = realtime.request<RoomSnapshot>(
        //     channel = Channel.ROOM,
        //     type = MessageType.JOIN_ROOM,
        //     payload = JoinRoom(code = code),
        // )
        // return snapshot.room.toSummary(myUserId)

        // --- fake path (delete when switching to production) ---
        delay(FakeDataProvider.NETWORK_DELAY_MS)
        return FakeDataProvider.PlayRooms.joinedRoom(code)
    }
}
