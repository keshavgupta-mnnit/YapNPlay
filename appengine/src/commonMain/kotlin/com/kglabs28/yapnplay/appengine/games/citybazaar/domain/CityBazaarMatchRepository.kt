package com.kglabs28.yapnplay.appengine.games.citybazaar.domain

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import kotlinx.coroutines.flow.Flow

/** A City Bazaar match: live updates in, the player's actions out. */
interface CityBazaarMatchRepository {
    fun observeMatch(matchId: String): Flow<CityBazaarUpdate>
    fun observeSpeaking(matchId: String): Flow<Set<String>>
    suspend fun send(matchId: String, command: CityBazaarCommand): AppResult<Unit>
}
