package com.kglabs28.yapnplay.appengine.features.moderation.data.remote

import com.kglabs28.yapnplay.appengine.protocol.PenaltyIssued
import com.kglabs28.yapnplay.appengine.protocol.ReportPlayer
import com.kglabs28.yapnplay.appengine.protocol.ReportResolved
import com.kglabs28.yapnplay.appengine.protocol.ReportVoteRequested
import kotlinx.coroutines.flow.Flow

/** Moderation transport (WebSocket moderation channel) and the rewarded-ad provider. */
interface ModerationClient {
    suspend fun report(request: ReportPlayer, reportedName: String, voters: Int)
    fun voteRequests(): Flow<ReportVoteRequested>
    suspend fun vote(reportId: String, agree: Boolean)
    fun resolutions(): Flow<ReportResolved>
    fun penalties(): Flow<PenaltyIssued?>
    /** Shows a rewarded ad; true when it was watched to the end (the server then lifts the block). */
    suspend fun watchRewardedAd(): Boolean
}
