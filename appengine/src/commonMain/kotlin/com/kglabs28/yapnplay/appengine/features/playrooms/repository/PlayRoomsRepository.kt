package com.kglabs28.yapnplay.appengine.features.playrooms.repository

import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoomInvite
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.CreatedPlayRoom
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.JoinOutcome
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoom
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoomPreview

/**
 * The rooms the user belongs to, and joining one by code. Implemented by
 * PlayRoomsRepositoryImpl over remote + local managers; a backend
 * implementation replaces the API client without touching callers.
 */
interface PlayRoomsRepository {
    suspend fun getMyRooms(): AppResult<List<PlayRoom>>
    suspend fun previewRoom(code: String): AppResult<PlayRoomPreview>
    suspend fun joinRoom(code: String): AppResult<JoinOutcome>

    /** Creates a group (no name: the server picks one) and invites [inviteUserIds]. */
    suspend fun createRoom(name: String?, icon: String, inviteUserIds: List<String>): AppResult<CreatedPlayRoom>

    /** Invites friends into a group the user belongs to; the ids that got an invite. */
    suspend fun inviteToRoom(roomId: String, userIds: List<String>): AppResult<List<String>>

    suspend fun getInvites(): AppResult<List<PlayRoomInvite>>

    /** Accepting joins the group at once. */
    suspend fun acceptInvite(inviteId: String): AppResult<PlayRoom>

    suspend fun declineInvite(inviteId: String): AppResult<Unit>
}
