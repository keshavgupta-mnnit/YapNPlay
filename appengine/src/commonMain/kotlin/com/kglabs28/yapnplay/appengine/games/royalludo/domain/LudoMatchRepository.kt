package com.kglabs28.yapnplay.appengine.games.royalludo.domain

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoCommand
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoUpdate
import kotlinx.coroutines.flow.Flow

/** A Royal Ludo match: live updates in, the player's actions out. */
interface LudoMatchRepository {
    fun observeMatch(matchId: String): Flow<LudoUpdate>
    fun observeSpeaking(matchId: String): Flow<Set<String>>
    fun observeLatency(): Flow<Int>
    suspend fun send(matchId: String, command: LudoCommand): AppResult<Unit>
}
