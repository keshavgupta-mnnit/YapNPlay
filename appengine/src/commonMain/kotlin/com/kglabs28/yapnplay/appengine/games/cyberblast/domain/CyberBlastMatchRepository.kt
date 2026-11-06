package com.kglabs28.yapnplay.appengine.games.cyberblast.domain

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import kotlinx.coroutines.flow.Flow

/** A Cyber Blast table: the match, its frames, who's talking; controls out. */
interface CyberBlastMatchRepository {
    fun observeMatch(matchId: String): Flow<CyberUpdate>
    fun observeFrames(): Flow<CyberFrameData>
    fun observeSpeaking(matchId: String): Flow<Set<String>>
    suspend fun send(matchId: String, command: CyberCommand): AppResult<Unit>
}
