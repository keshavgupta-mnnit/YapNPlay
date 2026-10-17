package com.kglabs28.yapnplay.appengine.games.citybazaar.data.remote

import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarCommand
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarUpdate
import kotlinx.coroutines.flow.Flow

/**
 * Match client. TODAY: every call goes to [FakeCityBazaarServer], which
 * runs the real engine on the device. LATER: the production paths below
 * (same match channel as Royal Ludo, game-specific actions).
 */
class CityBazaarMatchClientImpl(
    private val fakeServer: FakeCityBazaarServer,
) : CityBazaarMatchClient {

    override fun observeMatch(matchId: String): Flow<CityBazaarUpdate> {
        // --- production path ---
        // return realtime.stream(Channel.MATCH)
        //     .onStart { realtime.send(Channel.MATCH, MessageType.MATCH_REQUEST_SNAPSHOT, MatchRequestSnapshot(matchId, lastVersion = 0)) }
        //     .filter { it.type == MessageType.MATCH_SNAPSHOT }
        //     .map { it.decode<MatchSnapshot>().toCityBazaarUpdate(myUserId) }

        // --- fake path ---
        return fakeServer.match(matchId)
    }

    override fun observeSpeaking(matchId: String): Flow<Set<String>> {
        // --- production path ---
        // return voiceClient.activeSpeakers

        // --- fake path ---
        return fakeServer.speaking()
    }

    override suspend fun send(matchId: String, command: CityBazaarCommand) {
        // --- production path: one match-channel message per command ---
        // realtime.request(Channel.MATCH, MessageType.GAME_ACTION, GameAction(matchId, command.toWire()))

        // --- fake path ---
        fakeServer.handle(command)
    }
}
