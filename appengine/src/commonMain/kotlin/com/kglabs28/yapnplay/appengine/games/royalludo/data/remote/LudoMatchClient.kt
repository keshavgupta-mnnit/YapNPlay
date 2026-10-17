package com.kglabs28.yapnplay.appengine.games.royalludo.data.remote

import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoCommand
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoUpdate
import kotlinx.coroutines.flow.Flow

/**
 * Match transport: the MATCH channel of the WebSocket (snapshots and
 * events in, actions out) plus who is talking (voice provider).
 */
interface LudoMatchClient {
    /** Every `MatchSnapshot` with the events since the last one. */
    fun observeMatch(matchId: String): Flow<LudoUpdate>

    /** User ids currently speaking (LiveKit active speakers later). */
    fun observeSpeaking(matchId: String): Flow<Set<String>>

    /** Round-trip time to the server in ms (the ping shown in the top bar). */
    fun observeLatency(): Flow<Int>

    suspend fun send(matchId: String, command: LudoCommand)
}
