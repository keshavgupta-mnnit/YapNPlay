package com.kglabs28.yapnplay.appengine.games.mono.domain

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import kotlinx.coroutines.flow.Flow

/** A Standard MONO match: live updates in, the player's actions out. */
interface MonoMatchRepository {
    fun observeMatch(matchId: String): Flow<MonoUpdate>
    fun observeSpeaking(matchId: String): Flow<Set<String>>
    suspend fun send(matchId: String, command: MonoCommand): AppResult<Unit>
}
