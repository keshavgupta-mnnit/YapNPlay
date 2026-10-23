package com.kglabs28.yapnplay.appengine.games.monochaos.domain

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import kotlinx.coroutines.flow.Flow

/** A MONO: Complete Chaos match: live updates in, the player's actions out. */
interface MonoChaosMatchRepository {
    fun observeMatch(matchId: String): Flow<MonoChaosUpdate>
    fun observeSpeaking(matchId: String): Flow<Set<String>>
    suspend fun send(matchId: String, command: MonoChaosCommand): AppResult<Unit>
}
