package com.kglabs28.yapnplay.appengine.features.matchmaking.usecase

import com.kglabs28.yapnplay.appengine.features.matchmaking.repository.MatchmakingRepository

/** Tells the server to drop the user from the Quick Play queue. */
class LeaveQuickPlayQueueUseCase(private val repository: MatchmakingRepository) {

    suspend operator fun invoke() = repository.leaveQueue()
}
