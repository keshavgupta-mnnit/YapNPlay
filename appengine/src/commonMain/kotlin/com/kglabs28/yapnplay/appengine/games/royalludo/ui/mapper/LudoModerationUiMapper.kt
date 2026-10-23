package com.kglabs28.yapnplay.appengine.games.royalludo.ui.mapper

import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import com.kglabs28.yapnplay.appengine.features.moderation.domain.Penalty
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ReportOutcome
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ReportVoteRequest
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoMatch
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoMatchModal
import kotlin.math.ceil
import kotlinx.datetime.Instant
import com.kglabs28.yapnplay.appengine.ui.components.moderation.ReportPersonUi

/** Report, vote and penalty dialogs for the table. Pure functions. */
object LudoModerationUiMapper {

    /** Everyone the user can report: the other players still seated, then spectators. */
    fun reportablePeople(match: LudoMatch): List<ReportPersonUi> =
        match.players.filter { !it.isMe && !it.removed }.map { ReportPersonUi(it.userId, it.name, LudoStrings.ludoRolePlayer) } +
            match.spectators.filter { it.userId != match.viewerUserId }.map { ReportPersonUi(it.userId, it.name, LudoStrings.ludoRoleSpectator) }

    /** People who would vote: everyone present except the reporter and the reported person. */
    fun voters(match: LudoMatch): Int {
        val present = match.players.count { !it.removed } + match.spectators.size
        return (present - INVOLVED).coerceAtLeast(0)
    }

    fun vote(request: ReportVoteRequest, now: Instant): LudoMatchModal.Vote {
        val seconds = ceil((request.expiresAt - now).inWholeMilliseconds.coerceAtLeast(0) / MILLIS.toDouble()).toInt()
        return LudoMatchModal.Vote(
            reportId = request.reportId,
            title = LudoStrings.ludoVoteTitle,
            body = LudoStrings.ludoVoteBody(request.reporterName, request.reportedName),
            secondsLeft = LudoStrings.ludoVoteSecondsLeft(seconds),
        )
    }

    fun outcome(outcome: ReportOutcome): LudoMatchModal.ReportOutcome {
        val tally = LudoStrings.ludoReportTally(outcome.agreed, outcome.disagreed, outcome.noResponse)
        return if (outcome.upheld) {
            LudoMatchModal.ReportOutcome(LudoStrings.ludoReportUpheldTitle, LudoStrings.ludoReportUpheldBody(outcome.reportedName, tally))
        } else {
            LudoMatchModal.ReportOutcome(LudoStrings.ludoReportRejectedTitle, LudoStrings.ludoReportRejectedBody(tally, outcome.required))
        }
    }

    fun penalty(penalty: Penalty, now: Instant, watchingAd: Boolean, adOffered: Boolean): LudoMatchModal.Penalty {
        val minutes = ceil((penalty.blockedUntil - now).inWholeSeconds.coerceAtLeast(0) / SECONDS_PER_MINUTE.toDouble()).toInt()
        return LudoMatchModal.Penalty(LudoStrings.ludoPenaltyTitle, LudoStrings.ludoPenaltyBody(penalty.points, minutes), watchingAd, adOffered)
    }

    private const val INVOLVED = 2
    private const val MILLIS = 1_000L
    private const val SECONDS_PER_MINUTE = 60L
}
