package com.kglabs28.yapnplay.appengine.games.monochaos.data.remote

import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosCommand
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosUpdate
import kotlinx.coroutines.flow.Flow

/**
 * MONO: Complete Chaos over the MATCH channel: snapshots in, commands out.
 * Seat and presence requests are the channel's, so they are not repeated.
 */
interface MonoChaosMatchClient {
    fun observeMatch(matchId: String): Flow<MonoChaosUpdate>
    suspend fun send(matchId: String, command: MonoChaosCommand)
    suspend fun stillHere(matchId: String)
}