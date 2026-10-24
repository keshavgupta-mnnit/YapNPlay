package com.kglabs28.yapnplay.appengine.features.playroomlobby.repository

import com.kglabs28.yapnplay.appengine.core.common.AppResult

/** Who already has an invite to a room, not yet answered. */
interface RoomPendingInvitesRepository {
    suspend fun pendingUserIds(roomId: String): AppResult<Set<String>>
}
