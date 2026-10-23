package com.kglabs28.yapnplay.appengine.games.mono.data.remote

import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoCommand
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoUpdate
import kotlinx.coroutines.flow.Flow

/**
 * Standard MONO over the MATCH channel: snapshots in, commands out. Seat
 * and presence requests are the channel's, so they are not repeated here.
 */
interface MonoMatchClient {
    fun observeMatch(matchId: String): Flow<MonoUpdate>
    /** User ids talking right now at the table (its voice room). */
    fun observeSpeaking(matchId: String): Flow<Set<String>>
    suspend fun send(matchId: String, command: MonoCommand)
    suspend fun stillHere(matchId: String)
}