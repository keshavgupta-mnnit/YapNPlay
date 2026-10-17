package com.kglabs28.yapnplay.appengine.features.playroomlobby.usecase

import com.kglabs28.yapnplay.appengine.features.playroomlobby.repository.PlayRoomLobbyRepository
import kotlinx.coroutines.flow.Flow

/** Match ids the server starts for this room (countdown finished). */
class ObserveMatchStartingUseCase(private val repository: PlayRoomLobbyRepository) {

    operator fun invoke(roomId: String): Flow<String> = repository.observeMatchStarting(roomId)
}
