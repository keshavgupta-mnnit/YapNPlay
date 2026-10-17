package com.kglabs28.yapnplay.appengine.games.citybaazar.data

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.games.citybaazar.data.remote.CityBaazarMatchClient
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarCommand
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarMatchRepository
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/** Streams the match and sends actions. Never throws: failures come back typed. */
class CityBaazarMatchRepositoryImpl(
    private val client: CityBaazarMatchClient,
    private val dispatchers: DispatcherProvider,
) : CityBaazarMatchRepository {

    override fun observeMatch(matchId: String): Flow<CityBaazarUpdate> =
        client.observeMatch(matchId).flowOn(dispatchers.io)

    override fun observeSpeaking(matchId: String): Flow<Set<String>> =
        client.observeSpeaking(matchId).flowOn(dispatchers.io)

    override suspend fun send(matchId: String, command: CityBaazarCommand): AppResult<Unit> =
        withContext(dispatchers.io) {
            try {
                client.send(matchId, command)
                AppResult.Success(Unit)
            } catch (e: Exception) {
                CrashReporter.record(e, "CityBaazarMatchRepository.send:${command::class.simpleName}")
                AppResult.Failure(YapNPlayError.Network("match command failed"))
            }
        }
}
