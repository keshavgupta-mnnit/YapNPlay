package com.kglabs28.yapnplay.appengine.games.doodleking.data

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.games.doodleking.data.remote.DoodleKingMatchClient
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleCommand
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleInkOp
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleKingMatchRepository
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleUpdate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/** Streams the match and its drawing, and sends moves and strokes. Never throws: failures come back typed. */
class DoodleKingMatchRepositoryImpl(
    private val client: DoodleKingMatchClient,
    private val dispatchers: DispatcherProvider,
) : DoodleKingMatchRepository {

    override fun observeMatch(matchId: String): Flow<DoodleUpdate> = client.observeMatch(matchId).flowOn(dispatchers.io)

    override fun observeInk(): Flow<DoodleInkOp> = client.observeInk().flowOn(dispatchers.io)

    override fun observeSpeaking(matchId: String): Flow<Set<String>> = client.observeSpeaking(matchId).flowOn(dispatchers.io)

    override suspend fun send(matchId: String, command: DoodleCommand): AppResult<Unit> =
        safely("send:${command::class.simpleName}") { client.send(matchId, command) }

    override suspend fun sendInk(matchId: String, op: DoodleInkOp): AppResult<Unit> = safely("ink") { client.sendInk(matchId, op) }

    override suspend fun requestDrawing(matchId: String): AppResult<Unit> = safely("sync") { client.requestDrawing(matchId) }

    private suspend fun safely(what: String, block: suspend () -> Unit): AppResult<Unit> = withContext(dispatchers.io) {
        try {
            block()
            AppResult.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            CrashReporter.record(e, "DoodleKingMatchRepository.$what")
            AppResult.Failure(YapNPlayError.Network("doodle $what failed"))
        }
    }
}
