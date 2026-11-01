package com.kglabs28.yapnplay.appengine.games.doodleking.domain

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import kotlinx.coroutines.flow.Flow

/** A Doodle King table: the match, its drawing, who's talking; moves and strokes out. */
interface DoodleKingMatchRepository {
    fun observeMatch(matchId: String): Flow<DoodleUpdate>
    fun observeInk(): Flow<DoodleInkOp>
    fun observeSpeaking(matchId: String): Flow<Set<String>>
    suspend fun send(matchId: String, command: DoodleCommand): AppResult<Unit>
    suspend fun sendInk(matchId: String, op: DoodleInkOp): AppResult<Unit>
    suspend fun requestDrawing(matchId: String): AppResult<Unit>
}
