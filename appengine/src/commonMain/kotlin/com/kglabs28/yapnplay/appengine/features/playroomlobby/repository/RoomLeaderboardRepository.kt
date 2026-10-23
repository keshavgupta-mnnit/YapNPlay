package com.kglabs28.yapnplay.appengine.features.playroomlobby.repository

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.RoomMemberRecord

/** A room's standings: every member's record there. */
interface RoomLeaderboardRepository {
    suspend fun records(roomId: String): AppResult<List<RoomMemberRecord>>
}
