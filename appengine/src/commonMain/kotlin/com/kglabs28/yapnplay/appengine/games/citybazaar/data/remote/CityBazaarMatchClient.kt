package com.kglabs28.yapnplay.appengine.games.citybazaar.data.remote

import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarCommand
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarUpdate
import kotlinx.coroutines.flow.Flow

/** Match transport: the MATCH channel (snapshots and events in, actions out) plus who is talking. */
interface CityBazaarMatchClient {
    fun observeMatch(matchId: String): Flow<CityBazaarUpdate>
    fun observeSpeaking(matchId: String): Flow<Set<String>>
    suspend fun send(matchId: String, command: CityBazaarCommand)
}
