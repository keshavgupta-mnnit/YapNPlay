package com.kglabs28.yapnplay.appengine.features.moderation.data.remote

import com.kglabs28.yapnplay.appengine.protocol.PenaltyIssued
import com.kglabs28.yapnplay.appengine.protocol.ReportPlayer
import com.kglabs28.yapnplay.appengine.protocol.ReportResolved
import com.kglabs28.yapnplay.appengine.protocol.ReportVoteRequested
import kotlinx.coroutines.flow.Flow

/**
 * TODAY: [FakeModerationServer] plays the room (other people's votes,
 * one sample prompt for the user). LATER: uncomment the production paths
 * and delete the fake lines.
 */
class ModerationClientImpl(private val fakeServer: FakeModerationServer) : ModerationClient {

    override suspend fun report(request: ReportPlayer, reportedName: String, voters: Int) {
        // --- production path ---
        // realtime.request(Channel.MODERATION, MessageType.REPORT_PLAYER, request)

        // --- fake path ---
        fakeServer.report(request, reportedName, voters)
    }

    override fun voteRequests(): Flow<ReportVoteRequested> {
        // --- production path ---
        // return realtime.stream(Channel.MODERATION).filter { it.type == MessageType.REPORT_VOTE_REQUESTED }.map { it.decode() }

        // --- fake path ---
        return fakeServer.voteRequests
    }

    override suspend fun vote(reportId: String, agree: Boolean) {
        // --- production path ---
        // realtime.request(Channel.MODERATION, MessageType.CAST_REPORT_VOTE, CastReportVote(reportId, agree))

        // --- fake path ---
        fakeServer.vote(reportId, agree)
    }

    override fun resolutions(): Flow<ReportResolved> {
        // --- production path ---
        // return realtime.stream(Channel.MODERATION).filter { it.type == MessageType.REPORT_RESOLVED }.map { it.decode() }

        // --- fake path ---
        return fakeServer.resolutions
    }

    override fun penalties(): Flow<PenaltyIssued?> {
        // --- production path ---
        // return realtime.stream(Channel.MODERATION).filter { it.type == MessageType.PENALTY_ISSUED }.map { it.decode() }

        // --- fake path ---
        return fakeServer.penalty
    }

    override suspend fun watchRewardedAd(): Boolean {
        // --- production path: rewarded ad provider, then the server lifts the block ---
        // val watched = rewardedAds.show()
        // if (watched) http.post(AppConstants.SERVER_BASE_URL + "/penalties/clear-with-ad")
        // return watched

        // --- fake path ---
        return fakeServer.watchAd()
    }
}
