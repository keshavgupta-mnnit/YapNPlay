package com.kglabs28.yapnplay.appengine.features.moderation.data.remote

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.FakeDataProvider
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ModerationRules
import com.kglabs28.yapnplay.appengine.protocol.PenaltyIssued
import com.kglabs28.yapnplay.appengine.protocol.ReportBallot
import com.kglabs28.yapnplay.appengine.protocol.ReportPlayer
import com.kglabs28.yapnplay.appengine.protocol.ReportResolved
import com.kglabs28.yapnplay.appengine.protocol.ReportVoteRequested
import com.kglabs28.yapnplay.appengine.protocol.VoteResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Duration.Companion.minutes

/**
 * In-memory stand-in for the moderation service: the others in the room
 * vote on the user's report after a short while, the report resolves by
 * [ModerationRules], and a false report penalises the user. Each match
 * screen also gets one sample vote prompt so the flow can be tried.
 * Every voter's answer is kept as a ballot (agree, disagree, or no
 * response when the prompt closes unanswered), as the real server does.
 * Deleted with the fakes.
 */
class FakeModerationServer(private val clock: AppClock, dispatchers: DispatcherProvider) {
    private val scope = CoroutineScope(SupervisorJob() + dispatchers.default)
    private val fake = FakeDataProvider.Moderation

    private val requests = MutableSharedFlow<ReportVoteRequested>(extraBufferCapacity = BUFFER)
    private val _resolutions = MutableSharedFlow<ReportResolved>(extraBufferCapacity = BUFFER)
    val resolutions: SharedFlow<ReportResolved> = _resolutions
    private val _penalty = MutableStateFlow<PenaltyIssued?>(null)
    val penalty: StateFlow<PenaltyIssued?> = _penalty
    /** Server-side ballot store: one per voter per report. */
    private val ballots = mutableListOf<ReportBallot>()
    private val myUserId = FakeDataProvider.Profile.me.userId

    suspend fun report(request: ReportPlayer, reportedName: String, voters: Int) {
        scope.launch {
            delay(fake.VOTING_MS)
            val reportId = "report-${clock.now().toEpochMilliseconds()}"
            val required = ModerationRules.requiredAgreements(voters)
            repeat(voters) { voter -> record(reportId, "voter-$voter", simulatedResponse()) }
            val counts = ballotsFor(reportId).groupingBy { it.response }.eachCount()
            val agreed = counts[VoteResponse.AGREE] ?: 0
            val upheld = agreed >= required
            _resolutions.emit(
                ReportResolved(
                    reportId = reportId,
                    upheld = upheld,
                    reportedName = reportedName,
                    agreed = agreed,
                    disagreed = counts[VoteResponse.DISAGREE] ?: 0,
                    noResponse = counts[VoteResponse.NO_RESPONSE] ?: 0,
                    required = required,
                ),
            )
            if (!upheld) {
                _penalty.value = PenaltyIssued(fake.PENALTY_POINTS, (clock.now() + fake.BLOCK_MINUTES.minutes).toEpochMilliseconds())
            }
        }
    }

    /** Prompts for the user; each new listener (a match screen) gets one sample prompt later on. */
    val voteRequests: Flow<ReportVoteRequested> = flow {
        scope.launch {
            delay(fake.SAMPLE_PROMPT_AFTER_MS)
            val (reporter, reported) = FakeDataProvider.LudoMatch.opponents.take(2).map { it.name }
            val expires = clock.now().toEpochMilliseconds() + fake.VOTE_WINDOW_MS
            val reportId = "sample-$expires"
            requests.emit(ReportVoteRequested(reportId, reporter, reported, expires))
            // Closed without an answer: recorded as no response.
            delay(fake.VOTE_WINDOW_MS)
            if (ballotsFor(reportId).none { it.voterUserId == myUserId }) record(reportId, myUserId, VoteResponse.NO_RESPONSE)
        }
        emitAll(requests)
    }

    suspend fun vote(reportId: String, agree: Boolean) {
        delay(FakeDataProvider.NETWORK_DELAY_MS)
        // One ballot per voter: the first answer counts.
        if (ballotsFor(reportId).none { it.voterUserId == myUserId }) {
            record(reportId, myUserId, if (agree) VoteResponse.AGREE else VoteResponse.DISAGREE)
        }
    }

    private fun record(reportId: String, voterUserId: String, response: VoteResponse) = synchronized(ballots) {
        ballots += ReportBallot(reportId, voterUserId, response, clock.now().toEpochMilliseconds())
    }

    private fun ballotsFor(reportId: String) = synchronized(ballots) { ballots.filter { it.reportId == reportId } }

    private fun simulatedResponse(): VoteResponse = when {
        Random.nextDouble() < fake.AGREE_CHANCE -> VoteResponse.AGREE
        Random.nextDouble() < fake.NO_RESPONSE_SHARE -> VoteResponse.NO_RESPONSE
        else -> VoteResponse.DISAGREE
    }

    suspend fun watchAd(): Boolean {
        delay(fake.AD_LENGTH_MS)
        _penalty.value = null
        return true
    }

    private companion object {
        const val BUFFER = 8
    }
}
