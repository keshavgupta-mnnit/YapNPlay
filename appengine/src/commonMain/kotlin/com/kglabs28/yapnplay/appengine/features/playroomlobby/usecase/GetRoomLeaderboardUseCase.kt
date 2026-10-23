package com.kglabs28.yapnplay.appengine.features.playroomlobby.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.RoomMemberRecord
import com.kglabs28.yapnplay.appengine.features.playroomlobby.repository.RoomLeaderboardRepository

/** Every member's record in the room, for its leaderboard. */
class GetRoomLeaderboardUseCase(private val repository: RoomLeaderboardRepository) {
    suspend operator fun invoke(roomId: String): AppResult<List<RoomMemberRecord>> = repository.records(roomId)
}
