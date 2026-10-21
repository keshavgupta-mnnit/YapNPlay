package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.mapper

import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import com.kglabs28.yapnplay.appengine.features.moderation.domain.Penalty
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ReportOutcome
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ReportVoteRequest
import com.kglabs28.yapnplay.appengine.ui.components.moderation.ReportPersonUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarMatch
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbModal
import kotlinx.datetime.Instant
import kotlin.math.ceil

/** Report, vote and penalty dialogs for the City Bazaar table. Pure functions. */
object CityBazaarModerationUiMapper {

    fun reportablePeople(match: CityBazaarMatch): List<ReportPersonUi> =
        match.players.filter { !it.isMe && !it.bankrupt }.map { ReportPersonUi(it.userId, it.name, LudoStrings.ludoRolePlayer) } +
            match.spectators.filter { it.userId != match.viewerUserId }.map { ReportPersonUi(it.userId, it.name, LudoStrings.ludoRoleSpectator) }

    fun voters(match: CityBazaarMatch): Int {
        val present = match.players.count { !it.bankrupt } + match.spectators.size
        return (present - INVOLVED).coerceAtLeast(0)
    }

    fun vote(request: ReportVoteRequest, now: Instant): CbModal.Vote {
        val seconds = ceil((request.expiresAt - now).inWholeMilliseconds.coerceAtLeast(0) / MILLIS.toDouble()).toInt()
        return CbModal.Vote(request.reportId, LudoStrings.ludoVoteTitle, LudoStrings.ludoVoteBody(request.reporterName, request.reportedName), LudoStrings.ludoVoteSecondsLeft(seconds))
    }

    fun outcome(outcome: ReportOutcome): CbModal.ReportOutcome {
        val tally = LudoStrings.ludoReportTally(outcome.agreed, outcome.disagreed, outcome.noResponse)
        return if (outcome.upheld) {
            CbModal.ReportOutcome(LudoStrings.ludoReportUpheldTitle, LudoStrings.ludoReportUpheldBody(outcome.reportedName, tally))
        } else {
            CbModal.ReportOutcome(LudoStrings.ludoReportRejectedTitle, LudoStrings.ludoReportRejectedBody(tally, outcome.required))
        }
    }

    fun penalty(penalty: Penalty, now: Instant, watchingAd: Boolean): CbModal.Penalty {
        val minutes = ceil((penalty.blockedUntil - now).inWholeSeconds.coerceAtLeast(0) / SECONDS_PER_MINUTE.toDouble()).toInt()
        return CbModal.Penalty(LudoStrings.ludoPenaltyTitle, LudoStrings.ludoPenaltyBody(penalty.points, minutes), watchingAd)
    }

    private const val INVOLVED = 2
    private const val MILLIS = 1_000L
    private const val SECONDS_PER_MINUTE = 60L
}
