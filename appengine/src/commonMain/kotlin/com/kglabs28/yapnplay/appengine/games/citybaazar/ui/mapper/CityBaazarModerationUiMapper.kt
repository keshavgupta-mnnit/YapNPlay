package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.mapper

import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.moderation.domain.Penalty
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ReportOutcome
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ReportVoteRequest
import com.kglabs28.yapnplay.appengine.ui.components.moderation.ReportPersonUi
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarMatch
import com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match.CbModal
import kotlinx.datetime.Instant
import kotlin.math.ceil

/** Report, vote and penalty dialogs for the City Baazar table. Pure functions. */
object CityBaazarModerationUiMapper {

    fun reportablePeople(match: CityBaazarMatch): List<ReportPersonUi> =
        match.players.filter { !it.isMe && !it.bankrupt }.map { ReportPersonUi(it.userId, it.name, Strings.ludoRolePlayer) } +
            match.spectators.filter { it.userId != match.viewerUserId }.map { ReportPersonUi(it.userId, it.name, Strings.ludoRoleSpectator) }

    fun voters(match: CityBaazarMatch): Int {
        val present = match.players.count { !it.bankrupt } + match.spectators.size
        return (present - INVOLVED).coerceAtLeast(0)
    }

    fun vote(request: ReportVoteRequest, now: Instant): CbModal.Vote {
        val seconds = ceil((request.expiresAt - now).inWholeMilliseconds.coerceAtLeast(0) / MILLIS.toDouble()).toInt()
        return CbModal.Vote(request.reportId, Strings.ludoVoteTitle, Strings.ludoVoteBody(request.reporterName, request.reportedName), Strings.ludoVoteSecondsLeft(seconds))
    }

    fun outcome(outcome: ReportOutcome): CbModal.ReportOutcome {
        val tally = Strings.ludoReportTally(outcome.agreed, outcome.disagreed, outcome.noResponse)
        return if (outcome.upheld) {
            CbModal.ReportOutcome(Strings.ludoReportUpheldTitle, Strings.ludoReportUpheldBody(outcome.reportedName, tally))
        } else {
            CbModal.ReportOutcome(Strings.ludoReportRejectedTitle, Strings.ludoReportRejectedBody(tally, outcome.required))
        }
    }

    fun penalty(penalty: Penalty, now: Instant, watchingAd: Boolean): CbModal.Penalty {
        val minutes = ceil((penalty.blockedUntil - now).inWholeSeconds.coerceAtLeast(0) / SECONDS_PER_MINUTE.toDouble()).toInt()
        return CbModal.Penalty(Strings.ludoPenaltyTitle, Strings.ludoPenaltyBody(penalty.points, minutes), watchingAd)
    }

    private const val INVOLVED = 2
    private const val MILLIS = 1_000L
    private const val SECONDS_PER_MINUTE = 60L
}
