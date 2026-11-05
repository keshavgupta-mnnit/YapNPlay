package com.kglabs28.yapnplay.appengine.features.playroomlobby.usecase

import com.kglabs28.yapnplay.appengine.features.nearby.usecase.GetMyPlayerIdUseCase
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.gamecatalog.repository.GameCatalogRepository
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.LobbyData
import com.kglabs28.yapnplay.appengine.features.playroomlobby.repository.PlayRoomLobbyRepository
import com.kglabs28.yapnplay.appengine.features.profile.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

/**
 * The lobby as one stream: live room + who is talking, joined with who
 * "me" is and the game catalog (the selected game details and rules).
 */
class ObservePlayRoomLobbyUseCase(
    private val lobbyRepository: PlayRoomLobbyRepository,
    private val catalogRepository: GameCatalogRepository,
    private val profileRepository: ProfileRepository,
    /** Offline (a Nearby party) the profile can't be fetched: the account saved on the phone. */
    private val myPlayerId: GetMyPlayerIdUseCase,
) {

    operator fun invoke(roomId: String): Flow<LobbyData> = flow {
        val myUserId = (profileRepository.fetchProfileData() as? AppResult.Success)?.data?.userId ?: myPlayerId()
        val games = catalogRepository.getGames()
        emitAll(
            combine(
                lobbyRepository.observeRoom(roomId),
                lobbyRepository.observeSpeaking(roomId).onStart { emit(emptySet()) },
            ) { room, speaking ->
                LobbyData(
                    room = room,
                    speakingUserIds = speaking,
                    myUserId = myUserId,
                    games = games,
                    selectedGame = games.firstOrNull { it.id == room.selectedGameId },
                )
            },
        )
    }
}
