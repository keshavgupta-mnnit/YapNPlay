package com.kglabs28.yapnplay.appengine.games.mono.data

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.games.mono.data.remote.MonoMatchClient
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoCommand
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoMatchRepository
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/** Streams the match and sends actions. Never throws: failures come back typed. */
class MonoMatchRepositoryImpl(
    private val client: MonoMatchClient,
    private val dispatchers: DispatcherProvider,
) : MonoMatchRepository {

    override fun observeMatch(matchId: String): Flow<MonoUpdate> =
        client.observeMatch(matchId).flowOn(dispatchers.io)

    override suspend fun send(matchId: String, command: MonoCommand): AppResult<Unit> =
        withContext(dispatchers.io) {
            try {
                client.send(matchId, command)
                AppResult.Success(Unit)
            } catch (e: Exception) {
                CrashReporter.record(e, "MonoMatchRepository.send:${command::class.simpleName}")
                AppResult.Failure(YapNPlayError.Network("match command failed"))
            }
        }
}