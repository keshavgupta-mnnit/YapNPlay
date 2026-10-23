package com.kglabs28.yapnplay.appengine.features.moderation.data.remote

import com.kglabs28.yapnplay.appengine.core.network.BackendPaths
import com.kglabs28.yapnplay.appengine.core.network.RealtimeGateway
import com.kglabs28.yapnplay.appengine.protocol.CastReportVote
import com.kglabs28.yapnplay.appengine.protocol.PenaltyIssued
import com.kglabs28.yapnplay.appengine.protocol.ReportPlayer
import com.kglabs28.yapnplay.appengine.protocol.ReportResolved
import com.kglabs28.yapnplay.appengine.protocol.ReportVoteRequested
import com.kglabs28.yapnplay.appengine.protocol.transport.Channel
import com.kglabs28.yapnplay.appengine.protocol.transport.MessageType
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.serialization.KSerializer

/**
 * MODERATION channel. Reports and votes get an Ack; vote prompts, results
 * and penalties arrive as server pushes. The server counts the voters
 * itself, so [report]'s name and voter count are not sent.
 */
class ModerationClientImpl(
    private val realtime: RealtimeGateway,
    private val client: HttpClient,
) : ModerationClient {

    /** null once the block is lifted with an ad (the server sends nothing then). */
    private val cleared = MutableSharedFlow<PenaltyIssued?>(extraBufferCapacity = 1)

    override suspend fun report(request: ReportPlayer, reportedName: String, voters: Int) {
        realtime.request(Channel.MODERATION, MessageType.REPORT_PLAYER, ReportPlayer.serializer(), request)
    }

    override fun voteRequests(): Flow<ReportVoteRequested> = pushes(MessageType.REPORT_VOTE_REQUESTED, ReportVoteRequested.serializer())

    override suspend fun vote(reportId: String, agree: Boolean) {
        realtime.request(Channel.MODERATION, MessageType.CAST_REPORT_VOTE, CastReportVote.serializer(), CastReportVote(reportId, agree))
    }

    override fun resolutions(): Flow<ReportResolved> = pushes(MessageType.REPORT_RESOLVED, ReportResolved.serializer())

    override fun penalties(): Flow<PenaltyIssued?> = merge(pushes(MessageType.PENALTY_ISSUED, PenaltyIssued.serializer()), cleared)

    override suspend fun clearPenaltyWithAd() {
        client.post(BackendPaths.CLEAR_PENALTY_WITH_AD)
        cleared.emit(null)
    }

    private fun <T> pushes(type: String, serializer: KSerializer<T>): Flow<T> =
        realtime.stream(Channel.MODERATION)
            .filter { it.type == type }
            .map { realtime.decode(it, serializer) }
}
