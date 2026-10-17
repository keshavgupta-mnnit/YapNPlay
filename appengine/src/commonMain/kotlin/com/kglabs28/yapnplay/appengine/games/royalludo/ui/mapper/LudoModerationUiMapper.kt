package com.kglabs28.yapnplay.appengine.games.royalludo.ui.mapper

import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.moderation.domain.Penalty
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ReportOutcome
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ReportVoteRequest
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoMatch
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoMatchModal
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.ReportPersonUi
import kotlin.math.ceil
import kotlinx.datetime.Instant

/** Report, vote and penalty dialogs for the table. Pure functions. */
object LudoModerationUiMapper {

    /** Everyone the user can report: the other players still seated, then spectators. */
    fun reportablePeople(match: LudoMatch): List<ReportPersonUi> =
        match.players.filter { !it.isMe && !it.removed }.map { ReportPersonUi(it.userId, it.name, Strings.ludoRolePlayer) } +
            match.spectators.filter { it.userId != match.viewerUserId }.map { ReportPersonUi(it.userId, it.name, Strings.ludoRoleSpectator) }

    /** People who would vote: everyone present except the reporter and the reported person. */
    fun voters(match: LudoMatch): Int {
        val present = match.players.count { !it.removed } + match.spectators.size
        return (present - INVOLVED).coerceAtLeast(0)
    }

    fun vote(request: ReportVoteRequest, now: Instant): LudoMatchModal.Vote {
        val seconds = ceil((request.expiresAt - now).inWholeMilliseconds.coerceAtLeast(0) / MILLIS.toDouble()).toInt()
        return LudoMatchModal.Vote(
            reportId = request.reportId,
            title = Strings.ludoVoteTitle,
            body = Strings.ludoVoteBody(request.reporterName, request.reportedName),
            secondsLeft = Strings.ludoVoteSecondsLeft(seconds),
        )
    }

    fun outcome(outcome: ReportOutcome): LudoMatchModal.ReportOutcome {
        val tally = Strings.ludoReportTally(outcome.agreed, outcome.disagreed, outcome.noResponse)
        return if (outcome.upheld) {
            LudoMatchModal.ReportOutcome(Strings.ludoReportUpheldTitle, Strings.ludoReportUpheldBody(outcome.reportedName, tally))
        } else {
            LudoMatchModal.ReportOutcome(Strings.ludoReportRejectedTitle, Strings.ludoReportRejectedBody(tally, outcome.required))
        }
    }

    fun penalty(penalty: Penalty, now: Instant, watchingAd: Boolean): LudoMatchModal.Penalty {
        val minutes = ceil((penalty.blockedUntil - now).inWholeSeconds.coerceAtLeast(0) / SECONDS_PER_MINUTE.toDouble()).toInt()
        return LudoMatchModal.Penalty(Strings.ludoPenaltyTitle, Strings.ludoPenaltyBody(penalty.points, minutes), watchingAd)
    }

    private const val INVOLVED = 2
    private const val MILLIS = 1_000L
    private const val SECONDS_PER_MINUTE = 60L
}
