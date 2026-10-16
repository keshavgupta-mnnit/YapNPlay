package com.kglabs28.yapnplay.appengine.features.playrooms.data.remote

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.FakeDataProvider
import com.kglabs28.yapnplay.appengine.features.playrooms.data.remote.dto.JoinPlayRoomRequestDto
import com.kglabs28.yapnplay.appengine.features.playrooms.data.remote.dto.PlayRoomDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.delay

/**
 * Rooms API client. TODAY: serves from [FakeDataProvider] with a
 * network-like delay so the UI behaves exactly as it will in production.
 *
 * LATER (backend ready): uncomment the real calls below, delete the fake
 * `delay + return` lines. Nothing else changes — the interface signature
 * stays, so remote manager, repository, view model and screen are untouched.
 */
class PlayRoomsApiClient(
    private val client: HttpClient,
) : PlayRoomsApi {

    override suspend fun getMyRooms(): List<PlayRoomDto> {
        // --- production path (uncomment when backend is live) ---
        // return client.get(AppConstants.SERVER_BASE_URL + "/v1/rooms/mine").body()

        // --- fake path (delete when switching to production) ---
        delay(FakeDataProvider.NETWORK_DELAY_MS)
        return FakeDataProvider.PlayRooms.myRooms
    }

    override suspend fun joinRoom(code: String): PlayRoomDto {
        // --- production path (uncomment when backend is live) ---
        // return client.post(AppConstants.SERVER_BASE_URL + "/v1/rooms/join") {
        //     contentType(ContentType.Application.Json)
        //     setBody(JoinPlayRoomRequestDto(code = code))
        // }.body()

        // --- fake path (delete when switching to production) ---
        delay(FakeDataProvider.NETWORK_DELAY_MS)
        return FakeDataProvider.PlayRooms.joinedRoom(code)
    }
}
