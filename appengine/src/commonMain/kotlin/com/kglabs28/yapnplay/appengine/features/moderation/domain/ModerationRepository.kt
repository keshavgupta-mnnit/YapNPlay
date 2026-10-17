package com.kglabs28.yapnplay.appengine.features.moderation.domain

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/** Voice-chat reports, votes on others' reports, and the user's penalty. */
interface ModerationRepository {
    /** [voters] are the people who may confirm (everyone present but the two involved). */
    suspend fun report(contextId: String, reportedUserId: String, reportedName: String, voters: Int): AppResult<Unit>
    val voteRequests: Flow<ReportVoteRequest>
    suspend fun vote(reportId: String, agree: Boolean): AppResult<Unit>
    val outcomes: Flow<ReportOutcome>
    val penalty: StateFlow<Penalty?>
    /** Watch a rewarded ad; on success the block is lifted. */
    suspend fun clearPenaltyWithAd(): AppResult<Unit>
}
