package com.kglabs28.yapnplay.appengine.features.playrooms.data.remote

import com.kglabs28.yapnplay.appengine.features.playrooms.data.toCreated
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoomInvite
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.CreatedPlayRoom
import com.kglabs28.yapnplay.appengine.protocol.InviteToRoomBody
import com.kglabs28.yapnplay.appengine.protocol.CreateRoom
import com.kglabs28.yapnplay.appengine.appmanager.AppManagerKeys
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.network.RealtimeRejectedException
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.JoinPlayRoomByCodeUseCase
import com.kglabs28.yapnplay.appengine.protocol.ErrorCode
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
        AppResult.Failure(e.toJoinError())
    }

    suspend fun createRoom(name: String?, icon: String, inviteUserIds: List<String>): AppResult<CreatedPlayRoom> = try {
        AppResult.Success(api.createRoom(CreateRoom(name, icon, inviteUserIds)).toCreated())
    } catch (e: Exception) {
        CrashReporter.record(e, "PlayRoomsRemoteManager.createRoom")
        AppResult.Failure(e.toJoinError())
    }

    /** The ids that got an invite. */
    suspend fun inviteToRoom(roomId: String, userIds: List<String>, userCodes: List<String> = emptyList()): AppResult<List<String>> = try {
        AppResult.Success(api.inviteToRoom(roomId, InviteToRoomBody(userIds, userCodes)).invited)
    } catch (e: Exception) {
        CrashReporter.record(e, "PlayRoomsRemoteManager.inviteToRoom")
        AppResult.Failure(YapNPlayError.Network(INVITE_FAILED))
    }

    suspend fun fetchInvites(): AppResult<List<PlayRoomInvite>> = try {
        AppResult.Success(api.getRoomInvites().invites.map { it.toDomain() })
    } catch (e: Exception) {
        CrashReporter.record(e, "PlayRoomsRemoteManager.fetchInvites")
        AppResult.Failure(YapNPlayError.Network(INVITES_FAILED))
    }

    suspend fun acceptInvite(inviteId: String): AppResult<PlayRoom> = try {
        AppResult.Success(api.acceptRoomInvite(inviteId).toDomain())
    } catch (e: Exception) {
        CrashReporter.record(e, "PlayRoomsRemoteManager.acceptInvite")
        AppResult.Failure(YapNPlayError.Network(INVITE_FAILED))
    }

    suspend fun declineInvite(inviteId: String): AppResult<Unit> = try {
        AppResult.Success(api.declineRoomInvite(inviteId))
    } catch (e: Exception) {
        CrashReporter.record(e, "PlayRoomsRemoteManager.declineInvite")
        AppResult.Failure(YapNPlayError.Network(INVITE_FAILED))
    }

    /** The backend's reasons become the errors the rooms screen already explains. */
    private fun Exception.toJoinError(): YapNPlayError = when ((this as? RealtimeRejectedException)?.code) {
        ErrorCode.INVALID_CODE -> YapNPlayError.Validation(JoinPlayRoomByCodeUseCase.FIELD_ROOM_CODE)
        ErrorCode.NOT_FOUND -> YapNPlayError.NotFound(NOT_FOUND_ROOM)
        ErrorCode.ROOM_LIMIT_REACHED -> YapNPlayError.LimitReached(AppManagerKeys.App.MAX_ROOMS_PER_USER.default)
        ErrorCode.GROUP_EXISTS -> YapNPlayError.AlreadyExists(message.orEmpty())
        else -> YapNPlayError.Network(message ?: NETWORK_FAILED)
    }

    private companion object {
        const val NOT_FOUND_ROOM = "room"
        const val NETWORK_FAILED = "room request failed"
        const val INVITE_FAILED = "room invite failed"
        const val INVITES_FAILED = "room invites fetch failed"
    }
}
