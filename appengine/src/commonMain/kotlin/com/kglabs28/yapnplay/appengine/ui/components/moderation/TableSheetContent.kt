package com.kglabs28.yapnplay.appengine.ui.components.moderation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard

/** The card for [sheet]: settings, report, a vote, or an outcome (the same cards Royal Ludo shows). */
@Composable
fun TableSheetContent(
    sheet: TableSheetUi,
    onToggleEffects: () -> Unit,
    onToggleMusic: () -> Unit,
    onOpenReport: () -> Unit,
    onSelectReportPerson: (String) -> Unit,
    onSubmitReport: () -> Unit,
    onVote: (Boolean) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (sheet) {
        is TableSheetUi.Settings -> MatchSettingsCard(
            effectsOn = sheet.effectsOn,
            musicOn = sheet.musicOn,
            onToggleEffects = onToggleEffects,
            onToggleMusic = onToggleMusic,
            onReport = onOpenReport,
            onClose = onClose,
            modifier = modifier,
        )
        is TableSheetUi.Report -> ReportPlayerCard(
            options = sheet.people.map { LudoStrings.ludoReportOption(it.name, it.role) },
            selectedIndex = sheet.people.indexOfFirst { it.userId == sheet.selectedUserId }.takeIf { it >= 0 },
            warning = sheet.warning,
            error = sheet.error,
            onSelect = { index -> onSelectReportPerson(sheet.people[index].userId) },
            onSubmit = onSubmitReport,
            onClose = onClose,
            modifier = modifier,
        )
        is TableSheetUi.Vote -> ReportVoteCard(
            title = sheet.title,
            body = sheet.body,
            secondsLeft = sheet.secondsLeft,
            onAgree = { onVote(true) },
            onDisagree = { onVote(false) },
            modifier = modifier,
        )
        is TableSheetUi.Outcome -> ModalCard(title = sheet.title, subtitle = sheet.body, onClose = onClose, modifier = modifier)
    }
}
