package com.kglabs28.yapnplay.appengine.games.monochaos.data

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.games.monochaos.data.remote.MonoChaosMatchClient
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosCommand
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosMatchRepository
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/** Streams the match and sends actions. Never throws: failures come back typed. */
class MonoChaosMatchRepositoryImpl(
    private val client: MonoChaosMatchClient,
    private val dispatchers: DispatcherProvider,
) : MonoChaosMatchRepository {

    override fun observeMatch(matchId: String): Flow<MonoChaosUpdate> =
        client.observeMatch(matchId).flowOn(dispatchers.io)

    override suspend fun send(matchId: String, command: MonoChaosCommand): AppResult<Unit> =
        withContext(dispatchers.io) {
            try {
                client.send(matchId, command)
                AppResult.Success(Unit)
            } catch (e: Exception) {
                CrashReporter.record(e, "MonoChaosMatchRepository.send:${command::class.simpleName}")
                AppResult.Failure(YapNPlayError.Network("match command failed"))
            }
        }
}