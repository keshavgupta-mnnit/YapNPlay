package com.kglabs28.yapnplay.appengine.features.playrooms.data.remote

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.features.playrooms.data.toDomain
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.JoinOutcome
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoom
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoomPreview

/**
 * Network side of rooms data: calls the API, maps DTOs to domain,
 * converts any failure into a typed error. Never throws.
 */
class PlayRoomsRemoteManager(private val api: PlayRoomsApi) {

    suspend fun fetchMyRooms(): AppResult<List<PlayRoom>> = try {
        AppResult.Success(api.getMyRooms().rooms.map { it.toDomain() })
    } catch (e: Exception) {
        CrashReporter.record(e, "PlayRoomsRemoteManager.fetchMyRooms")
        AppResult.Failure(YapNPlayError.Network("rooms fetch failed"))
    }

    suspend fun previewRoom(code: String): AppResult<PlayRoomPreview> = try {
        AppResult.Success(api.previewRoom(code).toDomain())
    } catch (e: Exception) {
        CrashReporter.record(e, "PlayRoomsRemoteManager.previewRoom")
        AppResult.Failure(YapNPlayError.NotFound("room"))
    }

    suspend fun joinRoom(code: String): AppResult<JoinOutcome> = try {
        AppResult.Success(api.joinRoom(code).toDomain())
    } catch (e: Exception) {
        CrashReporter.record(e, "PlayRoomsRemoteManager.joinRoom")
        AppResult.Failure(YapNPlayError.Network("room join failed"))
    }
}
