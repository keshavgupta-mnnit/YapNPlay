package com.kglabs28.yapnplay.appengine.features.moderation.data

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.features.moderation.data.remote.ModerationClient
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ModerationRepository
import com.kglabs28.yapnplay.appengine.features.moderation.domain.Penalty
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ReportOutcome
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ReportVoteRequest
import com.kglabs28.yapnplay.appengine.protocol.ReportPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant

/** Maps the moderation channel to domain. Never throws: failures come back typed. */
class ModerationRepositoryImpl(
    private val client: ModerationClient,
    private val dispatchers: DispatcherProvider,
) : ModerationRepository {

    private val scope = CoroutineScope(SupervisorJob() + dispatchers.default)

    override suspend fun report(contextId: String, reportedUserId: String, reportedName: String, voters: Int): AppResult<Unit> =
        call("report") { client.report(ReportPlayer(contextId, reportedUserId, REASON_VOICE), reportedName, voters) }

    override val voteRequests: Flow<ReportVoteRequest> = client.voteRequests().map {
        ReportVoteRequest(it.reportId, it.reporterName, it.reportedName, Instant.fromEpochMilliseconds(it.expiresAt))
    }

    override suspend fun vote(reportId: String, agree: Boolean): AppResult<Unit> = call("vote") { client.vote(reportId, agree) }

    override val outcomes: Flow<ReportOutcome> = client.resolutions().map {
        ReportOutcome(it.upheld, it.reportedName, it.agreed, it.disagreed, it.noResponse, it.required)
    }

    override val penalty: StateFlow<Penalty?> = client.penalties()
        .map { issued -> issued?.let { Penalty(it.pointsDeducted, Instant.fromEpochMilliseconds(it.blockedUntil)) } }
        .stateIn(scope, SharingStarted.Eagerly, null)

    override suspend fun clearPenaltyWithAd(): AppResult<Unit> = withContext(dispatchers.io) {
        try {
            if (client.watchRewardedAd()) AppResult.Success(Unit) else AppResult.Failure(YapNPlayError.Unknown("ad not finished"))
        } catch (e: Exception) {
            CrashReporter.record(e, "ModerationRepository.clearPenaltyWithAd")
            AppResult.Failure(YapNPlayError.Network("ad failed"))
        }
    }

    private suspend fun call(name: String, block: suspend () -> Unit): AppResult<Unit> = withContext(dispatchers.io) {
        try {
            block()
            AppResult.Success(Unit)
        } catch (e: Exception) {
            CrashReporter.record(e, "ModerationRepository.$name")
            AppResult.Failure(YapNPlayError.Network("moderation $name failed"))
        }
    }

    private companion object {
        const val REASON_VOICE = "voice_chat"
    }
}
