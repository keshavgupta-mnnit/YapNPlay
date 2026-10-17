package com.kglabs28.yapnplay.appengine.games.citybaazar.data.remote

import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarCommand
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarUpdate
import kotlinx.coroutines.flow.Flow

/** Match transport: the MATCH channel (snapshots and events in, actions out) plus who is talking. */
interface CityBaazarMatchClient {
    fun observeMatch(matchId: String): Flow<CityBaazarUpdate>
    fun observeSpeaking(matchId: String): Flow<Set<String>>
    suspend fun send(matchId: String, command: CityBaazarCommand)
}
