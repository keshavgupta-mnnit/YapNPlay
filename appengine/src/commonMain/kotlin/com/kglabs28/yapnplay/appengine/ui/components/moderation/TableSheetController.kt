package com.kglabs28.yapnplay.appengine.ui.components.moderation

import com.kglabs28.yapnplay.appengine.core.audio.GameAudio
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ModerationRules
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ReportVoteRequest
import com.kglabs28.yapnplay.appengine.features.moderation.usecase.ModerationUseCases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlin.math.ceil

/**
 * The settings sheet for a table that has no modal system of its own
 * (MONO, Complete Chaos), working as Royal Ludo's does: sound effects and
 * music on or off, Report (pick a person; the table votes), a report to
 * vote on when someone else reports, and the outcome. A view model keeps
 * one, starts it with its match, and shows [current] on every render;
 * [onChange] asks it to render again.
 */
class TableSheetController(
    private val moderation: ModerationUseCases,
    private val audio: GameAudio,
    private val scope: CoroutineScope,
    private val onChange: () -> Unit,
) {
    private var sheet: TableSheetUi? = null
    private var vote: ReportVoteRequest? = null
    /** Whether the open settings offer "End game": kept while sound or music is switched. */
    private var endGameAllowed = false

    /** Listens for reports to vote on and their outcomes. */
    fun start(): List<Job> = listOf(
        scope.launch { moderation.voteRequests().collect { vote = it; onChange() } },
        scope.launch {
            moderation.outcomes().collect { outcome ->
                val tally = LudoStrings.ludoReportTally(outcome.agreed, outcome.disagreed, outcome.noResponse)
                sheet = if (outcome.upheld) {
                    TableSheetUi.Outcome(LudoStrings.ludoReportUpheldTitle, LudoStrings.ludoReportUpheldBody(outcome.reportedName, tally))
                } else {
                    TableSheetUi.Outcome(LudoStrings.ludoReportRejectedTitle, LudoStrings.ludoReportRejectedBody(tally, outcome.required))
                }
                onChange()
            }
        },
    )

    /** What to show now: a report waiting for the user's vote first, then their own sheet. */
    fun current(now: Instant): TableSheetUi? {
        val request = vote?.takeIf { now < it.expiresAt }
        if (request == null) vote = null
        return request?.let {
            val seconds = ceil((it.expiresAt - now).inWholeMilliseconds.coerceAtLeast(0) / MILLIS).toInt()
            TableSheetUi.Vote(it.reportId, LudoStrings.ludoVoteTitle, LudoStrings.ludoVoteBody(it.reporterName, it.reportedName), LudoStrings.ludoVoteSecondsLeft(seconds))
        } ?: sheet
    }

    /** The settings; [canEndGame] adds "End game (vote)". */
    fun openSettings(canEndGame: Boolean = endGameAllowed) {
        val s = audio.settings.value
        endGameAllowed = canEndGame
        sheet = TableSheetUi.Settings(effectsOn = s.effectsOn, musicOn = s.musicOn, canEndGame = canEndGame)
        onChange()
    }

    fun toggleEffects() {
        audio.setEffectsOn(!audio.settings.value.effectsOn)
        openSettings()
    }

    fun toggleMusic() {
        audio.setMusicOn(!audio.settings.value.musicOn)
        openSettings()
    }

    /** Report: [people] who may be reported, [voters] who would decide. */
    fun openReport(people: List<ReportPersonUi>, voters: Int, selected: String? = null, error: String? = null) {
        sheet = TableSheetUi.Report(
            people = people,
            selectedUserId = selected,
            warning = LudoStrings.ludoReportWarning(ModerationRules.requiredAgreements(voters)),
            error = error ?: if (ModerationRules.canReport(voters)) null else LudoStrings.ludoReportNeedPeople,
        )
        onChange()
    }

    fun selectReportPerson(userId: String, voters: Int) {
        val report = sheet as? TableSheetUi.Report ?: return
        openReport(report.people, voters, selected = userId)
    }

    /** Sends the report on match [matchId]; closes on success, says why not otherwise. */
    suspend fun submitReport(matchId: String, voters: Int) {
        val report = sheet as? TableSheetUi.Report ?: return
        val person = report.people.firstOrNull { it.userId == report.selectedUserId } ?: return
        when (moderation.report(matchId, person.userId, person.name, voters)) {
            is AppResult.Success -> close()
            is AppResult.Failure -> openReport(report.people, voters, report.selectedUserId, LudoStrings.ludoReportNeedPeople)
        }
    }

    suspend fun vote(agree: Boolean) {
        val request = vote ?: return
        vote = null
        onChange()
        moderation.castVote(request.reportId, agree)
    }

    fun close() {
        sheet = null
        onChange()
    }
}

private const val MILLIS = 1_000.0
