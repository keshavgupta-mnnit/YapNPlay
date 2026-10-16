package com.kglabs28.yapnplay.appengine.features.playrooms.data.remote

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.features.playrooms.data.toDomain
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoom

/**
 * Network side of rooms data: calls the API, maps DTOs to domain,
 * converts any failure into a typed error. Never throws.
 */
class PlayRoomsRemoteManager(private val api: PlayRoomsApi) {

    suspend fun fetchMyRooms(): AppResult<List<PlayRoom>> = try {
        AppResult.Success(api.getMyRooms().map { it.toDomain() })
    } catch (e: Exception) {
        CrashReporter.record(e, "PlayRoomsRemoteManager.fetchMyRooms")
        AppResult.Failure(YapNPlayError.Network("rooms fetch failed"))
    }

    suspend fun joinRoom(code: String): AppResult<PlayRoom> = try {
        AppResult.Success(api.joinRoom(code).toDomain())
    } catch (e: Exception) {
        CrashReporter.record(e, "PlayRoomsRemoteManager.joinRoom")
        AppResult.Failure(YapNPlayError.Network("room join failed"))
    }
}
