package com.kglabs28.yapnplay.appengine.games.royalludo.data

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.games.royalludo.data.remote.LudoMatchClient
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoCommand
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoUpdate
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoMatchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/** Streams the match and sends actions. Never throws: failures come back typed. */
class LudoMatchRepositoryImpl(
    private val client: LudoMatchClient,
    private val dispatchers: DispatcherProvider,
) : LudoMatchRepository {

    override fun observeMatch(matchId: String): Flow<LudoUpdate> =
        client.observeMatch(matchId).flowOn(dispatchers.io)

    override fun observeSpeaking(matchId: String): Flow<Set<String>> =
        client.observeSpeaking(matchId).flowOn(dispatchers.io)

    override fun observeLatency(): Flow<Int> = client.observeLatency().flowOn(dispatchers.io)

    override suspend fun send(matchId: String, command: LudoCommand): AppResult<Unit> =
        withContext(dispatchers.io) {
            try {
                client.send(matchId, command)
                AppResult.Success(Unit)
            } catch (e: Exception) {
                CrashReporter.record(e, "LudoMatchRepository.send:${command::class.simpleName}")
                AppResult.Failure(YapNPlayError.Network("match command failed"))
            }
        }
}
