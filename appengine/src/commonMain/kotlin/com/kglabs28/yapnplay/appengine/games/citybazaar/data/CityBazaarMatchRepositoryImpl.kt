package com.kglabs28.yapnplay.appengine.games.citybazaar.data

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.games.citybazaar.data.remote.CityBazaarMatchClient
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarCommand
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarMatchRepository
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/** Streams the match and sends actions. Never throws: failures come back typed. */
class CityBazaarMatchRepositoryImpl(
    private val client: CityBazaarMatchClient,
    private val dispatchers: DispatcherProvider,
) : CityBazaarMatchRepository {

    override fun observeMatch(matchId: String): Flow<CityBazaarUpdate> =
        client.observeMatch(matchId).flowOn(dispatchers.io)

    override fun observeSpeaking(matchId: String): Flow<Set<String>> =
        client.observeSpeaking(matchId).flowOn(dispatchers.io)

    override suspend fun send(matchId: String, command: CityBazaarCommand): AppResult<Unit> =
        withContext(dispatchers.io) {
            try {
                client.send(matchId, command)
                AppResult.Success(Unit)
            } catch (e: Exception) {
                CrashReporter.record(e, "CityBazaarMatchRepository.send:${command::class.simpleName}")
                AppResult.Failure(YapNPlayError.Network("match command failed"))
            }
        }
}
