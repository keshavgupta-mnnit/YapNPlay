package com.kglabs28.yapnplay.appengine.features.playroomlobby.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.LobbyCommand
import com.kglabs28.yapnplay.appengine.features.playroomlobby.repository.PlayRoomLobbyRepository

/** Sends one lobby command; the server re-checks every role rule. */
class SendLobbyCommandUseCase(private val repository: PlayRoomLobbyRepository) {

    suspend operator fun invoke(roomId: String, command: LobbyCommand): AppResult<Unit> =
        repository.send(roomId, command)
}
