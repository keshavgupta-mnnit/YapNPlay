package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components

import com.kglabs28.yapnplay.appengine.ui.components.seat.SeatHandoverDialog
import com.kglabs28.yapnplay.appengine.ui.components.seat.EndGameConfirmDialog
import com.kglabs28.yapnplay.appengine.ui.components.moderation.EndVoteCard
import com.kglabs28.yapnplay.appengine.ui.components.seat.LeaveMatchDialog
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.players.tint
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoMatchModal
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton
import com.kglabs28.yapnplay.appengine.ui.components.moderation.MatchSettingsCard
import com.kglabs28.yapnplay.appengine.ui.components.moderation.PenaltyCard
import com.kglabs28.yapnplay.appengine.ui.components.moderation.ReportPlayerCard
import com.kglabs28.yapnplay.appengine.ui.components.moderation.ReportVoteCard

/**
 * The table's dialogs: leave confirmation, final ranking, mic
 * permission, settings, report a player, vote on someone's report, the
 * report's outcome and a penalty.
 */
@Composable
fun LudoMatchModalContent(
    modal: LudoMatchModal,
    actions: LudoMatchModalActions,
    modifier: Modifier = Modifier,
) {
    val onDismiss = actions.onDismiss
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    val ctaHeight = YapNPlayTheme.dimens.ctaHeight

    when (modal) {
        is LudoMatchModal.LeaveConfirm -> LeaveMatchDialog(
            onStay = onDismiss,
            onLeave = actions.onConfirmLeave,
            message = modal.body,
            countdown = modal.countdown,
            options = modal.seatOptions,
            selectedIndex = modal.selectedIndex,
            onSelect = actions.onPickSeatTaker,
            modifier = modifier,
        )
        is LudoMatchModal.Result -> ModalCard(
            title = LudoStrings.ludoResultTitle,
            modifier = modifier,
            actions = {
                PillButton(
                    text = LudoStrings.ludoDone,
                    onClick = actions.onDone,
                    gradient = listOf(colors.CtaGreenStart, colors.CtaGreenMid, colors.CtaGreenEnd),
                    contentColor = colors.CtaGreenContent,
                    leadingIcon = Icons.Rounded.EmojiEvents,
                    trailingIcon = null,
                    textStyle = typography.sectionTitle,
                )
            },
        ) {
            modal.rows.forEach { row ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
                    BasicText(text = row.place, style = typography.title.copy(color = colors.Gold))
                    Box(Modifier.size(YapNPlayTheme.dimens.statusDot).background(row.colour.tint(), CircleShape))
                    BasicText(text = row.name, style = typography.title.copy(color = colors.TextPrimary))
                }
            }
        }
        LudoMatchModal.MicPermissionNeeded -> ModalCard(
            title = Strings.micPermissionTitle,
            subtitle = Strings.micPermissionBody,
            onClose = onDismiss,
            modifier = modifier,
            actions = {
                PillButton(
                    text = Strings.micPermissionOpenSettings,
                    onClick = actions.onOpenAppSettings,
                    gradient = listOf(colors.CtaGreenStart, colors.CtaGreenEnd),
                    contentColor = colors.CtaGreenContent,
                    leadingIcon = Icons.Rounded.Settings,
                    trailingIcon = null,
                    textStyle = typography.sectionTitle,
                )
            },
        )
        is LudoMatchModal.Settings -> MatchSettingsCard(
            effectsOn = modal.effectsOn,
            musicOn = modal.musicOn,
            onToggleEffects = actions.onToggleEffects,
            onToggleMusic = actions.onToggleMusic,
            onReport = actions.onOpenReport,
            onEndGame = actions.onProposeEnd.takeIf { modal.canEndGame },
            endGameLabel = TableStrings.endGameButton,
            onClose = onDismiss,
            rules = LudoStrings.ludoRules,
            modifier = modifier,
        )
        is LudoMatchModal.EndVote -> EndVoteCard(vote = modal.vote, onVote = actions.onEndVote, modifier = modifier)
        LudoMatchModal.ConfirmEnd -> EndGameConfirmDialog(onCancel = actions.onDismiss, onEnd = actions.onConfirmEnd, modifier = modifier)
        is LudoMatchModal.Report -> ReportPlayerCard(
            options = modal.people.map { LudoStrings.ludoReportOption(it.name, it.role) },
            selectedIndex = modal.people.indexOfFirst { it.userId == modal.selectedUserId }.takeIf { it >= 0 },
            warning = modal.warning,
            error = modal.error,
            onSelect = { index -> actions.onSelectReportPerson(modal.people[index].userId) },
            onSubmit = actions.onSubmitReport,
            onClose = onDismiss,
            modifier = modifier,
        )
        is LudoMatchModal.Vote -> ReportVoteCard(
            title = modal.title,
            body = modal.body,
            secondsLeft = modal.secondsLeft,
            onAgree = { actions.onVote(true) },
            onDisagree = { actions.onVote(false) },
            modifier = modifier,
        )
        is LudoMatchModal.ReportOutcome -> ModalCard(
            title = modal.title,
            subtitle = modal.body,
            onClose = onDismiss,
            modifier = modifier,
        )
        is LudoMatchModal.Penalty -> PenaltyCard(
            title = modal.title,
            body = modal.body,
            watchingAd = modal.watchingAd,
            onWatchAd = actions.onWatchAd.takeIf { modal.adOffered },
            onClose = onDismiss,
            modifier = modifier,
        )
        is LudoMatchModal.Seat -> SeatHandoverDialog(modal.modal, actions.onSeat, modifier)
    }
}

