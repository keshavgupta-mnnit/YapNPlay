package com.kglabs28.yapnplay.appengine.games.citybaazar.domain

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import kotlinx.coroutines.flow.Flow

/** A City Baazar match: live updates in, the player's actions out. */
interface CityBaazarMatchRepository {
    fun observeMatch(matchId: String): Flow<CityBaazarUpdate>
    fun observeSpeaking(matchId: String): Flow<Set<String>>
    suspend fun send(matchId: String, command: CityBaazarCommand): AppResult<Unit>
}
