package com.kglabs28.yapnplay.appengine.games.cyberblast.data

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.games.cyberblast.data.remote.CyberBlastMatchClient
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberBlastMatchRepository
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberCommand
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberFrameData
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberUpdate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/** Streams the match and its frames, and sends the user's controls. Never throws: failures come back typed. */
class CyberBlastMatchRepositoryImpl(
    private val client: CyberBlastMatchClient,
    private val dispatchers: DispatcherProvider,
) : CyberBlastMatchRepository {

    override fun observeMatch(matchId: String): Flow<CyberUpdate> = client.observeMatch(matchId).flowOn(dispatchers.io)

    override fun observeFrames(): Flow<CyberFrameData> = client.observeFrames().flowOn(dispatchers.io)

    override fun observeSpeaking(matchId: String): Flow<Set<String>> = client.observeSpeaking(matchId).flowOn(dispatchers.io)

    override suspend fun send(matchId: String, command: CyberCommand): AppResult<Unit> = withContext(dispatchers.io) {
        try {
            client.send(matchId, command)
            AppResult.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            CrashReporter.record(e, "CyberBlastMatchRepository.${command::class.simpleName}")
            AppResult.Failure(YapNPlayError.Network("cyber blast send failed"))
        }
    }
}
